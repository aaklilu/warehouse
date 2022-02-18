package com.example.warehouse.order.api.v1

import com.example.warehouse.order.data.Order
import com.example.warehouse.order.models.OrderDto
import com.example.warehouse.order.models.OrdersPageDto
import com.example.warehouse.order.models.PageDto
import com.example.warehouse.order.service.OrderService
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
@RequestMapping("/api/v1/orders")
class OrderController(@Autowired private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody @Validated orderDto: OrderDto): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.createOrder(orderDto.toOrder()).let(Order::toOrderDto))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable("id") id: UUID): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.getOrder(id).let(Order::toOrderDto))
    }

    @GetMapping
    fun findOrders(@PageableDefault(page = 0, size = 100) page: Pageable,): ResponseEntity<OrdersPageDto> {
        val pageRequest = PageRequest.of(
            page.pageNumber, page.pageSize,
            page.getSortOr(
                Sort.by("createdAt").descending()
            )
        )
        val orders = orderService.findOrders(pageRequest)
        return ResponseEntity.ok(
            OrdersPageDto(
                items = orders.content.map(Order::toOrderDto),
                page = PageDto(
                    pageNumber = orders.number,
                    pageSize = page.pageSize,
                    totalElements = orders.totalElements,
                    totalPages = orders.totalPages
                )
            )
        )
    }
}
