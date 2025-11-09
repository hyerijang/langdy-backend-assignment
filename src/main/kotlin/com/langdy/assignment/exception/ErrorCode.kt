package com.langdy.assignment.exception

import org.springframework.http.HttpStatus

/**
 * 애플리케이션 공통 커스텀 예외
 * ErrorCode 기반으로 예외를 표현합니다.
 */

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    // System Exception
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),

    // COMMON Custom Exception
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALID_FAILED(HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다."),

    // TeacherService
    INVALID_START_AT_MINUTE(HttpStatus.BAD_REQUEST, "startAt의 분(minute)은 00 또는 30이어야 합니다."),

    // LessonService
    TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "선생님을 찾을 수 없습니다."),
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다."),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "강좌를 찾을 수 없습니다."),
    INVALID_START_AT(HttpStatus.BAD_REQUEST, "startAt은 분이 :00 또는 :30이어야 하며 초와 나노초는 0이어야 합니다"),
    TEACHER_ALREADY_BOOKED(HttpStatus.CONFLICT, "요청한 시간에 선생님이 예약되어 있습니다"),
    STUDENT_ALREADY_BOOKED(HttpStatus.CONFLICT, "요청한 시간에 학생이 예약되어 있습니다"),
}
