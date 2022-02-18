package com.example.warehouse.order.service

import com.example.warehouse.Failure
import com.example.warehouse.ProcessResult
import com.example.warehouse.Success
import com.example.warehouse.event.v1.OrderCreatedEvent
import com.example.warehouse.event.v1.OrderEventArticle
import com.example.warehouse.event.v1.OrderInitiatedEvent
import com.example.warehouse.order.ProductServiceAdapter
import com.example.warehouse.order.data.Order
import com.example.warehouse.order.data.OrderArticle
import com.example.warehouse.order.data.OrderRepository
import com.example.warehouse.order.data.OrderStatus
import com.example.warehouse.otherwise
import com.example.warehouse.then
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.UUID

open class OrderPostProcessor(
    private val orderRepository: OrderRepository,
    private val productServiceAdapter: ProductServiceAdapter,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    companion object {
        private val logger = LoggerFactory.getLogger(OrderPostProcessor::class.java)
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    open fun process(event: OrderInitiatedEvent) {
        findOrder(event.orderId) then
            ::decorate then
            ::validate then
            ::save then
            ::publishEvent otherwise ::handleError
    }

    private fun findOrder(orderId: UUID): ProcessResult<Order> {
        return orderRepository.findByIdOrNull(orderId).let {
            if (it == null) Failure(null, "Order not found") else Success(it)
        }
    }

    private fun decorate(order: Order): ProcessResult<Order> {
        return with(order) {
            orderArticles = lineItems.asSequence().flatMap { lineItem ->
                productServiceAdapter
                    .getProduct(UUID.fromString(lineItem.productId)).also {
                        lineItem.productName = it.name
                        lineItem.unitPrice = it.price
                    }.containArticles
            }.groupBy { it.artId }.map { grouped ->
                OrderArticle(articleId = grouped.key, amountOf = grouped.value.sumOf { it.amountOf })
            }
            Success(this)
        }
    }

    private fun validate(order: Order): ProcessResult<Order> {
        return Success(order) // TODO validate available stock
    }

    private fun save(order: Order): ProcessResult<Order> {
        return Success(orderRepository.save(order))
    }

    private fun publishEvent(order: Order): ProcessResult<Order> {
        applicationEventPublisher.publishEvent(
            OrderCreatedEvent(
                orderId = order.id!!,
                orderArticles = order.orderArticles.map { OrderEventArticle(it.articleId, it.amountOf) }
            )
        )
        return Success(order)
    }

    private fun handleError(order: Order?, errorMessage: String) {
        logger.error("Failed to process order {}", errorMessage)
        order?.let {
            it.status = OrderStatus.FAILED
            it.statusMessage = errorMessage
            orderRepository.save(order)
        }
    }
}
