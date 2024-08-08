package com.lemontree.exam.controller

import com.lemontree.exam.domain.request.AuthorizationRequest
import com.lemontree.exam.domain.request.AuthorizationReversalRequest
import com.lemontree.exam.domain.response.AuthorizationResponse
import com.lemontree.exam.domain.response.AuthorizationReversalResponse
import com.lemontree.exam.domain.response.GeneralResponse
import com.lemontree.exam.service.AuthorizationService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationController(
    private val authorizationService: AuthorizationService,
) {

    @PostMapping("/user/authorize/{id}")
    fun authorize(@PathVariable id: Long, @RequestBody authorizationRequest: AuthorizationRequest): GeneralResponse<AuthorizationResponse> {
        val authorizationResponse = authorizationService.authorize(id, authorizationRequest)
        return GeneralResponse(data = authorizationResponse)
    }

    @PostMapping("/user/reverse/{id}")
    fun reverse(@PathVariable id: Long, @RequestBody authorizationReversalRequest: AuthorizationReversalRequest): GeneralResponse<AuthorizationReversalResponse> {
        val authorizationReversalResponse = authorizationService.reverse(id, authorizationReversalRequest)
        return GeneralResponse(data = authorizationReversalResponse)
    }

}