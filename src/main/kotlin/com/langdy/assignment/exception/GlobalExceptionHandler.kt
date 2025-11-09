package com.langdy.assignment.exception

import com.langdy.assignment.exception.CustomException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    // CustomException 처리기
    // 언제: 서비스/비즈니스 계층에서 `throw CustomException(ErrorCode.X)` 호출할 때 동작
    @ExceptionHandler(CustomException::class)
    fun handleCustom(ex: CustomException): ResponseEntity<ErrorResult> {
        val status = ex.status
        log.warn("Handled CustomException: {} - {}", status, ex.message)
        val body = ErrorResult(status.name, ex.message ?: ex.getErrorCode().message)
        return ResponseEntity(body, status)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ErrorResult> {
        val status = ex.statusCode
        log.warn("Handled ResponseStatusException: {} - {}", status, ex.reason)
        val body = ErrorResult(status.toString(), ex.reason ?: "")
        return ResponseEntity(body, status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResult> {
        val msg = ex.bindingResult.fieldErrors.joinToString(", ") { f -> "${f.field}: ${f.defaultMessage}" }
        log.warn("Validation failed: {}", msg)
        val body = ErrorResult(HttpStatus.BAD_REQUEST.name, "Validation failed", msg)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    // Catch-all: 예상치 못한 예외(서버 에러) 처리
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResult> {
        log.error("Unhandled exception", ex)
        val body = ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.name, "Internal server error")
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
