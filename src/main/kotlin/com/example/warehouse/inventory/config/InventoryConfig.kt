package com.example.warehouse.inventory.config

import com.example.warehouse.config.BaseConfig
import com.example.warehouse.inventory.data.ArticleRepository
import com.example.warehouse.inventory.service.ArticleService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InventoryConfig(
    private val articleRepository: ArticleRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : BaseConfig() {

    @Bean
    fun articleService() = ArticleService(articleRepository, applicationEventPublisher)
}
