package com.example.warehouse.product.api.v1

import com.example.warehouse.product.models.ProductCatalogueDto
import com.example.warehouse.product.models.ProductDto
import com.example.warehouse.product.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/product-catalogue")
class ProductCatalogueController(@Autowired private val productService: ProductService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProducts(@RequestBody @Validated productCatalogueDto: ProductCatalogueDto) {
        productService.createProducts(productCatalogueDto.products.map(ProductDto::toProduct))
    }
}
