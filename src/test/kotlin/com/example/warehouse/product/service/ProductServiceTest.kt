package com.example.warehouse.product.service

import com.example.warehouse.product.data.ProductRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class ProductServiceTest {
    private lateinit var productService: ProductService

    @MockK
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setup() {
        productService = ProductService(productRepository)
        every { productRepository.updateProductArticleStockLevels(any(), any()) } returns Unit
    }

    @Test
    fun `updates stock level for product articles`() {
        productService.updateProductArticleStockLevel("999", 2)
        verify {
            productRepository.updateProductArticleStockLevels(
                withArg {
                    assertEquals("999", it)
                },
                withArg {
                    assertEquals(2, it)
                }
            )
        }
    }
}
