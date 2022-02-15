package com.example.warehouse.inventory.api.v1

import com.example.warehouse.BaseControllerTest
import com.example.warehouse.inventory.service.ArticleService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InventoryControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var articleService: ArticleService

    @Test
    @WithMockUser(username = "admin", password = "password", roles = ["ADMIN"])
    fun `when POST bulk inventory articles, then STATUS 201 is returned`() {
        every { articleService.createArticles(any()) } returns listOf()

        mockMvc.perform(
            post("/api/v1/inventory")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "password"))
                .content(inventoryJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
    }
}
