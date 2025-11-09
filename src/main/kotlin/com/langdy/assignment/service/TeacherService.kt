package com.langdy.assignment.service

import com.langdy.assignment.dto.TeacherDto
import com.langdy.assignment.repository.TeacherRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TeacherService(
    private val teacherRepository: TeacherRepository,
)
{

    fun findAvailableTeachers(
        courseId: Long,
        startAt: LocalDateTime,
    ): List<TeacherDto> {
        return teacherRepository.findAvailableTeachers(courseId, startAt)
    }
}
