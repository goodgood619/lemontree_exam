package com.lemontree.exam.domain.response

data class GeneralResponse<T>(
    val code: Int = 200,
    val message: String = "ok",
    val data: T? = null
)