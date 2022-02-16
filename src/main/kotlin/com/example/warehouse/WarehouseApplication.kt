package com.example.warehouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class WarehouseApplication

fun main(args: Array<String>) {
    runApplication<WarehouseApplication>(args = args)
}
