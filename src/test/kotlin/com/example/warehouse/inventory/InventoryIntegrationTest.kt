package com.example.warehouse.inventory

import com.example.warehouse.BaseIntegrationTest
import com.example.warehouse.admin
import com.example.warehouse.inventory.article.models.ArticleDto
import com.example.warehouse.inventory.config.InventoryConfig
import com.example.warehouse.inventory.data.ArticleRepository
import com.example.warehouse.inventory.models.InventoryArticlesDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.awaitility.kotlin.await
import org.awaitility.kotlin.withPollInterval
import org.awaitility.pollinterval.FixedPollInterval
import java.time.Duration
import java.util.concurrent.TimeUnit

@Import(value = [InventoryConfig::class])
class InventoryIntegrationTest(
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val testRestTemplate: TestRestTemplate
) : BaseIntegrationTest() {

    private val headers = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    private val mapper = jacksonObjectMapper()

    @Test
    fun `inventory articles are successfully processed in bulk`() {
        val inventoryJson = ClassPathResource("inventory.json").file.readText()
        val status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(
                "/api/v1/inventory",
                HttpEntity(inventoryJson, headers), String::class.java
            ).statusCode

        assertEquals(HttpStatus.CREATED, status)

        mapper.readValue<InventoryArticlesDto>(inventoryJson).let { expected ->
            ((await withPollInterval FixedPollInterval.fixed(5, TimeUnit.SECONDS))
                .atMost(Duration.of(10, ChronoUnit.SECONDS))).untilAsserted {
                    val articles = articleRepository.findAll()
                    assertTrue(
                        expected.inventory.all { article -> articles.any { it.articleId == article.artId } }
                    )
                }
        }
    }

    @Test
    fun `single inventory article is created`() {
        val articleJson = ClassPathResource("article.json").file.readText()
        val status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(
                "/api/v1/inventory/articles",
                HttpEntity(articleJson, headers), String::class.java
            ).statusCode

        assertEquals(HttpStatus.OK, status)

        mapper.readValue<ArticleDto>(articleJson).let { expected ->
            assertTrue(
                articleRepository.findAll().any { article -> expected.artId == article.articleId }
            )
        }
    }
}
