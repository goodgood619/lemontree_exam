package com.lemontree.exam.service

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
import com.lemontree.exam.domain.type.AuthorizationType
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

    @Transactional
    fun payback(userId: Long, payBackRequest: PayBackRequest): PayBackResponse {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }

        // TODO : if 승인 취소가 이미 존재하면 payback 불가

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

        // TODO : payback이 존재하면 payback 불가

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

    // 취소가 여러번 들어올 수 있는 동시성 체크에 대해 구현이 추가로 되어 있어야 함
    @Transactional
    fun reverse(
        userId: Long,
        payBackReversalRequest: PayBackReversalRequest
    ) {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }


    }
}