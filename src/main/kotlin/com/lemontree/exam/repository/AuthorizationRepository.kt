package com.lemontree.exam.repository

import com.lemontree.exam.domain.entity.Authorization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.Currency

@Repository
interface AuthorizationRepository : JpaRepository<Authorization, Long> {
    fun findByCurrencyAndAmountAndAuthorizationNumber(currency: Currency, amount: BigDecimal, authorizationNumber: Int): List<Authorization>
}