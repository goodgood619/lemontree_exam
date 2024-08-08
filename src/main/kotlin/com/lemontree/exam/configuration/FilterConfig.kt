package com.lemontree.exam.configuration

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class FilterConfig {

    @Bean
    fun timeoutFilterRegistration(timeoutFilter: TimeoutFilter): FilterRegistrationBean<TimeoutFilter> {
        val registrationBean = FilterRegistrationBean<TimeoutFilter>()
        registrationBean.filter = timeoutFilter

        // 특정 경로에만 필터 적용
        registrationBean.addUrlPatterns(
            "/user/authorize/*",
            "/user/reverse/*",
            "/user/payback/*",
            "/user/payback/reverse/*"
        )


        return registrationBean
    }
}