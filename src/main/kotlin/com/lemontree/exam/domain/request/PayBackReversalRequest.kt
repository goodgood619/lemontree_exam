package com.lemontree.exam.domain.request

import java.math.BigDecimal
import java.util.*

data class PayBackReversalRequest(
    val currency: Currency,
    val authorizationNumber: Int,
    val amount: BigDecimal,
    val payBackAmount: BigDecimal,
    val payBackNumber: Int,
)
