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
    name = "line_item",
    indexes = [
        Index(name = "idx_line_item___product_id", columnList = "productId"),
        Index(name = "idx_line_item___order_id", columnList = "order_id")
    ]
)
class LineItem(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(nullable = false) var productId: String,
    var productName: String? = null,
    @Column(nullable = false) var quantity: Int,
    var unitPrice: Double? = null
) {

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order? = null
}
