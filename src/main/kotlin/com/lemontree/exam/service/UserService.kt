package com.lemontree.exam.service

import com.lemontree.exam.domain.entity.User
import com.lemontree.exam.domain.request.ChargeRequest
import com.lemontree.exam.domain.request.LoginRequest
import com.lemontree.exam.domain.request.SignUpRequest
import com.lemontree.exam.domain.response.LoginResponse
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import com.lemontree.exam.repository.UserRepository
import com.lemontree.exam.util.Utils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional
    fun signIn(signUpRequest: SignUpRequest) {
        val existUser = userRepository.findByEmail(signUpRequest.email)
        if (existUser != null) {
            throw CustomException(ErrorCode.INVALID_PARAMETER)
        }

        userRepository.save(User.of(signUpRequest))
    }

    @Transactional
    fun login(loginRequest: LoginRequest): LoginResponse {

        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw CustomException(ErrorCode.NOT_EXIST_USER)

        if (Utils.sha256Hash(loginRequest.password) !=  user.password) {
            throw CustomException(ErrorCode.INVALID_PARAMETER)
        }

        return LoginResponse(id = user.id)
    }

    @Transactional
    fun charge(id: Long, chargeRequest: ChargeRequest) {
        val user = userRepository.findById(id).getOrNull()
            ?: throw CustomException(ErrorCode.NOT_EXIST_USER)

        user.balance = user.balance.add(chargeRequest.amount) // dirty checking
    }
}