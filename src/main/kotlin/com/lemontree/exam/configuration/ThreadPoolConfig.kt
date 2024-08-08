package com.lemontree.exam.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class ThreadPoolConfig {

    @Bean
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 16 // 논리적 코어수 2배
        executor.maxPoolSize = 32 // 논리적 코어수 4배
        executor.queueCapacity = 500
        executor.setThreadNamePrefix("Timeout-")
        executor.initialize()
        return executor
    }
}