package com.lemontree.exam.service

import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.type.LimitAmountType
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import com.lemontree.exam.repository.LimitAmountPolicyRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class LimitAmountService(
    private val limitAmountPolicyRepository: LimitAmountPolicyRepository,
) {

    fun checkAmount(currentAmount: BigDecimal, list: MutableList<Authorization>) {
        val limitAmountPolicyList = limitAmountPolicyRepository.findAll()

        val currentDateTime = LocalDateTime.now()
        val startOfDay = currentDateTime.toLocalDate().atStartOfDay()
        val oneMonthAgo = currentDateTime.minus(1, ChronoUnit.MONTHS).toLocalDate().atStartOfDay()

        // 1회 결제 한도 체크
        val onceLimitAmount = limitAmountPolicyList.first { it.type == LimitAmountType.ONCE }
            .limitAmount

        if (currentAmount > onceLimitAmount) {
            throw CustomException(ErrorCode.ONCE_LIMIT_AMOUNT_EXCEED)
        }

        // 1회 결제 한도 체크
        val dayAuthorizationAmount = list.filter { it.createdAt >= startOfDay && it.createdAt <= currentDateTime }
            .sumOf { it.amount }

        val dayLimitAmount = limitAmountPolicyList.first { it.type == LimitAmountType.DAY }
            .limitAmount

        if (currentAmount.add(dayAuthorizationAmount) > dayLimitAmount) {
            throw CustomException(ErrorCode.DAY_LIMIT_AMOUNT_EXCEED)
        }

        // 1달 결제 한도 체크
        val monthAuthorizationAmount = list.filter { it.createdAt >= oneMonthAgo && it.createdAt <= currentDateTime }
            .sumOf { it.amount }

        val monthLimitAmount = limitAmountPolicyList.first { it.type == LimitAmountType.DAY }
            .limitAmount

        if (currentAmount.add(monthAuthorizationAmount) > monthLimitAmount) {
            throw CustomException(ErrorCode.MONTH_LIMIT_AMOUNT_EXCEED)
        }

    }

}