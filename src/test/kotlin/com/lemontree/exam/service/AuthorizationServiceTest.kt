package com.lemontree.exam.service

import com.lemontree.exam.domain.entity.LimitAmountPolicy
import com.lemontree.exam.domain.entity.QAuthorization.authorization
import com.lemontree.exam.domain.entity.QUser.user
import com.lemontree.exam.domain.entity.User
import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.AuthorizationReversalRequest
import com.lemontree.exam.domain.request.SignUpRequest
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
import java.util.Currency

@ActiveProfiles("test")
@SpringBootTest
class AuthorizationServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val limitAmountPolicyRepository: LimitAmountPolicyRepository,
    private val authorizationService: AuthorizationService,
) {

    private var userId: Long? = null

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

    }

    @Test
    fun `authorize - user가 존재하지 않을 때 에러`() {
        val errorCode = assertThrows<CustomException> {
            authorizationService.authorize(
                1000L,
                AuthorizationRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("500000"),
                    cardAcceptorName = "짝퉁 루이비통",
                    cardAcceptorCode = "1234254353412"
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NOT_EXIST_USER)
    }

    @Test
    fun `authorize - 1회 금액이 초과될 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            authorizationService.authorize(
                userId!!,
                AuthorizationRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("500000"),
                    cardAcceptorName = "짝퉁 루이비통",
                    cardAcceptorCode = "1234254353412"
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.ONCE_LIMIT_AMOUNT_EXCEED)
    }

    @Test
    fun `authorize - 1일 금액이 초과될 때 에러`() {

        for (i in 0..9) {
            authorizationService.authorize(
                userId!!,
                AuthorizationRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("100000"),
                    cardAcceptorName = "짝퉁 루이비통 $i",
                    cardAcceptorCode = "1234254353412"
                )
            )
        }
        val errorCode = assertThrows<CustomException> {
            authorizationService.authorize(
                userId!!,
                AuthorizationRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("100000"),
                    cardAcceptorName = "짝퉁 루이비통",
                    cardAcceptorCode = "1234254353412"
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.DAY_LIMIT_AMOUNT_EXCEED)
    }

    @Test
    fun `authorize - 잔액이 부족할 때 에러`() {

        val user = userRepository.findById(userId!!).orElseThrow()

        user.balance = BigDecimal("10000")

        userRepository.save(user)

        val errorCode = assertThrows<CustomException> {
            authorizationService.authorize(
                userId!!,
                AuthorizationRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("100000"),
                    cardAcceptorName = "짝퉁 루이비통",
                    cardAcceptorCode = "1234254353412"
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NOT_ENOUGH_BALANCE)
    }

    @Test
    fun `reverse - 유저가 없을 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            authorizationService.reverse(
                1000L,
                AuthorizationReversalRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("100000"),
                    authorizationNumber = generateRandomSixDigitNumber()
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NOT_EXIST_USER)
    }


    @Test
    fun `reverse - 원 승인이 없을 때 에러`() {

        val errorCode = assertThrows<CustomException> {
            authorizationService.reverse(
                1L,
                AuthorizationReversalRequest(
                    currency = Currency.getInstance("KRW"),
                    amount = BigDecimal("100000"),
                    authorizationNumber = generateRandomSixDigitNumber()
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.NO_AUTHORIZATION)
    }


    @Test
    fun `reverse - 승인 취소가 이미 존재할 때 에러`() {

        val authorizationResponse = authorizationService.authorize(
            userId!!,
            AuthorizationRequest(
                currency = Currency.getInstance("KRW"),
                amount = BigDecimal("100000"),
                cardAcceptorName = "10만원 상품권",
                cardAcceptorCode = "12353545342"
            )
        )

        authorizationService.reverse(
            1L,
            AuthorizationReversalRequest(
                currency = authorizationResponse.currency,
                amount = authorizationResponse.amount,
                authorizationNumber = authorizationResponse.authorizationNumber
            )
        )


        val errorCode = assertThrows<CustomException> {
            authorizationService.reverse(
                1L,
                AuthorizationReversalRequest(
                    currency = authorizationResponse.currency,
                    amount = authorizationResponse.amount,
                    authorizationNumber = authorizationResponse.authorizationNumber
                )
            )
        }.errorCode

        assertThat(errorCode).isEqualTo(ErrorCode.EXIST_REVERSE)
    }
}