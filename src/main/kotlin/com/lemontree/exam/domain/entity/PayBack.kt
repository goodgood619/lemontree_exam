package com.lemontree.exam.domain.entity

import com.lemontree.exam.domain.request.PayBackRequest
import com.lemontree.exam.domain.type.PayBackType
import com.lemontree.exam.util.Utils
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "payback")
class PayBack(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Enumerated
    val type: PayBackType,
    val currency: Currency,
    val authorizationAmount: BigDecimal,
    val amount: BigDecimal,
    val authorizationNumber: Int, // 승인 번호
    val payBackNumber: Int, // payBack 번호
    @ManyToOne(fetch = FetchType.LAZY)
    val authorization: Authorization
): BaseEntity() {
    companion object {

        fun of(payBackRequest: PayBackRequest, authorization: Authorization): PayBack = with(payBackRequest) {
            return PayBack(
                type = PayBackType.PAYBACK,
                currency = currency,
                authorizationAmount = amount,
                amount = payBackAmount,
                authorizationNumber = authorization.authorizationNumber,
                payBackNumber = Utils.generateRandomSixDigitNumber(),
                authorization = authorization
            )
        }
    }
}