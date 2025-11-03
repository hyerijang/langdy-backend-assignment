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

        // All teachers can teach any course (assumption)
        val teachers = teacherRepository.findAll()
        return teachers.filter { t ->
            if (t.id == null) return@filter false
            !lessonRepository.existsByTeacherIdAndStartAtAndStatus(t.id!!, startAt, LessonStatus.BOOKED)
        }.map { TeacherDto(it.id!!, it.name) }
    }

    fun createLesson(request: LessonRequest, studentId: Long): LessonResponse {
        val startAt = parseStartAt(request.startAt ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt is required"))
        val courseId = request.courseId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required")
        val teacherId = request.teacherId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "teacherId is required")

        // Validate minute boundary (00 or 30) and seconds/nanos
        if (startAt.minute % 30 != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt must be on :00 or :30 with zero seconds")
        }

        val teacher = teacherRepository.findById(teacherId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "teacher not found") }
        val student = studentRepository.findById(studentId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "student not found") }
        val course = courseRepository.findById(courseId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "course not found") }

        // Check existing bookings for teacher and student at the given startAt
        if (lessonRepository.existsByTeacherIdAndStartAtAndStatus(teacherId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "teacher is not available at requested time")
        }
        if (lessonRepository.existsByStudentIdAndStartAtAndStatus(studentId, startAt, LessonStatus.BOOKED)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "student is not available at requested time")
        }

        val endAt = startAt.plusMinutes(20)
        val lesson = Lesson(
            courseId = courseId,
            teacherId = teacherId,
            studentId = studentId,
            status = LessonStatus.BOOKED,
            startAt = startAt,
            endAt = endAt
        )
        val saved = lessonRepository.save(lesson)

        return LessonResponse(
            id = saved.id!!,
            courseId = saved.courseId,
            teacherId = saved.teacherId,
            studentId = saved.studentId,
            status = saved.status,
            startAt = saved.startAt,
            endAt = saved.endAt
        )
    }

    private fun parseStartAt(startAtStr: String): LocalDateTime {
        try {
            return LocalDateTime.parse(startAtStr)
        } catch (e: DateTimeParseException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "startAt must be a valid ISO-8601 datetime: ${e.message}")
        }
    }
}
