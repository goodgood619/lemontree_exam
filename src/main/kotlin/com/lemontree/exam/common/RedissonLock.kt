package com.lemontree.exam.common

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedissonLock(val keyPrefix: String, val timeout: Long = 5L)

