package com.example.warehouse.order.api.v1

import com.example.warehouse.order.data.Customer
import com.example.warehouse.order.data.LineItem
import com.example.warehouse.order.data.Order
import com.example.warehouse.order.models.CustomerDto
import com.example.warehouse.order.models.LineItemDto
import com.example.warehouse.order.models.OrderDto
import com.example.warehouse.order.models.OrderStatusDto

internal fun OrderDto.toOrder() = Order(
    name = this.name,
    customer = Customer(this.customer.name, this.customer.phone, this.customer.address),
).also {
    it.lineItems = this.lineItems.map(LineItemDto::toLineItem)
}

internal fun Order.toOrderDto() = OrderDto(
    id = this.id,
    name = this.name!!,
    status = OrderStatusDto.valueOf(this.status.name),
    statusMessage = this.statusMessage,
    customer = CustomerDto(this.customer.name, this.customer.phone, this.customer.address),
    lineItems = this.lineItems.map(LineItem::toLineItemDto)
)

internal fun LineItemDto.toLineItem() = LineItem(
    productId = this.productId,
    quantity = this.quantity
)

internal fun LineItem.toLineItemDto() = LineItemDto(
    productId = this.productId,
    productName = this.productName,
    unitPrice = this.unitPrice,
    quantity = this.quantity
)
