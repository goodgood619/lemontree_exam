package com.lemontree.exam.domain.entity

import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.AuthorizationReversalRequest
import com.lemontree.exam.domain.type.AuthorizationType
import com.lemontree.exam.util.Utils.generateRandomSixDigitNumber
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Currency

@Entity
@Table(name = "authorization")
class Authorization(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Enumerated
    val type: AuthorizationType,
    val currency: Currency,
    val amount: BigDecimal,
    val authorizationNumber: Int, // 승인 번호
    val cardAcceptorName: String, // 가맹점
    val cardAcceptorCode: String, // 가맹점 코드
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User
) : BaseEntity() {

    companion object {
        fun of(authorizationRequest: AuthorizationRequest, user: User): Authorization = with(authorizationRequest) {
            return Authorization(
                type = AuthorizationType.AUTHORIZE,
                currency = currency,
                amount = amount,
                authorizationNumber = generateRandomSixDigitNumber(),
                cardAcceptorCode = cardAcceptorCode,
                cardAcceptorName = cardAcceptorName,
                user = user
            )
        }

        fun of(authorizationReversalRequest: AuthorizationReversalRequest, user: User,
               authorizationNumber: Int,
               cardAcceptorCode: String, cardAcceptorName: String): Authorization = with(authorizationReversalRequest) {
            return Authorization(
                type = AuthorizationType.AUTHORIZE_REVERSE,
                currency = currency,
                amount = amount,
                authorizationNumber = authorizationNumber,
                cardAcceptorCode = cardAcceptorCode,
                cardAcceptorName = cardAcceptorName,
                user = user
            )
        }
    }
}