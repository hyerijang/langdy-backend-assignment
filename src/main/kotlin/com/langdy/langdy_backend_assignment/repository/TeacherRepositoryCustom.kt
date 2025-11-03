package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.dto.TeacherDto
import java.time.LocalDateTime

interface TeacherRepositoryCustom {
    fun findAvailableTeachers(courseId: Long, startAt: LocalDateTime): List<TeacherDto>
}

