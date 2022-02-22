package com.example.warehouse.product.api.v1

import com.example.warehouse.product.data.Product
import com.example.warehouse.product.data.ProductArticle
import com.example.warehouse.product.models.ContainArticleDto
import com.example.warehouse.product.models.ProductDto

internal fun ProductDto.toProduct() = Product(
    name = this.name,
    price = this.price
).apply {
    this.articles = containArticles.map(ContainArticleDto::toProductArticle)
}

internal fun Product.toProductDto() = ProductDto(
    id = this.id,
    name = this.name,
    price = this.price,
    availableQuantity = this.availableQuantity,
    containArticles = this.articles.map(ProductArticle::toContainArticleDto)
)

internal fun ContainArticleDto.toProductArticle() = ProductArticle(
    articleId = this.artId,
    amountOf = this.amountOf
)

internal fun ProductArticle.toContainArticleDto() = ContainArticleDto(
    artId = this.articleId!!,
    amountOf = this.amountOf
)
