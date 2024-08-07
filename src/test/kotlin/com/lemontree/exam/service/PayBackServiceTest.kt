package com.lemontree.exam.service

import com.lemontree.exam.domain.entity.LimitAmountPolicy
import com.lemontree.exam.domain.entity.User
import com.lemontree.exam.domain.request.*
import com.lemontree.exam.domain.response.AuthorizationResponse
import com.lemontree.exam.domain.type.LimitAmountType
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import com.lemontree.exam.repository.LimitAmountPolicyRepository
import com.lemontree.exam.repository.UserRepository
import com.lemontree.exam.util.Utils.generateRandomSixDigitNumber
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class PayBackServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val limitAmountPolicyRepository: LimitAmountPolicyRepository,
    private val authorizationService: AuthorizationService,
    private val payBackService: PayBackService,
) {

    private var userId: Long? = null

    private lateinit var authorizationResponse: AuthorizationResponse

    @BeforeEach
    fun setUp() {

        val user = userRepository.save(
            User.of(
                SignUpRequest(
                    email = "yoonseok@test.com",
                    nickName = "yoonseok",
                    password = "password"
                )
            )
        )

        user.balance = user.balance.add(BigDecimal("10000000")) // 테스트 구현 위해, 한도를 넘어선 초기 보유 한도가 세팅되어 있다고 가정

        userRepository.save(user)

        userId = user.id


        val limitAmountPolicyList = listOf(
            LimitAmountPolicy(
                id = 1L,
                type = LimitAmountType.ONCE,
                currency = Currency.getInstance("KRW"),
                limitAmount = BigDecimal("100000")
            ),
            LimitAmountPolicy(
                id = 2L,
                type = LimitAmountType.DAY,
                currency = Currency.getInstance("KRW"),
                limitAmount = BigDecimal("1000000")
            ),
            LimitAmountPolicy(
                id = 3L,
                type = LimitAmountType.DAY,
                currency = Currency.getInstance("KRW"),
                limitAmount = BigDecimal("1000000")
            )
        )

        limitAmountPolicyRepository.saveAll(limitAmountPolicyList)

        authorizationResponse = authorizationService.authorize(
            userId!!,
            AuthorizationRequest(
                currency = Currency.getInstance("KRW"),
                amount = BigDecimal("100000"),
                cardAcceptorName = "10만원 상품권",
                cardAcceptorCode = "1234323523"
            )
        )
    }

    @Test
    fun `payBack - 유저가 존재하지 않을 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            payBackService.payback(
                1000L,
                PayBackRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NOT_EXIST_USER)
    }


    @Test
    fun `payBack - 원 승인이 존재하지 않을 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            payBackService.payback(
                userId!!,
                PayBackRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = generateRandomSixDigitNumber(),
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NO_AUTHORIZATION)
    }

    @Test
    fun `payBack - 승인 취소가 이미 된 경우 에러`() {

        authorizationService.reverse(
            userId!!,
            AuthorizationReversalRequest(
                currency = authorizationResponse.currency,
                amount = authorizationResponse.amount,
                authorizationNumber = authorizationResponse.authorizationNumber
            )
        )

        val errorCode = assertThrows<CustomException> {
            payBackService.payback(
                userId!!,
                PayBackRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NO_PAYBACK_EXIST_REVERSE)
    }

    @Test
    fun `payBack - 페이백 금액이 원 승인 금액보다 클 경우 에러`() {

        val errorCode = assertThrows<CustomException> {
            payBackService.payback(
                userId!!,
                PayBackRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount.add(BigDecimal.ONE),
                    authorizationNumber = authorizationResponse.authorizationNumber,
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.EXCEED_PAYBACK_AMOUNT)
    }

    @Test
    fun `reverse - 유저가 존재하지 않을 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            payBackService.reverse(
                1000L,
                PayBackReversalRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                    payBackNumber = generateRandomSixDigitNumber()
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NOT_EXIST_USER)
    }

    @Test
    fun `reverse - 승인 취소가 이미 된 경우 에러`() {

        authorizationService.reverse(
            userId!!,
            AuthorizationReversalRequest(
                currency = authorizationResponse.currency,
                amount = authorizationResponse.amount,
                authorizationNumber = authorizationResponse.authorizationNumber
            )
        )

        val errorCode = assertThrows<CustomException> {
            payBackService.reverse(
                userId!!,
                PayBackReversalRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                    payBackNumber = generateRandomSixDigitNumber()
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NO_PAYBACK_EXIST_REVERSE)
    }

    @Test
    fun `reverse - payBack이 없을 경우 에러`() {

        val errorCode = assertThrows<CustomException> {
            payBackService.reverse(
                userId!!,
                PayBackReversalRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    payBackAmount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                    payBackNumber = generateRandomSixDigitNumber()
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NO_PAYBACK)
    }

    @Test
    fun `reverse - payBack 취소가 이미 되었을 때 에러`() {

        val payBackResponse = payBackService.payback(
            userId!!,
            PayBackRequest(
                currency = authorizationResponse.currency,
                amount = authorizationResponse.amount,
                payBackAmount = authorizationResponse.amount,
                authorizationNumber = authorizationResponse.authorizationNumber,
            )
        )

        payBackService.reverse(
            userId!!,
            PayBackReversalRequest(
                currency = payBackResponse.currency,
                amount = payBackResponse.amount,
                payBackAmount = payBackResponse.amount,
                authorizationNumber = authorizationResponse.authorizationNumber,
                payBackNumber = payBackResponse.payBackNumber
            )
        )


        val errorCode = assertThrows<CustomException> {
            payBackService.reverse(
                userId!!,
                PayBackReversalRequest(
                    currency = payBackResponse.currency,
                    amount = payBackResponse.amount,
                    payBackAmount = payBackResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber,
                    payBackNumber = payBackResponse.payBackNumber
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.EXIST_PAYBACK_REVERSE)
    }
}