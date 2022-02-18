package com.example.warehouse.inventory.service

import com.example.warehouse.event.v1.InventoryLevelChangedEvent
import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.data.ArticleRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.UUID
import javax.transaction.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Async
    @Transactional
    fun createArticles(articles: List<Article>) {
        articleRepository.saveAll(articles)
    }

    @Transactional
    fun createArticle(articles: Article): Article {
        return articleRepository.save(articles)
    }

    fun getArticle(id: UUID): Article {
        return articleRepository.getById(id)
    }

    fun findArticles(pageable: Pageable): Page<Article> {
        return articleRepository.findAll(pageable)
    }

    @Transactional
    fun decreaseStock(articleId: String, delta: Int) {
        require(delta > 0) { "Stock level delta must be positive" }
        articleRepository.findByArticleId(articleId).let {
            if (delta <= it.stockLevel) {
                it.stockLevel = it.stockLevel - delta
                articleRepository.save(it)
                applicationEventPublisher.publishEvent(InventoryLevelChangedEvent(it.articleId, it.stockLevel))
            }
        }
    }
}
