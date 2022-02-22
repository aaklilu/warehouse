package com.example.warehouse

import com.example.warehouse.order.models.CustomerDto
import org.awaitility.kotlin.await
import org.awaitility.kotlin.withPollInterval
import org.awaitility.pollinterval.FixedPollInterval
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.TimeUnit

val admin = User("admin", "password", emptyList())

val customerDto = CustomerDto(name = "test", phone = "123", address = "Amsterdam 123")


fun <T> TestRestTemplate.awaitUntilAndGet(
    duration: Duration = Duration.of(10, ChronoUnit.SECONDS),
    path: String, clazz: Class<T>,
    condition: (response: T?) -> Boolean): T {
    var result: T? = null
    ((await withPollInterval FixedPollInterval.fixed(5, TimeUnit.SECONDS))
        .atMost(duration)).until {
            result = this
                .getForEntity(path, clazz).body
            condition(result)
        }

    return result!!
}


fun <T> TestRestTemplate.getById(path: String, id: UUID, clazz: Class<T>): ResponseEntity<T>? {
    return this.getForEntity("$path/$id", clazz)
}