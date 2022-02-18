package com.example.warehouse.inventory.api.v1

import com.example.warehouse.inventory.article.models.ArticleDto
import com.example.warehouse.inventory.data.Article

internal fun ArticleDto.toArticle() = Article(
    articleId = this.artId,
    name = this.name,
    stockLevel = this.stock
)

internal fun Article.toArticleDto() = ArticleDto(
    artId = this.articleId,
    name = this.name,
    stock = this.stockLevel
)
