package com.example.warehouse.product.config

import com.example.warehouse.config.BaseConfig
import com.example.warehouse.product.data.ProductRepository
import com.example.warehouse.product.service.ProductEventListener
import com.example.warehouse.product.service.ProductService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductConfig(
    private val productRepository: ProductRepository
) : BaseConfig() {
    @Bean
    fun productService() = ProductService(productRepository)

    @Bean
    fun productEventListener() = ProductEventListener(productService())
}
