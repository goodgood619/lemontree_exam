package com.lemontree.exam.domain.request

import java.math.BigDecimal
import java.util.*

data class PayBackRequest(
    val currency: Currency,
    val amount: BigDecimal,
    val payBackAmount: BigDecimal,
    val authorizationNumber: Int,
)