package com.lemontree.exam.controller

import com.lemontree.exam.domain.request.PayBackRequest
import com.lemontree.exam.domain.request.PayBackReversalRequest
import com.lemontree.exam.domain.response.GeneralResponse
import com.lemontree.exam.domain.response.PayBackResponse
import com.lemontree.exam.domain.response.PayBackReversalResponse
import com.lemontree.exam.service.PayBackService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PayBackController(
    private val payBackService: PayBackService,
) {

    @PostMapping("/user/payback/{id}")
    fun authorize(@PathVariable id: Long, @RequestBody payBackRequest: PayBackRequest): GeneralResponse<PayBackResponse> {
        val payBackResponse = payBackService.payback(id, payBackRequest)
        return GeneralResponse(data = payBackResponse)
    }

    @PostMapping("/user/payback/reverse/{id}")
    fun reverse(@PathVariable id: Long, @RequestBody payBackReversalRequest: PayBackReversalRequest): GeneralResponse<PayBackReversalResponse> {
        val payBackReversalResponse = payBackService.reverse(id, payBackReversalRequest)
        return GeneralResponse(data = payBackReversalResponse)
    }
}