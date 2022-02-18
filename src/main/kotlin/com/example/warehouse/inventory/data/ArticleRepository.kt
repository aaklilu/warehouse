package com.example.warehouse.inventory.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArticleRepository : JpaRepository<Article, UUID> {
    fun findByArticleId(articleId: String): Article
}
