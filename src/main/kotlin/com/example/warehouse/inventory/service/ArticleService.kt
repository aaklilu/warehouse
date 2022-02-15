package com.example.warehouse.inventory.service

import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.data.ArticleRepository

class ArticleService(private val articleRepository: ArticleRepository) {
    /**
     * Creates articles in bulk.
     * TODO Make it async for better performance and responsiveness
     */
    fun createArticles(articles: List<Article>): List<Article> {
        return articleRepository.saveAll(articles)
    }

    fun createArticle(articles: Article): Article {
        return articleRepository.save(articles)
    }
}
