package com.langdy.assignment.repository

import com.langdy.assignment.dto.projection.TeacherQueryDto
import java.time.LocalDateTime

interface TeacherRepositoryCustom {
    fun findAvailableTeachers(
        courseId: Long,
        startAt: LocalDateTime,
    ): List<TeacherQueryDto>
}
