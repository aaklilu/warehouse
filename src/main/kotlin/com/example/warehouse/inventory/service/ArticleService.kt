package com.example.warehouse.inventory.service

import com.example.warehouse.inventory.InventoryLevelChangedEvent
import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.data.ArticleRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import java.util.UUID
import javax.transaction.Transactional

open class ArticleService(
    private val articleRepository: ArticleRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Async
    open fun createArticles(articles: List<Article>): List<Article> {
        return articleRepository.saveAll(articles)
    }

    @Transactional
    open fun createArticle(articles: Article): Article {
        return articleRepository.save(articles)
    }

    @Transactional
    open fun decreaseStock(id: UUID, delta: Int) {
        require(delta > 0) { "Stock level delta must be positive" }
        articleRepository.getById(id).let {
            if (delta <= it.stockLevel) {
                it.stockLevel = it.stockLevel - delta
                articleRepository.save(it)
                applicationEventPublisher.publishEvent(InventoryLevelChangedEvent(it.id!!, it.stockLevel))
            }
        }
    }
}
