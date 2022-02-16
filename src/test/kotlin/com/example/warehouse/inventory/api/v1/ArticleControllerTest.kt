package com.example.warehouse.inventory.api.v1

import com.example.warehouse.BaseControllerTest
import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.service.ArticleService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.UUID

class ArticleControllerTest(@Autowired private val mockMvc: MockMvc) : BaseControllerTest() {
    @MockkBean
    private lateinit var articleService: ArticleService

    @Test
    @WithMockUser(username = "admin", password = "password", roles = ["ADMIN"])
    fun `when POST an article, then STATUS 200 is returned`() {
        every { articleService.createArticle(any()) } returns Article(
            id = UUID.randomUUID(),
            name = "leg",
            stockLevel = 12,
            articleId = "1",
            createdDate = LocalDateTime.now()
        )
        val articleJson = ClassPathResource("article.json").file.readText()

        mockMvc.perform(
            post("/api/v1/inventory/articles")
                .content(articleJson).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.art_id").value("1"))
            .andExpect(jsonPath("$.name").value("leg"))
            .andExpect(jsonPath("$.stock").value("12"))
    }
}
