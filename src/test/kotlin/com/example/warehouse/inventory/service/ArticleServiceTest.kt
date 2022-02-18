package com.example.warehouse.inventory.service

import com.example.warehouse.event.v1.InventoryLevelChangedEvent
import com.example.warehouse.inventory.data.ArticleRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
internal class ArticleServiceTest {
    private lateinit var articleService: ArticleService

    @MockK
    private lateinit var articleRepository: ArticleRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @BeforeEach
    fun setup() {
        articleService = ArticleService(articleRepository, applicationEventPublisher)
        every { articleRepository.findByArticleId(TEST_ARTICLE_ID) } returns testArticle
        every { articleRepository.save(any()) } returns testArticle
        every { applicationEventPublisher.publishEvent(any<InventoryLevelChangedEvent>()) } returns Unit
    }

    @Test
    fun `given valid delta, when decrease stock, stock level is decreased by delta`() {
        articleService.decreaseStock(TEST_ARTICLE_ID, 2)

        verify {
            articleRepository.save(
                withArg {
                    assertEquals(testArticle.stockLevel - 2, it.stockLevel)
                }
            )
        }
        verify {
            applicationEventPublisher.publishEvent(
                withArg<InventoryLevelChangedEvent> {
                    assertEquals(TEST_ARTICLE_ID, it.subjectId)
                    assertEquals(testArticle.stockLevel - 2, it.stockLevel)
                }
            )
        }
    }

    @Test
    fun `given negative delta, when decrease stock, stock level is not affected`() {
        assertFailsWith<IllegalArgumentException>(
            message = "Stock level delta must be positive",
            block = {
                articleService.decreaseStock(TEST_ARTICLE_ID, -2)
            }
        )
    }

    @Test
    fun `given zero delta, when decrease stock, stock level is not affected`() {
        assertFailsWith<IllegalArgumentException>(
            message = "Stock level delta must be positive",
            block = {
                articleService.decreaseStock(TEST_ARTICLE_ID, 0)
            }
        )
    }

    @Test
    fun `given delta greater than existing stock, when decrease stock, stock level is not affected`() {
        articleService.decreaseStock(TEST_ARTICLE_ID, 999)

        verify(exactly = 0) {
            articleRepository.save(any())
        }
        verify(exactly = 0) {
            applicationEventPublisher.publishEvent(any())
        }
    }
}
