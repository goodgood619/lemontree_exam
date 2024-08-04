package com.lemontree.exam.domain.request

import java.math.BigDecimal
import java.util.*

data class PayBackReversalRequest(
    val currency: Currency,
    val amount: BigDecimal,
    val payBackNumber: Int,
)
