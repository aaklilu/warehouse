package com.example.warehouse.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = [
        "com.example.warehouse.inventory.data",
        "com.example.warehouse.order.data",
        "com.example.warehouse.product.data",
    ]
)
class DatasourceConfig
