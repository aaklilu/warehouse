package com.example.warehouse.order.data

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
    name = "product_order_article",
    indexes = [
        Index(name = "idx_order_article__article_id", columnList = "articleId", unique = true),
        Index(name = "idx_order_article__order_id", columnList = "order_id")
    ]
)
class OrderArticle(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(nullable = false) var articleId: String,
    @Column(nullable = false) var amountOf: Int = 0
) {
    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order? = null
}
