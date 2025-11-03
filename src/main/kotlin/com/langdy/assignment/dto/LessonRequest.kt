package com.langdy.assignment.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class LessonRequest(
    @Contextual
    val startAt: LocalDateTime,
    val courseId: Long,
    val teacherId: Long,
)
