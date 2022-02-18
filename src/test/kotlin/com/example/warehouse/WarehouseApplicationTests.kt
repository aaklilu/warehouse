package com.example.warehouse

import com.example.warehouse.inventory.data.ArticleRepository
import com.example.warehouse.order.models.LineItemDto
import com.example.warehouse.order.models.OrderDto
import com.example.warehouse.product.data.Product
import com.example.warehouse.product.data.ProductRepository
import org.awaitility.kotlin.await
import org.awaitility.kotlin.withPollInterval
import org.awaitility.pollinterval.FixedPollInterval
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Tag("integration")
class WarehouseApplicationTests(
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val testRestTemplate: TestRestTemplate
) {
    companion object {
        @Container
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }
    }

    private val headers = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    @AfterEach
    @BeforeEach
    fun setup() {
        articleRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun `given product catalog and inventory, when product is sold, stock level and available products are reduced`() {
        val inventoryJson = ClassPathResource("inventory.json").file.readText()
        var status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(
                "/api/v1/inventory",
                HttpEntity(inventoryJson, headers), String::class.java
            ).statusCode

        assertEquals(HttpStatus.CREATED, status)

        val productsJson = ClassPathResource("products.json").file.readText()
        status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(
                "/api/v1/product-catalogue",
                HttpEntity(productsJson, headers), String::class.java
            ).statusCode

        assertEquals(HttpStatus.CREATED, status)
        var products = emptyList<Product>()
        (
            (await withPollInterval FixedPollInterval.fixed(5, TimeUnit.SECONDS))
                .atMost(Duration.of(10, ChronoUnit.SECONDS))
            ).untilAsserted {
            products = productRepository.findAll()
            assertTrue(products.isNotEmpty())
        }

        val dinningChair = products.find { it.name == "Dining Chair" }!!
        val dinningTable = products.find { it.name == "Dinning Table" }!!
        val orderDto = OrderDto(
            customer = customerDto,
            lineItems = listOf(
                LineItemDto(productId = dinningChair.id.toString(), 1),
                LineItemDto(productId = dinningTable.id.toString(), 1)
            )
        )
        val response = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(
                "/api/v1/orders",
                HttpEntity(orderDto, headers), OrderDto::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        (
            (await withPollInterval FixedPollInterval.fixed(5, TimeUnit.SECONDS))
                .atMost(Duration.of(30, ChronoUnit.SECONDS))
            ).untilAsserted {
            val leg = articleRepository.findByArticleId("1")
            val screw = articleRepository.findByArticleId("2")
            val seat = articleRepository.findByArticleId("3")
            val tableTop = articleRepository.findByArticleId("4")

            assertEquals(4, leg.stockLevel) // stock(12) - (chair(4) + table (4)) = 4
            assertEquals(8, screw.stockLevel) // stock(24) - (chair(8) + table (8)) = 8
            assertEquals(1, seat.stockLevel) // stock(2) - (chair(1) + table (0)) = 1
            assertEquals(0, tableTop.stockLevel) // stock(1) - (chair(0) + table (1)) = 0

            val updatedChair = productRepository.getById(dinningChair.id!!)
            val updatedTable = productRepository.getById(dinningTable.id!!)

            assertEquals(1, updatedChair.availableQuantity)
            assertEquals(0, updatedTable.availableQuantity)
        }
    }
}
