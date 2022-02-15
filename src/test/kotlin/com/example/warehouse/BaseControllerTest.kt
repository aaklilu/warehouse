package com.example.warehouse

import com.example.warehouse.config.HttpSecurityConfig
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser

@Import(value = [HttpSecurityConfig::class])
@WebMvcTest
@WithMockUser(username = "user", password = "password", roles = ["USER"])
open class BaseControllerTest
