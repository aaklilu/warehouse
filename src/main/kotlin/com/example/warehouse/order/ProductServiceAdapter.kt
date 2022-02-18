package com.example.warehouse.order

import com.example.warehouse.product.models.ProductDto
import java.util.UUID

interface ProductServiceAdapter {
    fun getProduct(id: UUID): ProductDto
}
