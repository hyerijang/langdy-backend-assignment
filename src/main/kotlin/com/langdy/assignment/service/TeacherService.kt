package com.langdy.assignment.service

import com.langdy.assignment.dto.projection.TeacherQueryDto
import com.langdy.assignment.exception.CustomException
import com.langdy.assignment.exception.ErrorCode
import com.langdy.assignment.repository.TeacherRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 강사 조회 관련 비즈니스 로직을 제공하는 서비스입니다.
 * @property teacherRepository 강사 조회 쿼리를 수행하는 리포지토리
 */
@Service
class TeacherService(
    private val teacherRepository: TeacherRepository,
) {
    /**
     * 지정한 코스와 시작 시간에 대해 사용 가능한 강사를 조회합니다.
     *
     * 분 단위 유효성 검사를 수행합니다: 시작 시간의 분(minute)이 0 또는 30이 아니면
     * [ErrorCode.INVALID_START_AT_MINUTE]를 포함한 [CustomException]을 던집니다.
     *
     * @param courseId 조회할 코스의 식별자
     * @param startAt 조회할 수업의 시작 시각 (분 단위는 0 또는 30이어야 함)
     * @return 사용 가능한 강사 목록을 나타내는 [TeacherQueryDto] 리스트
     * @throws CustomException 시작 시각의 분이 유효하지 않을 경우
     */
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
