package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.LessonStatus
import java.time.LocalDateTime

interface LessonRepositoryCustom {
    fun existsBookedLessonForTeacher(teacherId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
    fun existsBookedLessonForStudent(studentId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
}

