package com.example.warehouse.product.data

import org.hibernate.annotations.Formula
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(
    name = "product",
    indexes = [
        Index(name = "idx_product___name", columnList = "name")
    ]
)
class Product(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var price: Double = 0.0
) {

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var articles: List<ProductArticle> = listOf()
        set(value) {
            field = value.map { it.product = this; it }
        }

    @Formula("(select min(pa.stock_level/pa.amount_of) from product_article as pa where pa.product_id = id)")
    val availableQuantity: Int = 0

    @CreatedDate
    var createdAt: LocalDateTime? = null
}
