package com.lemontree.exam.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 입니다."),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재 하지 않는 유저 입니다."),
    NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    NO_AUTHORIZATION(HttpStatus.BAD_REQUEST, "원 승인이 존재 하지 않습니다."),
    EXIST_REVERSE(HttpStatus.BAD_REQUEST, "이미 승인 취소가 존재해 취소가 불가능 합니다."),
    EXCEED_PAYBACK_AMOUNT(HttpStatus.BAD_REQUEST, "페이백 금액은 원 승인 금액보다 클수 없습니다."), // 변수 문구 고민
}