package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.Lesson
import com.langdy.langdy_backend_assignment.domain.LessonStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface LessonRepository : JpaRepository<Lesson, Long> {
    fun existsByTeacherIdAndStartAtAndStatus(teacherId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
    fun existsByStudentIdAndStartAtAndStatus(studentId: Long, startAt: LocalDateTime, status: LessonStatus): Boolean
    fun findByTeacherIdAndStartAtAndStatus(teacherId: Long, startAt: LocalDateTime, status: LessonStatus): List<Lesson>
}

