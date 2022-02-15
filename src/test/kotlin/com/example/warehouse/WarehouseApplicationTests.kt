package com.example.warehouse

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class WarehouseApplicationTests : BaseIntegrationTest() {
    companion object {
        private val logger = LoggerFactory.getLogger(WarehouseApplicationTests::class.java)
    }

    @Test
    fun `context loads`() {
        logger.info("Context loads")
    }
}
