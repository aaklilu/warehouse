package com.example.warehouse.product.api.v1

import com.example.warehouse.BaseControllerTest
import com.example.warehouse.product.service.ProductService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProductCatalogueController::class)
class ProductCatalogueControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var productService: ProductService

    @Test
    @WithMockUser(username = "admin", password = "password", roles = ["ADMIN"])
    fun `when POST product catalogue, then STATUS 201 is returned`() {
        every { productService.createProducts(any()) } returns Unit
        val productsJson = ClassPathResource("products.json").file.readText()

        mockMvc.perform(
            post("/api/v1/product-catalogue")
                .content(productsJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
    }
}
