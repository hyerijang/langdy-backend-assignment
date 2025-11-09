package com.langdy.assignment.service

import com.langdy.assignment.domain.Lesson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import java.time.LocalDateTime

@DisplayName("선생님 서비스 통합 테스트")
class TeacherServiceIntegrationTest : IntegrationTestBase() {
    @DisplayName("예약된 선생님은 가능한 선생님 목록에서 제외되어야 한다")
    @RepeatedTest(10)
    fun `findAvailableTeachers excludes booked teachers`() {
        val course = createCourse()
        val t1 = createTeacher("t1")
        val t2 = createTeacher("t2")
        val t3 = createTeacher("t3")
        val student = createStudent("s1")

        val startAt = LocalDateTime.of(2025, 11, 9, 10, 0)

        // t1이 해당 시간에 예약이 있다고 가정하고 테스트 배치
        val lesson =
            Lesson(
                course = course,
                teacher = t1,
                student = student,
                startAt = startAt,
                endAt = startAt.plusMinutes(20),
            )
        lessonRepository.save(lesson)

        val courseId = requireNotNull(course.id) { "course id must not be null" }
        val available = teacherService.findAvailableTeachers(courseId, startAt)
        val ids = available.map { it.id }

        assertThat(ids).doesNotContain(t1.id)
        assertThat(ids).contains(t2.id, t3.id)
    }
}
