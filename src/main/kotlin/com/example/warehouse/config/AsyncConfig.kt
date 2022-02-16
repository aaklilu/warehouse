package com.example.warehouse.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor
import java.util.concurrent.Executor

@Configuration
class AsyncConfig {

    companion object {
        private const val MAX_POOL_SIZE = 10
        private const val CORE_POOL_SIZE = 4
        private const val QUEUE_CAPACITY = 50
        private val logger = LoggerFactory.getLogger(AsyncConfig::class.java)
    }

    @Bean
    fun baseTaskExecutor() = ThreadPoolTaskExecutor().apply {
        maxPoolSize = MAX_POOL_SIZE
        corePoolSize = CORE_POOL_SIZE
        setQueueCapacity(QUEUE_CAPACITY)
        setWaitForTasksToCompleteOnShutdown(true)
        setThreadNamePrefix("Async-")
    }

    @Bean(name = [AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME])
    fun taskExecutor(): Executor {
        logger.info("Creating Async Task Executor")
        return DelegatingSecurityContextExecutor(baseTaskExecutor())
    }
}
