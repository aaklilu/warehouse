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
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(OrderController::class)
class OrderControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var orderService: OrderService

    @Test
    fun `when POST an order, then STATUS 200 is returned`() {
        every { orderService.createOrder(any()) } returns Order(
            id = UUID.randomUUID(),
            name = "test_",
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

    @Test
    fun `when GET an order, then STATUS 200 is returned`() {
        val orderId = UUID.randomUUID()
        every { orderService.getOrder(orderId) } returns Order(
            id = orderId,
            name = "test_",
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

        mockMvc.perform(
            get("/api/v1/orders/$orderId")
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

    @Test
    @WithMockUser(username = "admin", password = "password", roles = ["ADMIN"])
    fun `when an admin requests for all Orders, then STATUS 200 is returned`() {
        val orderId = UUID.randomUUID()
        every { orderService.findOrders(any()) } returns PageImpl(
            listOf(Order(
            id = orderId,
            name = "test_",
            customer = Customer(name = "test", phone = "012", address = "Amsterdam 123")
            ).also {
            it.lineItems = listOf(
                LineItem(
                    productName = "Dinning Chair",
                    productId = "6ec538e9-0052-4e96-89c5-32eae4b03037",
                    quantity = 2
                )
            )
        }))

        mockMvc.perform(
            get("/api/v1/orders")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items[0].name").value("test_"))
            .andExpect(jsonPath("$.items[0].customer.name").value("test"))
            .andExpect(jsonPath("$.items[0].customer.phone").value("012"))
            .andExpect(jsonPath("$.items[0].customer.address").value("Amsterdam 123"))
            .andExpect(jsonPath("$.items[0].line_items[0].product_name").value("Dinning Chair"))
            .andExpect(jsonPath("$.items[0].line_items[0].product_id").value("6ec538e9-0052-4e96-89c5-32eae4b03037"))
            .andExpect(jsonPath("$.items[0].line_items[0].quantity").value("2"))
    }

    @Test
    fun `when an unauthenticated user requests for all Orders, then STATUS 401 is returned`() {
        mockMvc.perform(
            get("/api/v1/orders")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser
    fun `when an unauthorized user requests for all Orders, then STATUS 403 is returned`() {
        mockMvc.perform(
            get("/api/v1/orders")
        )
            .andExpect(status().isForbidden)
    }
}
