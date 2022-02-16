package com.example.warehouse.inventory.service

import com.example.warehouse.inventory.data.Article
import java.util.UUID

val TEST_ARTICLE_ID = UUID.randomUUID()
val testArticle: Article
    get() = Article(
        id = TEST_ARTICLE_ID,
        name = "Test",
        stockLevel = 10,
        articleId = "2"
    )
