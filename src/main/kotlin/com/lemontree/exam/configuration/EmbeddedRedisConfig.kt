package com.lemontree.exam.configuration

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class EmbeddedRedisConfig {

    private val redisPort: Int = 6379

    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        redisServer = RedisServer(redisPort)
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().address = "redis://127.0.0.1:$redisPort"
        return Redisson.create(config)
    }
}
