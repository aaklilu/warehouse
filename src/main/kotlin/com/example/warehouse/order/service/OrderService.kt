package com.example.warehouse.order.service

import com.example.warehouse.event.v1.OrderEventLineItem
import com.example.warehouse.event.v1.OrderInitiatedEvent
import com.example.warehouse.order.data.Order
import com.example.warehouse.order.data.OrderRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun createOrder(order: Order) = orderRepository.save(order).also {
        applicationEventPublisher.publishEvent(
            OrderInitiatedEvent(
                orderId = it.id!!,
                lineItems = it.lineItems.map { lineItem ->
                    OrderEventLineItem(lineItem.productId, lineItem.quantity)
                }
            )
        )
    }
    fun getOrder(id: UUID) = orderRepository.getById(id)
    fun findOrders(pageable: Pageable) = orderRepository.findAll(pageable)

    @Transactional
    fun deleteAll() {
        orderRepository.deleteAll()
    }
}
