package com.langdy.assignment.repository

import com.langdy.assignment.dto.TeacherDto
import java.time.LocalDateTime

interface TeacherRepositoryCustom {
    fun findAvailableTeachers(
        courseId: Long,
        startAt: LocalDateTime,
    ): List<TeacherDto>
}
