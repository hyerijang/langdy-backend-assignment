package com.langdy.assignment.exception

import org.springframework.http.HttpStatus

class CustomException(
    private val errorCode: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(errorCode.message, cause) {
    // Java의 private final 과 유사하게 내부에 보관하고, 외부 접근은 getter 사용
    fun getErrorCode(): ErrorCode = errorCode

    // 편의 프로퍼티
    val status: HttpStatus
        get() = errorCode.status
}
