package com.lemontree.exam.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 입니다."),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재 하지 않는 유저 입니다."),
    NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    NO_AUTHORIZATION(HttpStatus.BAD_REQUEST, "원 승인이 존재 하지 않습니다."),
    EXIST_REVERSE(HttpStatus.BAD_REQUEST, "이미 승인 취소가 존재해 취소가 불가능 합니다."),
    EXCEED_PAYBACK_AMOUNT(HttpStatus.BAD_REQUEST, "페이백 금액은 원 승인 금액보다 클수 없습니다."), // 변수 문구 고민
    ONCE_LIMIT_AMOUNT_EXCEED(HttpStatus.BAD_REQUEST, "1회 결제 금액이 초과 되어 결제가 불가능 합니다."),
    DAY_LIMIT_AMOUNT_EXCEED(HttpStatus.BAD_REQUEST, "1일 결제 금액이 초과 되어 결제가 불가능 합니다. "),
    MONTH_LIMIT_AMOUNT_EXCEED(HttpStatus.BAD_REQUEST, "1달 결제 금액이 초과 되어 결제가 불가능 합니다. "),
    NO_PAYBACK_EXIST_REVERSE(HttpStatus.BAD_REQUEST, "이미 승인 취소가 존재해 페이백이 불가능 합니다."),
    NO_PAYBACK(HttpStatus.BAD_REQUEST, "페이백이 존재하지 않아 취소 할수 없습니다."),
    EXIST_PAYBACK_REVERSE(HttpStatus.BAD_REQUEST, "페이백 취소가 이미 존재해 취소 할수 없습니다."),
    LOCK_ACQUIRE_FAIL(HttpStatus.BAD_REQUEST, "동시에 API 요청이 발생해 처리할수 없습니다."),
    USER_BALANCE_LIMIT_EXCEED(HttpStatus.BAD_REQUEST, "유저가 보유할 수 있는 최대 한도를 넘었습니다."),
}