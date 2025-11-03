package com.langdy.langdy_backend_assignment.dto

import com.langdy.langdy_backend_assignment.domain.Lesson
import com.langdy.langdy_backend_assignment.domain.LessonStatus
import java.time.LocalDateTime

data class LessonResponse(
    val id: Long,
    val courseId: Long,
    val teacherId: Long,
    val studentId: Long,
    val status: LessonStatus,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
){

    companion object {
        fun from(lesson: Lesson): LessonResponse {
            return LessonResponse(
                id = lesson.id!!,
                courseId = lesson.course!!.id!!,
                teacherId = lesson.teacher!!.id!!,
                studentId = lesson.student!!.id!!,
                status = lesson.status,
                startAt = lesson.startAt,
                endAt = lesson.endAt,
            )
        }
    }
}
