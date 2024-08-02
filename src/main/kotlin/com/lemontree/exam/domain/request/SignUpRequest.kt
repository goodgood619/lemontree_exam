package com.lemontree.exam.domain.request

data class SignUpRequest(
    val email: String,
    val nickName: String,
    val password: String,
) {
}