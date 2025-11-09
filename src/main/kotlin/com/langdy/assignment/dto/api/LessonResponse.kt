package com.langdy.assignment.dto.api

import com.langdy.assignment.domain.Lesson
import com.langdy.assignment.domain.LessonStatus
import java.time.LocalDateTime

data class LessonResponse(
    val id: Long,
    val courseId: Long,
    val teacherId: Long,
    val studentId: Long,
    val status: LessonStatus,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
) {
    companion object {
        fun from(lesson: Lesson): LessonResponse {
            val id = requireNotNull(lesson.id) { "Lesson id must not be null" }
            val courseId = requireNotNull(lesson.course?.id) { "Lesson.course.id must not be null" }
            val teacherId = requireNotNull(lesson.teacher?.id) { "Lesson.teacher.id must not be null" }
            val studentId = requireNotNull(lesson.student?.id) { "Lesson.student.id must not be null" }

            return LessonResponse(
                id = id,
                courseId = courseId,
                teacherId = teacherId,
                studentId = studentId,
                status = lesson.status,
                startAt = lesson.startAt,
                endAt = lesson.endAt,
            )
        }
    }
}
