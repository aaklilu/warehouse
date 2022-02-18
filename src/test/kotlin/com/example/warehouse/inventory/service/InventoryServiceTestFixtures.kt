package com.example.warehouse.inventory.service

import com.example.warehouse.inventory.data.Article
import java.util.UUID

const val TEST_ARTICLE_ID = "9999"
val testArticle: Article
    get() = Article(
        id = UUID.randomUUID(),
        name = "Test",
        stockLevel = 10,
        articleId = TEST_ARTICLE_ID
    )
