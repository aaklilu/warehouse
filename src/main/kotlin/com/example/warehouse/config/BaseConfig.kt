package com.example.warehouse.config

import org.springframework.context.annotation.Import

@Import(value = [AsyncConfig::class, DatasourceConfig::class, HttpSecurityConfig::class])
open class BaseConfig
