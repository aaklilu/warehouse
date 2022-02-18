package com.example.warehouse

import com.example.warehouse.config.HttpSecurityConfig
import org.springframework.context.annotation.Import

@Import(value = [HttpSecurityConfig::class])
open class BaseControllerTest
