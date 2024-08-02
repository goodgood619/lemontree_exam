package com.lemontree.exam.util

import java.security.MessageDigest

object Utils {

    fun sha256Hash(input: String): String {
        val bytes = input.toByteArray() // 입력 문자열을 바이트 배열로 변환
        val md = MessageDigest.getInstance("SHA-256") // SHA-256 해시 함수 인스턴스 생성
        val digest = md.digest(bytes) // 바이트 배열을 해시 함수에 전달하여 해시 값 생성
        return digest.joinToString("") { "%02x".format(it) } // 해시 값을 16진수 문자열로 변환하여 반환
    }
}