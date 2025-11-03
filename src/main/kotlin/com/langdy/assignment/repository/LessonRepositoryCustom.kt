package com.langdy.assignment.repository

import com.langdy.assignment.domain.LessonStatus
import java.time.LocalDateTime

interface LessonRepositoryCustom {
    fun existsBookedLessonForTeacher(teacherId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
    fun existsBookedLessonForStudent(studentId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
}

