package com.example.warehouse.inventory.data

import com.example.warehouse.BaseIntegrationTest
import com.example.warehouse.inventory.config.InventoryConfig
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

@Import(value = [InventoryConfig::class])
class ArticleRepositoryTest(@Autowired private val articleRepository: ArticleRepository) : BaseIntegrationTest() {
    @Test
    fun `given article entity, persistence works`() {
        val article = articleRepository.save(
            Article(
                articleId = "1",
                name = "test",
                stockLevel = 1
            )
        )

        assertNotNull(article)
        assertNotNull(article.id)
    }
}
