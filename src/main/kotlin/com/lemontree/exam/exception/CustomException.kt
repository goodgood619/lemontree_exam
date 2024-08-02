package com.lemontree.exam.exception

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException()