package com.lemontree.exam.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(apiInfo())

    private fun apiInfo() = Info()
        .title("Swagger")
        .description("Spring doc을 이용한 Swagger UI")
        .version("1.0.0")
}