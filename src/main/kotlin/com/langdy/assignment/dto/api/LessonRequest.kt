package com.langdy.assignment.dto.api

import com.langdy.assignment.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class LessonRequest(
    @Contextual
    @Serializable(with = LocalDateTimeSerializer::class)
    val startAt: LocalDateTime,
    val courseId: Long,
    val teacherId: Long,
)
