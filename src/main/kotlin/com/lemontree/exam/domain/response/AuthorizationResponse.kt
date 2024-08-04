package com.lemontree.exam.domain.response

import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.type.AuthorizationType
import java.math.BigDecimal
import java.util.*

data class AuthorizationResponse(
    val type: AuthorizationType,
    val currency: Currency,
    val amount: BigDecimal,
    val authorizationNumber: Int, // 승인 번호
    val cardAcceptorName: String, // 가맹점
    val cardAcceptorCode: String, // 가맹점 코드
) {
    companion object {
        fun from(authorization: Authorization) = with(authorization) {
            AuthorizationResponse(
                type = type,
                currency = currency,
                amount = amount,
                authorizationNumber = authorizationNumber,
                cardAcceptorName = cardAcceptorName,
                cardAcceptorCode = cardAcceptorCode
            )
        }
    }
}
