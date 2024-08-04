package com.lemontree.exam.domain.response

import java.math.BigDecimal

data class AuthorizationReversalResponse(
    val balance: BigDecimal
) {

    companion object {
        fun from(balance: BigDecimal) = AuthorizationReversalResponse(balance = balance)
    }
}
