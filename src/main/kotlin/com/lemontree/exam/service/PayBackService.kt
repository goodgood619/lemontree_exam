package com.lemontree.exam.service

import com.lemontree.exam.common.RedissonLock
import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.entity.PayBack
import com.lemontree.exam.domain.entity.QAuthorization.authorization
import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.AuthorizationReversalRequest
import com.lemontree.exam.domain.request.PayBackRequest
import com.lemontree.exam.domain.request.PayBackReversalRequest
import com.lemontree.exam.domain.response.AuthorizationResponse
import com.lemontree.exam.domain.response.AuthorizationReversalResponse
import com.lemontree.exam.domain.response.PayBackResponse
import com.lemontree.exam.domain.response.PayBackReversalResponse
import com.lemontree.exam.domain.type.AuthorizationType
import com.lemontree.exam.domain.type.PayBackType
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import com.lemontree.exam.repository.AuthorizationRepository
import com.lemontree.exam.repository.PayBackRepository
import com.lemontree.exam.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PayBackService(
    private val userRepository: UserRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val payBackRepository: PayBackRepository
) {

    @RedissonLock(keyPrefix = "payBack")
    @Transactional
    fun payback(userId: Long, payBackRequest: PayBackRequest): PayBackResponse {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }

        val authorizationList =
            authorizationRepository.findByTypeAndCurrencyAndAuthorizationNumber(
                userId = user.id,
                type = AuthorizationType.AUTHORIZE,
                amount = payBackRequest.amount,
                currency = payBackRequest.currency,
                authorizationNumber = payBackRequest.authorizationNumber
            )


        if (authorizationList.isEmpty()) {
            throw CustomException(ErrorCode.NO_AUTHORIZATION)
        }

        // if 승인 취소가 이미 존재하면 payback 불가
        val authorizationReverseList =
            authorizationRepository.findByTypeAndCurrencyAndAuthorizationNumber(
                userId = user.id,
                type = AuthorizationType.AUTHORIZE_REVERSE,
                currency = payBackRequest.currency,
                amount = payBackRequest.amount,
                authorizationNumber = payBackRequest.authorizationNumber
            )

        if (authorizationReverseList.isNotEmpty()) {
            throw CustomException(ErrorCode.NO_PAYBACK_EXIST_REVERSE)
        }

        val authorization = authorizationList.first()

        if (payBackRequest.payBackAmount > authorization.amount) {
            throw CustomException(ErrorCode.EXCEED_PAYBACK_AMOUNT)
        }

        // save PayBack
        val payBack = PayBack.of(payBackRequest, authorization)

        payBackRepository.save(payBack)

        // user balance update
        user.balance = user.balance.add(payBackRequest.payBackAmount)

        return PayBackResponse.of(payBack, user.balance)
    }

    @RedissonLock(keyPrefix = "payBackReverse")
    @Transactional
    fun reverse(
        userId: Long,
        payBackReversalRequest: PayBackReversalRequest
    ): PayBackReversalResponse {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }


        // if 승인 취소가 이미 존재하면 payback 취소 또한 불가
        val authorizationReverseList =
            authorizationRepository.findByTypeAndCurrencyAndAuthorizationNumber(
                userId = user.id,
                type = AuthorizationType.AUTHORIZE_REVERSE,
                currency = payBackReversalRequest.currency,
                amount = payBackReversalRequest.amount,
                authorizationNumber = payBackReversalRequest.authorizationNumber
            )

        if (authorizationReverseList.isNotEmpty()) {
            throw CustomException(ErrorCode.NO_PAYBACK_EXIST_REVERSE)
        }

        // check payback
        val payBackList = payBackRepository.findByTypeAndCurrencyAndAuthorizationNumber(
            userId = userId,
            amount = payBackReversalRequest.payBackAmount,
            currency = payBackReversalRequest.currency,
            authorizationNumber = payBackReversalRequest.authorizationNumber,
            payBackNumber = payBackReversalRequest.payBackNumber
        )

        if (payBackList.isEmpty()) {
            throw CustomException(ErrorCode.NO_PAYBACK)
        }


        // check payback reverse
        if (payBackList.any { it.type == PayBackType.PAYBACK_REVERSE }) {
            throw CustomException(ErrorCode.EXIST_PAYBACK_REVERSE)
        }

        val authorization =
            authorizationRepository.findByTypeAndCurrencyAndAuthorizationNumber(
                userId = user.id,
                type = AuthorizationType.AUTHORIZE,
                amount = payBackReversalRequest.amount,
                currency = payBackReversalRequest.currency,
                authorizationNumber = payBackReversalRequest.authorizationNumber
            ).first()

        // save Payback
        val payBack = PayBack.of(payBackReversalRequest, authorization)

        payBackRepository.save(payBack)

        // user balance update
        user.balance = user.balance.subtract(payBackReversalRequest.payBackAmount)

        return PayBackReversalResponse.from(user.balance)
    }
}