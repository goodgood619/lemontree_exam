package com.lemontree.exam.domain.request

data class LoginRequest(
    val email: String,
    val password: String
)