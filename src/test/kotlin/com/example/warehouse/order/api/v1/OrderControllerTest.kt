package com.example.warehouse.order.api.v1

import com.example.warehouse.BaseControllerTest
import com.example.warehouse.order.data.Customer
import com.example.warehouse.order.data.LineItem
import com.example.warehouse.order.data.Order
import com.example.warehouse.order.service.OrderService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(OrderController::class)
class OrderControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var orderService: OrderService

    @Test
    fun `when POST a product, then STATUS 200 is returned`() {
        every { orderService.createOrder(any()) } returns Order(
            id = UUID.randomUUID(),
            name = "test_",
            createdAt = LocalDateTime.now(),
            customer = Customer(name = "test", phone = "012", address = "Amsterdam 123")
        ).also {
            it.lineItems = listOf(
                LineItem(
                    productName = "Dinning Chair",
                    productId = "6ec538e9-0052-4e96-89c5-32eae4b03037",
                    quantity = 2
                )
            )
        }
        val orderJson = ClassPathResource("order.json").file.readText()

        mockMvc.perform(
            post("/api/v1/orders")
                .content(orderJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("test_"))
            .andExpect(jsonPath("$.customer.name").value("test"))
            .andExpect(jsonPath("$.customer.phone").value("012"))
            .andExpect(jsonPath("$.customer.address").value("Amsterdam 123"))
            .andExpect(jsonPath("$.line_items[0].product_name").value("Dinning Chair"))
            .andExpect(jsonPath("$.line_items[0].product_id").value("6ec538e9-0052-4e96-89c5-32eae4b03037"))
            .andExpect(jsonPath("$.line_items[0].quantity").value("2"))
    }
}
