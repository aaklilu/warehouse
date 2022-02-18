package com.example.warehouse.order.data

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Customer(
    @Column(name = "o_customer_name", nullable = false) val name: String,
    @Column(name = "o_customer_phone", nullable = false) val phone: String,
    @Column(name = "o_customer_address", nullable = false) val address: String
)
