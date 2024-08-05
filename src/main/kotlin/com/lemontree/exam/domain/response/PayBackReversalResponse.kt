package com.lemontree.exam.domain.response

import java.math.BigDecimal

data class PayBackReversalResponse(
    val balance: BigDecimal
) {
    companion object {
        fun from(balance: BigDecimal) = PayBackReversalResponse(balance = balance)
    }
}
