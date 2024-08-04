package com.lemontree.exam.controller.advice

import com.lemontree.exam.domain.response.GeneralResponse
import com.lemontree.exam.exception.CustomException
import com.lemontree.exam.exception.ErrorCode
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(value = [CustomException::class])
    fun handlingCustomException(ex: CustomException) : GeneralResponse<Any> {
        return GeneralResponse (
            code = ex.errorCode.status.value(),
            message = ex.errorCode.message
        )
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): GeneralResponse<Any> {
        return GeneralResponse(
            code = ErrorCode.INVALID_PARAMETER.status.value(),
            message = ErrorCode.INVALID_PARAMETER.message
        )
    }
}