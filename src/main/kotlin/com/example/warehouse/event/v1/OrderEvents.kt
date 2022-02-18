package com.example.warehouse.event.v1

import java.util.UUID

data class OrderCreatedEvent(val orderId: UUID, val orderArticles: List<OrderEventArticle>)
data class OrderInitiatedEvent(val orderId: UUID, val lineItems: List<OrderEventLineItem>)

data class OrderEventArticle(val articleId: String, val quantity: Int)
data class OrderEventLineItem(val productId: String, val quantity: Int)
