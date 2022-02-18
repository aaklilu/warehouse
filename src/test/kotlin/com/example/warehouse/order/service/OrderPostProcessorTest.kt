package com.example.warehouse.order.service

import com.example.warehouse.event.v1.OrderCreatedEvent
import com.example.warehouse.event.v1.OrderEventArticle
import com.example.warehouse.event.v1.OrderInitiatedEvent
import com.example.warehouse.order.ProductServiceAdapter
import com.example.warehouse.order.data.OrderArticle
import com.example.warehouse.order.data.OrderRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
internal class OrderPostProcessorTest {
    private lateinit var orderPostProcessor: OrderPostProcessor

    @MockK
    private lateinit var orderRepository: OrderRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var productServiceAdapter: ProductServiceAdapter

    @BeforeEach
    fun setup() {
        orderPostProcessor = OrderPostProcessor(orderRepository, productServiceAdapter, applicationEventPublisher)
        every { orderRepository.save(any()) } returns order
        every { productServiceAdapter.getProduct(firstProductId) } returns firstProductDto
        every { productServiceAdapter.getProduct(secondProductId) } returns secondProductDto
        every { applicationEventPublisher.publishEvent(any<OrderCreatedEvent>()) } returns Unit
    }

    @Test
    fun `given valid order, processed successfully`() {
        val expectedOrder = order.apply {
            orderArticles = listOf(firstProductDto, secondProductDto)
                .flatMap { productDto ->
                    productDto.containArticles
                }.groupBy { it.artId }
                .map { group -> OrderArticle(articleId = group.key, amountOf = group.value.sumOf { it.amountOf }) }
        }

        every { orderRepository.findByIdOrNull(orderId) } returns expectedOrder

        orderPostProcessor.process(OrderInitiatedEvent(orderId, listOf(firstOrderLineItem, secondOrderLineItem)))

        verify {
            orderRepository.save(
                withArg {
                    assertEquals(order.customer.name, it.customer.name)
                    assertEquals(order.customer.phone, it.customer.phone)
                    assertEquals(order.customer.address, it.customer.address)
                    assertTrue(it.lineItems.any { item -> item.productId == firstProductId.toString() })
                    assertTrue(it.lineItems.any { item -> item.productId == secondProductId.toString() })
                    assertTrue(it.lineItems.any { item -> item.unitPrice == firstProductDto.price })
                    assertTrue(it.lineItems.any { item -> item.unitPrice == secondProductDto.price })
                    assertTrue(it.lineItems.any { item -> item.productName == firstProductDto.name })
                    assertTrue(it.lineItems.any { item -> item.productName == secondProductDto.name })
                    assertTrue(it.orderArticles.isNotEmpty())
                    assertTrue(it.orderArticles.containsAll(expectedOrder.orderArticles))
                }
            )
        }
        val orderEventArticles = expectedOrder.orderArticles.map {
            OrderEventArticle(articleId = it.articleId, quantity = it.amountOf)
        }

        verify {
            applicationEventPublisher.publishEvent(
                withArg<OrderCreatedEvent> {
                    assertEquals(order.id, it.orderId)
                    assertTrue(it.orderArticles.containsAll(orderEventArticles))
                }
            )
        }
    }
}
