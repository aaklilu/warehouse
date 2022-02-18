package com.example.warehouse.order.service

import com.example.warehouse.event.v1.OrderEventLineItem
import com.example.warehouse.order.data.Customer
import com.example.warehouse.order.data.LineItem
import com.example.warehouse.order.data.Order
import com.example.warehouse.product.models.ContainArticleDto
import com.example.warehouse.product.models.ProductDto
import java.util.UUID

val firstProductId = UUID.randomUUID()
val secondProductId = UUID.randomUUID()
val orderId = UUID.randomUUID()

val order = Order(
    id = orderId,
    customer = Customer(name = "test", phone = "123", address = "123"),
).also {
    it.lineItems = listOf(
        LineItem(
            productId = firstProductId.toString(),
            quantity = 2
        ),
        LineItem(
            productId = secondProductId.toString(),
            quantity = 1
        )
    )
}

val firstOrderLineItem = OrderEventLineItem(firstProductId.toString(), 2)
val secondOrderLineItem = OrderEventLineItem(firstProductId.toString(), 1)

val firstProductDto = ProductDto(
    id = firstProductId,
    name = "Dinning Chair",
    price = 2.0,
    containArticles = listOf(
        ContainArticleDto(
            artId = "1",
            amountOf = 4
        ),
        ContainArticleDto(
            artId = "2",
            amountOf = 10
        )
    )
)

val secondProductDto = ProductDto(
    id = firstProductId,
    name = "Stool",
    price = 2.0,
    containArticles = listOf(
        ContainArticleDto(
            artId = "1",
            amountOf = 3
        ),
        ContainArticleDto(
            artId = "4",
            amountOf = 2
        )
    )
)
