package com.example.warehouse.product.api.v1

import com.example.warehouse.BaseControllerTest
import com.example.warehouse.product.data.Product
import com.example.warehouse.product.data.ProductArticle
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ProductController::class)
class ProductControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var productService: ProductService

    @Test
    @WithMockUser(username = "admin", password = "password", roles = ["ADMIN"])
    fun `when POST a product, then STATUS 200 is returned`() {
        every { productService.createProduct(any()) } returns Product(
            id = UUID.randomUUID(),
            name = "Dining Chair"
        ).also {
            it.articles = listOf(ProductArticle(articleId = "1", amountOf = 4))
        }
        val articleJson = ClassPathResource("product.json").file.readText()

        mockMvc.perform(
            post("/api/v1/products")
                .content(articleJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Dining Chair"))
            .andExpect(jsonPath("$.contain_articles[0].art_id").value("1"))
            .andExpect(jsonPath("$.contain_articles[0].amount_of").value("4"))
    }

    @Test
    fun `when unauthenticated user POST a product, then STATUS 401 is returned`() {
        val articleJson = ClassPathResource("product.json").file.readText()

        mockMvc.perform(
            post("/api/v1/products")
                .content(articleJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser
    fun `when unauthorized user POST a product, then STATUS 403 is returned`() {
        val articleJson = ClassPathResource("product.json").file.readText()

        mockMvc.perform(
            post("/api/v1/products")
                .content(articleJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }
}
