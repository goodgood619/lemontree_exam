package com.lemontree.exam.repository

import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.entity.QAuthorization.authorization
import com.lemontree.exam.domain.entity.QUser.user
import com.lemontree.exam.domain.type.AuthorizationType
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Currency

@Repository
interface AuthorizationRepository : JpaRepository<Authorization, Long>, AuthorizationQueryService {
    fun findByCurrencyAndAmountAndAuthorizationNumber(currency: Currency, amount: BigDecimal, authorizationNumber: Int): List<Authorization>

}

interface AuthorizationQueryService {
    fun findByTypeAndCurrencyAndAuthorizationNumber(userId: Long, type: AuthorizationType, amount: BigDecimal, currency: Currency, authorizationNumber: Int): MutableList<Authorization>

    fun findByCreatedAtBetweenAndType(userId: Long, from: LocalDateTime, to: LocalDateTime, type: AuthorizationType): MutableList<Authorization>
}

class AuthorizationQueryServiceImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : AuthorizationQueryService {
    override fun findByTypeAndCurrencyAndAuthorizationNumber(
        userId: Long,
        type: AuthorizationType,
        amount: BigDecimal,
        currency: Currency,
        authorizationNumber: Int
    ): MutableList<Authorization> {

        return jpaQueryFactory.selectFrom(authorization)
            .join(authorization.user, user).on(user.id.eq(userId))
            .where(
                authorization.authorizationNumber.eq(authorizationNumber)
                    .and(authorization.amount.eq(amount))
                    .and(authorization.currency.eq(currency))
                    .and(authorization.type.eq(type))
            )
            .fetch()
    }

    override fun findByCreatedAtBetweenAndType(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        type: AuthorizationType
    ): MutableList<Authorization> {
        return jpaQueryFactory.selectFrom(authorization)
            .join(authorization.user, user).on(user.id.eq(userId))
            .where(
                authorization.createdAt.between(from, to)
                    .and(authorization.type.eq(type))
            ).fetch()
    }
}