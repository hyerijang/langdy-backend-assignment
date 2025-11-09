package com.langdy.assignment.service

import com.langdy.assignment.domain.Lesson
import com.langdy.assignment.domain.LessonStatus
import com.langdy.assignment.dto.api.LessonRequest
import com.langdy.assignment.dto.api.LessonResponse
import com.langdy.assignment.exception.CustomException
import com.langdy.assignment.exception.ErrorCode
import com.langdy.assignment.repository.CourseRepository
import com.langdy.assignment.repository.LessonRepository
import com.langdy.assignment.repository.StudentRepository
import com.langdy.assignment.repository.TeacherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 수업(레슨) 관련 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) {
    /**
     * 새로운 레슨을 생성합니다.
     *
     * 동작 요약:
     * 1. 요청의 시작 시각 유효성 검사(분은 0 또는 30, 초/나노초는 0)
     * 2. 교사/학생/코스 존재 확인
     * 3. 교사와 학생의 해당 시작 시각 예약 가능 여부 확인
     * 4. 레슨 엔티티 생성 후 저장하고 [LessonResponse]로 반환
     *
     * @param request 클라이언트에서 전달한 레슨 생성 요청 데이터
     * @param studentId 레슨을 예약하는 학생의 ID (인증/컨텍스트로부터 전달됨)
     * @return 생성된 레슨 정보를 담은 [LessonResponse]
     * @throws CustomException 다음 경우에 발생:
     *  - [ErrorCode.INVALID_START_AT]: 시작 시각이 30분 단위가 아니거나 초/나노초가 0이 아님
     *  - [ErrorCode.TEACHER_NOT_FOUND]: 요청한 교사를 찾을 수 없음
     *  - [ErrorCode.STUDENT_NOT_FOUND]: 학생을 찾을 수 없음
     *  - [ErrorCode.COURSE_NOT_FOUND]: 코스를 찾을 수 없음
     *  - [ErrorCode.TEACHER_ALREADY_BOOKED]: 해당 교사에 예약 충돌이 있음
     *  - [ErrorCode.STUDENT_ALREADY_BOOKED]: 해당 학생에 예약 충돌이 있음
     */
    @Transactional
    fun createLesson(
        request: LessonRequest,
        studentId: Long,
    ): LessonResponse {
        validateStartAt(request.startAt)

        val teacher = findTeacherOrThrow(request.teacherId)
        val student = findStudentOrThrow(studentId)
        val course = findCourseOrThrow(request.courseId)

        val teacherId = requireNotNull(teacher.id) { "Teacher id must not be null for existing teacher" }
        val studentIdNonNull = requireNotNull(student.id) { "Student id must not be null for existing student" }

        ensureTeacherAvailable(teacherId, request.startAt)
        ensureStudentAvailable(studentIdNonNull, request.startAt)

        val lesson =
            Lesson(
                null,
                course,
                teacher,
                student,
                LessonStatus.BOOKED,
                request.startAt,
                request.startAt.plusMinutes(20),
            )

        val saved = lessonRepository.save(lesson)
        return LessonResponse.from(saved)
    }

    /**
     * 시작 시각 유효성 검사: 분(minute)은 0 또는 30이어야 하고, 초/나노 초는 0이어야 합니다.
     * 위 조건을 만족하지 않으면 [CustomException]과 [ErrorCode.INVALID_START_AT]를 던집니다.
     */
    private fun validateStartAt(startAt: LocalDateTime) {
        if (startAt.minute % 30 != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw CustomException(ErrorCode.INVALID_START_AT)
        }
    }

    /**
     * 주어진 교사 ID로 교사를 조회합니다. 존재하지 않으면 [CustomException]을 던집니다.
     *
     * @throws CustomException ErrorCode.TEACHER_NOT_FOUND
     */
    private fun findTeacherOrThrow(teacherId: Long) =
        teacherRepository
            .findById(teacherId)
            .orElseThrow { CustomException(ErrorCode.TEACHER_NOT_FOUND) }

    /**
     * 주어진 학생 ID로 학생을 조회합니다. 존재하지 않으면 [CustomException]을 던집니다.
     *
     * @throws CustomException ErrorCode.STUDENT_NOT_FOUND
     */
    private fun findStudentOrThrow(studentId: Long) =
        studentRepository
            .findById(studentId)
            .orElseThrow { CustomException(ErrorCode.STUDENT_NOT_FOUND) }

    /**
     * 주어진 코스 ID로 코스를 조회합니다. 존재하지 않으면 [CustomException]을 던집니다.
     *
     * @throws CustomException ErrorCode.COURSE_NOT_FOUND
     */
    private fun findCourseOrThrow(courseId: Long) =
        courseRepository
            .findById(courseId)
            .orElseThrow { CustomException(ErrorCode.COURSE_NOT_FOUND) }

    /**
     * 해당 교사가 특정 시작 시각에 이미 예약되어 있는지 확인합니다. 예약이 있으면 예외를 던집니다.
     *
     * @throws CustomException ErrorCode.TEACHER_ALREADY_BOOKED
     */
    private fun ensureTeacherAvailable(
        teacherId: Long,
        startAt: LocalDateTime,
    ) {
        if (lessonRepository.existsBookedLessonForTeacher(teacherId, startAt, LessonStatus.BOOKED)) {
            throw CustomException(ErrorCode.TEACHER_ALREADY_BOOKED)
        }
    }

    /**
     * 해당 학생이 특정 시작 시각에 이미 예약되어 있는지 확인합니다. 예약이 있으면 예외를 던집니다.
     *
     * @throws CustomException ErrorCode.STUDENT_ALREADY_BOOKED
     */
    private fun ensureStudentAvailable(
        studentId: Long,
        startAt: LocalDateTime,
    ) {
        if (lessonRepository.existsBookedLessonForStudent(studentId, startAt, LessonStatus.BOOKED)) {
            throw CustomException(ErrorCode.STUDENT_ALREADY_BOOKED)
        }
    }
}
