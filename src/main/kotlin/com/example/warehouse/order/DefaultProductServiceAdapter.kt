package com.example.warehouse.order

import com.example.warehouse.product.api.v1.toProductDto
import com.example.warehouse.product.service.ProductService
import java.util.UUID

class DefaultProductServiceAdapter(private val productService: ProductService) : ProductServiceAdapter {
    override fun getProduct(id: UUID) = productService.getProduct(id).toProductDto()
}
