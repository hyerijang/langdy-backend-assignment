package com.langdy.langdy_backend_assignment.service

import com.langdy.langdy_backend_assignment.domain.Lesson
import com.langdy.langdy_backend_assignment.domain.LessonStatus
import com.langdy.langdy_backend_assignment.dto.LessonRequest
import com.langdy.langdy_backend_assignment.dto.LessonResponse
import com.langdy.langdy_backend_assignment.dto.TeacherDto
import com.langdy.langdy_backend_assignment.repository.CourseRepository
import com.langdy.langdy_backend_assignment.repository.LessonRepository
import com.langdy.langdy_backend_assignment.repository.StudentRepository
import com.langdy.langdy_backend_assignment.repository.TeacherRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) {
    fun findAvailableTeachers(courseId: Long, startAtStr: String): List<TeacherDto> {
        val startAt = parseStartAt(startAtStr)
        // 분 검사 (:00 또는 :30) 및 초/나노초가 0인지 확인
        validateStartAt(startAt)
        // 수업 가능한 선생님 목록 조회
        return teacherRepository.findAvailableTeachers(courseId, startAt)
    }

    fun createLesson(request: LessonRequest, studentId: Long): LessonResponse {
        val startAt =
            parseStartAt(request.startAt ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt은 필수입니다"))
        val courseId = request.courseId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId는 필수입니다")
        val teacherId = request.teacherId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "teacherId는 필수입니다")

        // 분 검사 (:00 또는 :30) 및 초/나노초가 0인지 확인
        validateStartAt(startAt)

        val teacher = teacherRepository.findById(teacherId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "선생님을 찾을 수 없습니다") }
        val student = studentRepository.findById(studentId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다") }
        val course = courseRepository.findById(courseId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "강좌를 찾을 수 없습니다") }

        // 선생님과 학생의 해당 시간 예약 여부 확인
        if (lessonRepository.existsBookedLessonForTeacher(teacherId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "요청한 시간에 선생님이 예약되어 있습니다")
        }
        if (lessonRepository.existsBookedLessonForStudent(studentId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "요청한 시간에 학생이 예약되어 있습니다")
        }

        val endAt = startAt.plusMinutes(20)
        val lesson = Lesson(
            course = course,
            teacher = teacher,
            student = student,
            status = LessonStatus.BOOKED,
            startAt = startAt,
            endAt = endAt
        )
        val saved = lessonRepository.save(lesson)

        return LessonResponse(
            id = saved.id!!,
            courseId = saved.course?.id ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "수업에 강좌 정보가 없습니다"
            ),
            teacherId = saved.teacher?.id ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "수업에 선생님 정보가 없습니다"
            ),
            studentId = saved.student?.id ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "수업에 학생 정보가 없습니다"
            ),
            status = saved.status,
            startAt = saved.startAt,
            endAt = saved.endAt
        )
    }

    private fun parseStartAt(startAtStr: String): LocalDateTime {
        try {
            return LocalDateTime.parse(startAtStr)
        } catch (e: DateTimeParseException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt은 유효한 ISO-8601 날짜시간이어야 합니다: ${e.message}")
        }
    }

    private fun validateStartAt(startAt: LocalDateTime) {
        if (startAt.minute % 30 != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt은 분이 :00 또는 :30이어야 하며 초와 나노초는 0이어야 합니다")
        }
    }
}
