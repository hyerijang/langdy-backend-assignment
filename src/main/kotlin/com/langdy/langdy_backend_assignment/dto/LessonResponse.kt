package com.langdy.langdy_backend_assignment.dto

import com.langdy.langdy_backend_assignment.domain.LessonStatus
import java.time.LocalDateTime

data class LessonResponse(
    val id: Long,
    val courseId: Long,
    val teacherId: Long,
    val studentId: Long,
    val status: LessonStatus,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)

