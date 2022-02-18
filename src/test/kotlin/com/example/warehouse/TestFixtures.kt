package com.example.warehouse

import com.example.warehouse.order.models.CustomerDto
import org.springframework.security.core.userdetails.User

val admin = User("admin", "password", emptyList())
val user = User("user", "password", emptyList())

val customerDto = CustomerDto(name = "test", phone = "123", address = "Amsterdam 123")
