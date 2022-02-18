package com.example.warehouse.product.service

import com.example.warehouse.product.data.Product
import com.example.warehouse.product.data.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import java.util.UUID
import javax.transaction.Transactional

open class ProductService(
    private val productRepository: ProductRepository
) {
    @Async
    @Transactional
    open fun createProducts(product: List<Product>) {
        productRepository.saveAll(product)
    }

    @Transactional
    open fun createProduct(product: Product) = productRepository.save(product)

    fun getProduct(id: UUID) = productRepository.getById(id)

    fun findProducts(pageable: Pageable) = productRepository.findAll(pageable)

    @Transactional
    open fun updateProductArticleStockLevel(articleId: String, stockLevel: Int) {
        productRepository.updateProductArticleStockLevels(articleId, stockLevel)
    }
}
