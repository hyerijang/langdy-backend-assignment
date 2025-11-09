package com.langdy.assignment.domain

/**
 * 레슨의 상태를 나타내는 열거형입니다.
 *
 * - BOOKED: 예약된 상태
 * - CANCELLED: 취소된 상태
 * - DONE: 완료된 상태
 */
enum class LessonStatus {
    BOOKED,
    CANCELLED,
    DONE,
}
