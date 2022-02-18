package com.example.warehouse.inventory.service

import com.example.warehouse.event.v1.OrderCreatedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

open class InventoryEventListener(private val articleService: ArticleService) {

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    open fun handleOrderCreatedEvent(orderCreatedEvent: OrderCreatedEvent) {
        orderCreatedEvent.orderArticles.forEach { articleService.decreaseStock(it.articleId, it.quantity) }
    }
}
