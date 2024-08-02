package com.lemontree.exam.controller

import com.lemontree.exam.domain.request.ChargeRequest
import com.lemontree.exam.domain.request.LoginRequest
import com.lemontree.exam.domain.request.SignUpRequest
import com.lemontree.exam.domain.response.GeneralResponse
import com.lemontree.exam.domain.response.LoginResponse
import com.lemontree.exam.service.UserService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/user/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): GeneralResponse<Any> {
        userService.signIn(signUpRequest)
        return GeneralResponse()
    }

    @PostMapping("/user/login")
    fun login(@RequestBody loginRequest: LoginRequest): GeneralResponse<LoginResponse> {
        val loginResponse = userService.login(loginRequest)
        return GeneralResponse(data = loginResponse)
    }

    // TODO : 계좌 충전 API, 사실 별도로 빼는게 낫긴 함
    @PostMapping("/user/{id}/charge")
    fun charge(@PathVariable id: Long, @RequestBody chargeRequest: ChargeRequest): GeneralResponse<Any> {
        userService.charge(id, chargeRequest)
        return GeneralResponse()
    }
}