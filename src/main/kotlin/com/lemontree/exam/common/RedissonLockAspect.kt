package com.lemontree.exam.common

import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Component
class RedissonLockAspect(private val redissonClient: RedissonClient) {

    @Around("@annotation(redissonLock)")
    fun around(joinPoint: ProceedingJoinPoint, redissonLock: RedissonLock): Any? {
        // 고유한 락 키 생성
        val lockKey = generateLockKey(joinPoint, redissonLock.keyPrefix)
        val lock = redissonClient.getLock(lockKey)
        val timeout = redissonLock.timeout
        try {
            val isLocked = lock.tryLock(timeout, timeout, TimeUnit.SECONDS)
            if (!isLocked) {
                throw CustomException(ErrorCode.LOCK_ACQUIRE_FAIL)
            }
            return joinPoint.proceed()
        } finally {
            // 락 해제
            lock.unlock()
        }

    }

    private fun generateLockKey(joinPoint: ProceedingJoinPoint, keyPrefix: String): String {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val parameterNames = method.parameters.map { it.name }
        val parameterValues = joinPoint.args
        val keySuffix = parameterNames.zip(parameterValues).joinToString("_") { it.second.toString() }
        return "$keyPrefix:$keySuffix"
    }
}
