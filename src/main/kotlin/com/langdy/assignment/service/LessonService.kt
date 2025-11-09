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

@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) {
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

    private fun validateStartAt(startAt: LocalDateTime) {
        if (startAt.minute % 30 != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw CustomException(ErrorCode.INVALID_START_AT)
        }
    }

    private fun findTeacherOrThrow(teacherId: Long) =
        teacherRepository
            .findById(teacherId)
            .orElseThrow { CustomException(ErrorCode.TEACHER_NOT_FOUND) }

    private fun findStudentOrThrow(studentId: Long) =
        studentRepository
            .findById(studentId)
            .orElseThrow { CustomException(ErrorCode.STUDENT_NOT_FOUND) }

    private fun findCourseOrThrow(courseId: Long) =
        courseRepository
            .findById(courseId)
            .orElseThrow { CustomException(ErrorCode.COURSE_NOT_FOUND) }

    private fun ensureTeacherAvailable(
        teacherId: Long,
        startAt: LocalDateTime,
    ) {
        if (lessonRepository.existsBookedLessonForTeacher(teacherId, startAt, LessonStatus.BOOKED)) {
            throw CustomException(ErrorCode.TEACHER_ALREADY_BOOKED)
        }
    }

    private fun ensureStudentAvailable(
        studentId: Long,
        startAt: LocalDateTime,
    ) {
        if (lessonRepository.existsBookedLessonForStudent(studentId, startAt, LessonStatus.BOOKED)) {
            throw CustomException(ErrorCode.STUDENT_ALREADY_BOOKED)
        }
    }
}
