package com.langdy.assignment.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

/**
 * 애플리케이션 전역 예외 처리기입니다.
 *
 * 컨트롤러 계층에서 발생하는 다양한 예외를 한 곳에서 처리하여 일관된
 * [ErrorResult] 형태의 응답을 반환합니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 비즈니스 계층에서 던지는 [CustomException]을 처리합니다.
     * 상태 코드와 메시지를 사용해 [ErrorResult]를 생성하고 해당 상태로 응답합니다.
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustom(ex: CustomException): ResponseEntity<ErrorResult> {
        val status = ex.status
        log.warn("Handled CustomException: {} - {}", status, ex.message)
        val body = ErrorResult(status.name, ex.message ?: ex.getErrorCode().message)
        return ResponseEntity(body, status)
    }

    /**
     * Spring의 [ResponseStatusException]을 처리합니다.
     * HTTP 상태 코드와 이유(reason)를 응답 본문으로 전달합니다.
     */
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ErrorResult> {
        val status = ex.statusCode
        log.warn("Handled ResponseStatusException: {} - {}", status, ex.reason)
        val body = ErrorResult(status.toString(), ex.reason ?: "")
        return ResponseEntity(body, status)
    }

    /**
     * 요청 바인딩/검증 실패 시 발생하는 [MethodArgumentNotValidException]을 처리합니다.
     * 필드별 오류 메시지를 모아서 `details`에 포함한 [ErrorResult]를 반환합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResult> {
        val msg = ex.bindingResult.fieldErrors.joinToString(", ") { f -> "${f.field}: ${f.defaultMessage}" }
        log.warn("Validation failed: {}", msg)
        val body = ErrorResult(HttpStatus.BAD_REQUEST.name, "Validation failed", msg)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    /**
     * 그 외 예기치 못한 예외를 처리하여 내부 서버 오류 응답을 반환합니다.
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResult> {
        log.error("Unhandled exception", ex)
        val body = ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.name, "Internal server error")
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
