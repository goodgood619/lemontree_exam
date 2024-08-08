package com.lemontree.exam.controller

import com.lemontree.exam.domain.entity.LimitAmountPolicy
import com.lemontree.exam.domain.entity.User
import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.SignUpRequest
import com.lemontree.exam.domain.response.AuthorizationReversalResponse
import com.lemontree.exam.domain.response.GeneralResponse
import com.lemontree.exam.domain.type.LimitAmountType
import com.lemontree.exam.repository.LimitAmountPolicyRepository
import com.lemontree.exam.repository.UserRepository
import com.lemontree.exam.service.AuthorizationService
import com.lemontree.exam.util.toJsonString
import com.lemontree.exam.util.toObject
import mu.KLogging
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationControllerTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val limitAmountPolicyRepository: LimitAmountPolicyRepository,
    private val authorizationService: AuthorizationService,
) {

    companion object : KLogging()

    @Autowired
    private lateinit var mvc: MockMvc

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
    fun `reverse - 동일한 취소 여러개 동시 테스트`() {
        // 먼저 승인을 해야 한다
        val authorizationResponse = authorizationService.authorize(
            userId!!,
            AuthorizationRequest(
                currency = Currency.getInstance("KRW"),
                amount = BigDecimal("100000"),
                cardAcceptorName = "10만원 상품권",
                cardAcceptorCode = "1234323523"
            )
        )


        val executorService = Executors.newFixedThreadPool(10)

        val totalCount = 10
        val failCount = AtomicInteger()

        val request = mapOf(
            "currency" to authorizationResponse.currency,
            "amount" to authorizationResponse.amount,
            "authorizationNumber" to authorizationResponse.authorizationNumber
        )


        (0..9).forEach {
            try {
                val response = mvc.perform(
                    MockMvcRequestBuilders.post("/user/reverse/${userId}")
                        .content(toJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )

                val authorizationReversalResponse =
                    toObject<GeneralResponse<AuthorizationReversalResponse>>(response.andReturn().response.contentAsString)

                if (authorizationReversalResponse.message != "ok") {
                    failCount.incrementAndGet()
                }

            } finally {
                logger.info { "$it 번째 결과 순서" }
            }
        }

        Thread.sleep(2000)

        assertThat(totalCount - failCount.get()).isEqualTo(1)

        executorService.shutdown()
        executorService.awaitTermination(3, TimeUnit.SECONDS)
    }

}