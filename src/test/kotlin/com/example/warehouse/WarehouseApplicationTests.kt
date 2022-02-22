package com.example.warehouse

import com.example.warehouse.inventory.article.models.ArticleDto
import com.example.warehouse.inventory.article.models.ArticlesPageDto
import com.example.warehouse.order.models.LineItemDto
import com.example.warehouse.order.models.OrderDto
import com.example.warehouse.product.models.ProductDto
import com.example.warehouse.product.models.ProductsPageDto
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Tag("integration")
class WarehouseApplicationTests(
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

        const val V1_INVENTORY_PATH = "/api/v1/inventory"
        const val V1_ARTICLES_PATH = "/api/v1/inventory/articles"
        const val V1_PRODUCT_CATALOGUE_PATH = "/api/v1/product-catalogue"
        const val V1_PRODUCTS_PATH = "/api/v1/products"
        const val V1_ORDERS_PATH = "/api/v1/orders"
    }

    private val headers = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    @AfterEach
    @BeforeEach
    fun setup() {
        testRestTemplate.delete(V1_ARTICLES_PATH)
        testRestTemplate.delete(V1_PRODUCTS_PATH)
        testRestTemplate.delete(V1_ORDERS_PATH)
    }

    @Test
    fun `given product catalog and inventory, when product is sold, stock level and available products are reduced`() {
        val inventoryJson = ClassPathResource("inventory.json").file.readText()
        var status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(V1_INVENTORY_PATH, HttpEntity(inventoryJson, headers), String::class.java).statusCode

        assertEquals(HttpStatus.CREATED, status)
        val articles = getArticles()

        val productsJson = ClassPathResource("products.json").file.readText()
        status = testRestTemplate
            .withBasicAuth(admin.username, admin.password)
            .postForEntity(V1_PRODUCT_CATALOGUE_PATH, HttpEntity(productsJson, headers), String::class.java).statusCode

        assertEquals(HttpStatus.CREATED, status)
        val products = getProducts()

        val dinningChair = products.find { it.name == "Dining Chair" }!!
        val dinningTable = products.find { it.name == "Dinning Table" }!!
        val orderDto = OrderDto(
            customer = customerDto,
            lineItems = listOf(
                LineItemDto(productId = dinningChair.id.toString(), 1),
                LineItemDto(productId = dinningTable.id.toString(), 1)
            )
        )
        val orderResponse = testRestTemplate
            .postForEntity(V1_ORDERS_PATH, HttpEntity(orderDto, headers), OrderDto::class.java)

        assertEquals(HttpStatus.OK, orderResponse.statusCode)
        assertEquals(
            orderResponse.body?.id,
            testRestTemplate.getById(V1_ORDERS_PATH, orderResponse.body?.id!!, OrderDto::class.java)?.body?.id
        )

        (
            (await withPollInterval FixedPollInterval.fixed(5, TimeUnit.SECONDS))
                .atMost(Duration.of(30, ChronoUnit.SECONDS))
            ).untilAsserted {
            val leg = testRestTemplate.withBasicAuth(admin.username, admin.password)
                .getById(V1_ARTICLES_PATH, articles.find { it.artId == "1" }?.id!!, ArticleDto::class.java)?.body
            val screw = testRestTemplate.withBasicAuth(admin.username, admin.password)
                .getById(V1_ARTICLES_PATH, articles.find { it.artId == "2" }?.id!!, ArticleDto::class.java)?.body
            val seat = testRestTemplate.withBasicAuth(admin.username, admin.password)
                .getById(V1_ARTICLES_PATH, articles.find { it.artId == "3" }?.id!!, ArticleDto::class.java)?.body
            val tableTop = testRestTemplate.withBasicAuth(admin.username, admin.password)
                .getById(V1_ARTICLES_PATH, articles.find { it.artId == "4" }?.id!!, ArticleDto::class.java)?.body

            assertEquals(4, leg?.stock) // stock(12) - (chair(4) + table (4)) = 4
            assertEquals(8, screw?.stock) // stock(24) - (chair(8) + table (8)) = 8
            assertEquals(1, seat?.stock) // stock(2) - (chair(1) + table (0)) = 1
            assertEquals(0, tableTop?.stock) // stock(1) - (chair(0) + table (1)) = 0

            val updatedChair = testRestTemplate
                .getForEntity("$V1_PRODUCTS_PATH/${dinningChair.id!!}", ProductDto::class.java).body
            val updatedTable = testRestTemplate
                .getForEntity("$V1_PRODUCTS_PATH/${dinningTable.id!!}", ProductDto::class.java).body

            assertEquals(1, updatedChair?.availableQuantity)
            assertEquals(0, updatedTable?.availableQuantity)
        }
    }

    private fun getArticles() = testRestTemplate
        .withBasicAuth(admin.username, admin.password)
        .awaitUntilAndGet(path = V1_ARTICLES_PATH, clazz = ArticlesPageDto::class.java) {
            it != null && it.items.orEmpty().isNotEmpty()
        }.items!!

    private fun getProducts() = testRestTemplate
        .awaitUntilAndGet(path = V1_PRODUCTS_PATH, clazz = ProductsPageDto::class.java) {
            it != null && it.items.orEmpty().isNotEmpty()
        }.items!!
}
