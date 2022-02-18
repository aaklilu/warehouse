package com.example.warehouse.product.data

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "product_article",
    indexes = [
        Index(name = "idx_product_article__product_article_id", columnList = "product_id,articleId", unique = true),
        Index(name = "idx_product_article__article_id", columnList = "articleId")
    ]
)
class ProductArticle(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(nullable = false) var articleId: String? = null,
    @Column(nullable = false) var amountOf: Int = 0,
    @Column(nullable = false) var stockLevel: Int = 0
) {
    @ManyToOne
    @JoinColumn(name = "product_id")
    var product: Product? = null
}
