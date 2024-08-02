package com.lemontree.exam.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 입니다."),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재 하지 않는 유저 입니다.")

}