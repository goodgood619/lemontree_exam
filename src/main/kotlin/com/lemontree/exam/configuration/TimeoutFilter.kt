package com.lemontree.exam.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class TimeoutFilter(
    private val taskExecutor: Executor
) : OncePerRequestFilter() {

    companion object : KLogging()


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val startTime = System.nanoTime()

        val future = (taskExecutor as ThreadPoolTaskExecutor).submit {
            filterChain.doFilter(request, response)
        }

        try {
            future.get(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, "Request timed out Exceed 5 seconds")
        } finally {
            calculateTotalRequestTime(startTime, request)
        }
    }

    private fun calculateTotalRequestTime(startTime: Long, request: HttpServletRequest) {
        val elapsed = (System.nanoTime() - startTime) / 1_000_000
        var reqLine = "${request.method} ${request.requestURI}"
        reqLine = if (request.queryString != null) "$reqLine?${request.queryString}" else reqLine

        if (request.requestURI != "/") {
            logger.info("[ACCESS_LOG] $reqLine - ($elapsed ms)")
        }
    }
}