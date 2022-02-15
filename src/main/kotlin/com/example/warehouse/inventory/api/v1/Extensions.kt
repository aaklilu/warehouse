package com.example.warehouse.inventory.api.v1

import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.models.ArticleDto

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
