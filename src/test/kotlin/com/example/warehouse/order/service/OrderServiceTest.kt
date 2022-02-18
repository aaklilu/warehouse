package com.example.warehouse.order.service

import com.example.warehouse.event.v1.OrderInitiatedEvent
import com.example.warehouse.order.data.OrderRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class OrderServiceTest {
    private lateinit var orderService: OrderService

    @MockK
    private lateinit var orderRepository: OrderRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @BeforeEach
    fun setup() {
        orderService = OrderService(orderRepository, applicationEventPublisher)
        every { orderRepository.save(any()) } returns order
        every { applicationEventPublisher.publishEvent(any<OrderInitiatedEvent>()) } returns Unit
    }

    @Test
    fun `when order is created, an order initiated event is published`() {
        orderService.createOrder(order)
        verify {
            orderRepository.save(
                withArg {
                    assertEquals("test", it.customer.name)
                }
            )
        }
        verify {
            applicationEventPublisher.publishEvent(
                withArg<OrderInitiatedEvent> {
                    assertEquals(order.id, it.orderId)
                }
            )
        }
    }
}
