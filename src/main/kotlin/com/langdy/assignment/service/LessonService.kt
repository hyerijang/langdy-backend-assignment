package com.langdy.assignment.service

import com.langdy.assignment.domain.Lesson
import com.langdy.assignment.domain.LessonStatus
import com.langdy.assignment.dto.LessonRequest
import com.langdy.assignment.dto.LessonResponse
import com.langdy.assignment.dto.TeacherDto
import com.langdy.assignment.repository.CourseRepository
import com.langdy.assignment.repository.LessonRepository
import com.langdy.assignment.repository.StudentRepository
import com.langdy.assignment.repository.TeacherRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) {

    fun findAvailableTeachers(courseId: Long, startAt: LocalDateTime): List<TeacherDto> {
        validateStartAt(startAt)
        return teacherRepository.findAvailableTeachers(courseId, startAt)
    }

    fun createLesson(request: LessonRequest, studentId: Long): LessonResponse {
        validateStartAt(request.startAt)

        val teacher = findTeacherOrThrow(request.teacherId)
        val student = findStudentOrThrow(studentId)
        val course = findCourseOrThrow(request.courseId)

        ensureTeacherAvailable(teacher.id!!, request.startAt)
        ensureStudentAvailable(student.id!!, request.startAt)

        val lesson = Lesson(
            course = course,
            teacher = teacher,
            student = student,
            status = LessonStatus.BOOKED,
            startAt = request.startAt,
            endAt = request.startAt.plusMinutes(20),
        )

        val saved = lessonRepository.save(lesson)
        return LessonResponse.from(saved)
    }

    private fun validateStartAt(startAt: LocalDateTime) {
        if (startAt.minute % 30 != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "startAt은 분이 :00 또는 :30이어야 하며 초와 나노초는 0이어야 합니다"
            )
        }
    }

    private fun findTeacherOrThrow(teacherId: Long) =
        teacherRepository.findById(teacherId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "선생님을 찾을 수 없습니다") }

    private fun findStudentOrThrow(studentId: Long) =
        studentRepository.findById(studentId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "학생을 찾을 수 없습니다") }

    private fun findCourseOrThrow(courseId: Long) =
        courseRepository.findById(courseId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "강좌를 찾을 수 없습니다") }

    private fun ensureTeacherAvailable(teacherId: Long, startAt: LocalDateTime) {
        if (lessonRepository.existsBookedLessonForTeacher(teacherId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "요청한 시간에 선생님이 예약되어 있습니다")
        }
    }

    private fun ensureStudentAvailable(studentId: Long, startAt: LocalDateTime) {
        if (lessonRepository.existsBookedLessonForStudent(studentId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "요청한 시간에 학생이 예약되어 있습니다")
        }
    }


    private fun internalError(message: String): ResponseStatusException =
        ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message)
}
