package com.lemontree.exam.repository

import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.entity.PayBack
import com.lemontree.exam.domain.entity.QAuthorization
import com.lemontree.exam.domain.entity.QAuthorization.authorization
import com.lemontree.exam.domain.entity.QPayBack.payBack
import com.lemontree.exam.domain.entity.QUser
import com.lemontree.exam.domain.entity.QUser.user
import com.lemontree.exam.domain.type.AuthorizationType
import com.lemontree.exam.domain.type.PayBackType
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface PayBackRepository : JpaRepository<PayBack, Long>, PayBackQueryService {
}

interface PayBackQueryService {
    fun findByTypeAndCurrencyAndAuthorizationNumber(userId: Long, amount: BigDecimal, currency: Currency, authorizationNumber: Int, payBackNumber: Int): MutableList<PayBack>

}

class PayBackQueryServiceImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : PayBackQueryService {
    override fun findByTypeAndCurrencyAndAuthorizationNumber(
        userId: Long,
        amount: BigDecimal,
        currency: Currency,
        authorizationNumber: Int,
        payBackNumber: Int
    ): MutableList<PayBack> {

        return jpaQueryFactory.selectFrom(payBack)
            .join(payBack.authorization, authorization).on(authorization.authorizationNumber.eq(authorizationNumber))
            .join(authorization.user, user).on(user.id.eq(userId))
            .where(
                payBack.payBackNumber.eq(payBackNumber)
                    .and(payBack.amount.eq(amount))
                    .and(payBack.currency.eq(currency))
            )
            .fetch()
    }
}