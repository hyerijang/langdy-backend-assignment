package com.langdy.langdy_backend_assignment.dto

import jakarta.validation.constraints.NotNull

data class LessonRequest(
    @field:NotNull
    val startAt: String?,

    @field:NotNull
    val courseId: Long?,

    @field:NotNull
    val teacherId: Long?
)

