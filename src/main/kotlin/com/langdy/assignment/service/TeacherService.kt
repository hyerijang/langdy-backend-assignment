package com.langdy.assignment.service

import com.langdy.assignment.dto.projection.TeacherQueryDto
import com.langdy.assignment.exception.CustomException
import com.langdy.assignment.exception.ErrorCode
import com.langdy.assignment.repository.TeacherRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TeacherService(
    private val teacherRepository: TeacherRepository,
) {
    fun findAvailableTeachers(
        courseId: Long,
        startAt: LocalDateTime,
    ): List<TeacherQueryDto> {
        // 분 단위 유효성 검사: 00 또는 30이 아니면 Bad Request
        val minute = startAt.minute
        if (minute != 0 && minute != 30) {
            throw CustomException(ErrorCode.INVALID_START_AT_MINUTE)
        }
        return teacherRepository.findAvailableTeachers(courseId, startAt)
    }
}
