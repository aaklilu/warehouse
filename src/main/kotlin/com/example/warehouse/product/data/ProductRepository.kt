package com.example.warehouse.product.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ProductRepository : JpaRepository<Product, UUID> {
    @Modifying
    @Query(
        """
        update ProductArticle 
        set stockLevel = :level
        where articleId = :id
    """
    )
    fun updateProductArticleStockLevels(id: String, level: Int)
}
