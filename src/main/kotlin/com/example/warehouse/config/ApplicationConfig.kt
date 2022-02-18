package com.example.warehouse.config

import org.springdoc.core.SpringDocConfigProperties
import org.springdoc.core.SpringDocConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [AsyncConfig::class, DatasourceConfig::class, HttpSecurityConfig::class])
class ApplicationConfig {
    @Bean
    fun springDocConfiguration(): SpringDocConfiguration? {
        return SpringDocConfiguration()
    }

    @Bean
    fun springDocConfigProperties(): SpringDocConfigProperties? {
        return SpringDocConfigProperties()
    }
}
