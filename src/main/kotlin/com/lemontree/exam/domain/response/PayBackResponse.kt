package com.lemontree.exam.domain.response

import com.lemontree.exam.domain.entity.PayBack
import java.math.BigDecimal
import java.util.*

data class PayBackResponse(
    val currency: Currency,
    val amount: BigDecimal, // 원 승인 금액
    val payBackAmount: BigDecimal, // 페이백 금액
    val payBackNumber: Int, // payBack 번호
    val balance: BigDecimal,
) {

    companion object {

        fun of(payBack: PayBack, balance: BigDecimal): PayBackResponse = with(payBack) {

            return PayBackResponse(
                currency = currency,
                amount = authorizationAmount,
                payBackAmount = amount,
                payBackNumber = payBackNumber,
                balance = balance
            )
        }
    }
}