package com.langdy.assignment.exception

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * API 에러 응답을 표현하는 DTO입니다.
 *
 * @property code 오류 코드(또는 HTTP 상태 이름)
 * @property message 사용자에게 표시할 오류 메시지
 * @property details 추가적인 오류 상세 정보(있을 경우)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResult(
    val code: String,
    val message: String,
    val details: String? = null,
)
