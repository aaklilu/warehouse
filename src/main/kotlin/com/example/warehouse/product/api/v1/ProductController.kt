package com.example.warehouse.product.api.v1

import com.example.warehouse.product.data.Product
import com.example.warehouse.product.models.PageDto
import com.example.warehouse.product.models.ProductDto
import com.example.warehouse.product.models.ProductsPageDto
import com.example.warehouse.product.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/products")
class ProductController(@Autowired private val productService: ProductService) {

    @PostMapping
    fun createProduct(@RequestBody @Validated productDto: ProductDto): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.createProduct(productDto.toProduct()).let(Product::toProductDto))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable("id") id: UUID): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.getProduct(id).let(Product::toProductDto))
    }

    @GetMapping
    fun findProducts(@PageableDefault(page = 0, size = 100) page: Pageable,): ResponseEntity<ProductsPageDto> {
        val pageRequest = PageRequest.of(
            page.pageNumber, page.pageSize,
            page.getSortOr(
                Sort.by("createdAt").descending()
            )
        )
        val products = productService.findProducts(pageRequest)
        return ResponseEntity.ok(
            ProductsPageDto(
                items = products.content.map(Product::toProductDto),
                page = PageDto(
                    pageNumber = products.number,
                    pageSize = page.pageSize,
                    totalElements = products.totalElements,
                    totalPages = products.totalPages
                )
            )
        )
    }
}
