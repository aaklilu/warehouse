package com.example.warehouse.order.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Table

@Entity
@Table(
    name = "product_order",
    indexes = [
        Index(name = "idx_order___name", columnList = "name"),
        Index(name = "idx_order___o_customer_phone", columnList = "o_customer_phone")
    ]
)
class Order(
    @Id @GeneratedValue var id: UUID? = null,
    @Embedded var customer: Customer,
    @Column(nullable = false) var name: String? = null,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: OrderStatus = OrderStatus.INITIATED,
    var statusMessage: String? = null
) {

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var orderArticles: List<OrderArticle> = listOf()
        set(value) {
            field = value.map { it.order = this; it }
        }

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var lineItems: List<LineItem> = listOf()
        set(value) {
            field = value.map { it.order = this; it }
        }

    @PrePersist
    @PreUpdate
    fun setOrderName() {
        this.name = customer.name + lineItems.joinToString("_") { it.productName ?: "" }
    }

    @CreatedDate
    var createdAt: LocalDateTime? = null
}
