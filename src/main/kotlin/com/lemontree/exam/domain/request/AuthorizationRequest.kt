package com.lemontree.exam.domain.request

import java.math.BigDecimal
import java.util.*

data class AuthorizationRequest(
    val currency: Currency,
    val amount: BigDecimal,
    val cardAcceptorName: String,
    val cardAcceptorCode: String,
)
