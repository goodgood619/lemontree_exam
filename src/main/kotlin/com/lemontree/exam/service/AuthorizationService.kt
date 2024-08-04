package com.lemontree.exam.service

import com.lemontree.exam.domain.entity.Authorization
import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.AuthorizationReversalRequest
import com.lemontree.exam.domain.response.AuthorizationResponse
import com.lemontree.exam.domain.response.AuthorizationReversalResponse
import com.lemontree.exam.domain.type.AuthorizationType
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import com.lemontree.exam.repository.AuthorizationRepository
import com.lemontree.exam.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorizationService(
    private val userRepository: UserRepository,
    private val authorizationRepository: AuthorizationRepository,
) {

    @Transactional
    fun authorize(userId: Long, authorizationRequest: AuthorizationRequest): AuthorizationResponse {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }

        // balance check
        if (user.balance < authorizationRequest.amount) {
            throw CustomException(ErrorCode.NOT_ENOUGH_BALANCE)
        }

        // save authorize
        val authorization = Authorization.of(authorizationRequest, user)

        authorizationRepository.save(authorization)

        // user balance update
        user.balance = user.balance.subtract(authorizationRequest.amount)

        return AuthorizationResponse.from(authorization)
    }

    // 취소가 여러번 들어올 수 있는 동시성 체크에 대해 구현이 추가로 되어 있어야 함
    @Transactional
    fun reverse(
        userId: Long,
        authorizationReversalRequest: AuthorizationReversalRequest
    ): AuthorizationReversalResponse {
        // user exist check
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ErrorCode.NOT_EXIST_USER)
        }

        // authorization exist
        val authorizationList =
            authorizationRepository.findByCurrencyAndAmountAndAuthorizationNumber(
                currency = authorizationReversalRequest.currency,
                amount = authorizationReversalRequest.amount,
                authorizationNumber = authorizationReversalRequest.authorizationNumber
            )

        if (authorizationList.isEmpty()) {
            throw CustomException(ErrorCode.NO_AUTHORIZATION)
        }


        // reverse no exist
        if (authorizationList.any { authorization ->
                authorization.type == AuthorizationType.AUTHORIZE_REVERSE
            }) {
            throw CustomException(ErrorCode.EXIST_REVERSE)
        }

        val authorization = authorizationList.first { authorization ->
            authorization.type == AuthorizationType.AUTHORIZE
        }

        // save reverse
        val reverse = Authorization.of(
            authorizationReversalRequest, user,
            authorization.authorizationNumber,
            authorization.cardAcceptorCode,
            authorization.cardAcceptorName
        )

        authorizationRepository.save(reverse)

        // user balance add
        user.balance = user.balance.add(authorizationReversalRequest.amount)

        return AuthorizationReversalResponse.from(balance = user.balance)
    }
}