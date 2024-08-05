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
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class AuthorizationService(
    private val userRepository: UserRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val limitAmountService: LimitAmountService,
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

        // check limit Amount
        // 최대 1달 이내의 결제액을 알아야 제대로 된 계산을 할수 있다고 믿었음
        // 일일이 1일, 1달과 연관된 쿼리문을 호출하는 것 보다, CPU를 사용해서 처리하는게 좀 더 효율적일 것이라 생각
        val monthlyAuthorizationList = authorizationRepository.findByCreatedAtBetweenAndType(
            userId = userId,
            from = LocalDateTime.now().minus(1, ChronoUnit.MONTHS).toLocalDate().atStartOfDay(),
            to = LocalDateTime.now(),
            type = AuthorizationType.AUTHORIZE
        )

        limitAmountService.checkAmount(
            currentAmount = authorizationRequest.amount,
            list = monthlyAuthorizationList
        )

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