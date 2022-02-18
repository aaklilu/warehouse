package com.example.warehouse.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@EnableWebSecurity
class HttpSecurityConfig {
    @Bean
    fun userDetailsService(): UserDetailsService {
        val users: User.UserBuilder = User.withDefaultPasswordEncoder()
        val manager = InMemoryUserDetailsManager()
        manager.createUser(users.username("user").password("password").roles("USER").build())
        manager.createUser(users.username("admin").password("password").roles("USER", "ADMIN").build())
        return manager
    }

    @Configuration
    class ApiWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/api/v1")
                authorizeRequests {
                    authorize(HttpMethod.POST, "/inventory/articles", hasRole("ADMIN"))
                    authorize(HttpMethod.GET, "/inventory/articles", permitAll)
                    authorize(HttpMethod.POST, "/inventory", hasRole("ADMIN"))
                    authorize(HttpMethod.POST, "/product-catalogue", hasRole("ADMIN"))
                    authorize(HttpMethod.POST, "/products", hasRole("ADMIN"))
                    authorize(HttpMethod.GET, "/products", permitAll)
                    authorize("/orders/{\\d+}", permitAll)
                    authorize(HttpMethod.POST, "/orders", permitAll)
                    authorize(HttpMethod.GET, "/orders", authenticated)
                    authorize(anyRequest, authenticated)
                }
                httpBasic { }
            }
        }
    }
}
