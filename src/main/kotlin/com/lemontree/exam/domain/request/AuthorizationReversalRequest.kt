package com.lemontree.exam.domain.request

import java.math.BigDecimal
import java.util.*

// 부분 취소는 고려하지 않았음
data class AuthorizationReversalRequest(
    val currency: Currency,
    val amount: BigDecimal,
    val authorizationNumber: Int,
)
