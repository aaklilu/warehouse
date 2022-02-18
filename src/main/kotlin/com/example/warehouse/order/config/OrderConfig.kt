package com.example.warehouse.order.config

import com.example.warehouse.config.BaseConfig
import com.example.warehouse.order.DefaultProductServiceAdapter
import com.example.warehouse.order.data.OrderRepository
import com.example.warehouse.order.service.OrderPostProcessor
import com.example.warehouse.order.service.OrderService
import com.example.warehouse.product.data.ProductRepository
import com.example.warehouse.product.service.ProductService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrderConfig(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : BaseConfig() {

    @Bean
    fun orderService() = OrderService(orderRepository, applicationEventPublisher)

    @Bean
    fun productServiceAdapter() = DefaultProductServiceAdapter(ProductService(productRepository))

    @Bean
    fun orderPostProcessor() = OrderPostProcessor(orderRepository, productServiceAdapter(), applicationEventPublisher)
}
