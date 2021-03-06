package com.example.warehouse.product.service

import com.example.warehouse.event.v1.InventoryLevelChangedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductEventListener(private val productService: ProductService) {

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleInventoryLevelChangedEvent(event: InventoryLevelChangedEvent) = productService
        .updateProductArticleStockLevel(event.subjectId, event.stockLevel)
}
