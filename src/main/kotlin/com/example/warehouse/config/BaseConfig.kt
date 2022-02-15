package com.example.warehouse.config

import org.springframework.context.annotation.Import

@Import(value = [DatasourceConfig::class, HttpSecurityConfig::class])
open class BaseConfig
