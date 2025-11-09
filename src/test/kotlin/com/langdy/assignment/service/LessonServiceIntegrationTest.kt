package com.langdy.assignment.service

import com.langdy.assignment.domain.Lesson
import com.langdy.assignment.dto.LessonRequest
import com.langdy.assignment.repository.CourseRepository
import com.langdy.assignment.repository.LessonRepository
import com.langdy.assignment.repository.StudentRepository
import com.langdy.assignment.repository.TeacherRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@DisplayName("수업 서비스 통합 테스트")
class LessonServiceIntegrationTest {
    @Autowired
    lateinit var lessonService: LessonService

    @Autowired
    lateinit var lessonRepository: LessonRepository

    @Autowired
    lateinit var teacherRepository: TeacherRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    private fun createTeacher(name: String) =
        teacherRepository.save(
            com.langdy.assignment.domain
                .Teacher(name = name),
        )

    private fun createStudent(name: String) =
        studentRepository.save(
            com.langdy.assignment.domain
                .Student(name = name),
        )

    private fun createCourse(name: String = "course") =
        courseRepository.save(
            com.langdy.assignment.domain
                .Course(name = name),
        )

    @Test
    @DisplayName("Task1: 예약된 선생님은 가능한 선생님 목록에서 제외되어야 한다")
    fun `findAvailableTeachers excludes booked teachers`() {
        val course = createCourse()
        val t1 = createTeacher("t1")
        val t2 = createTeacher("t2")
        val t3 = createTeacher("t3")
        val student = createStudent("s1")

        val startAt = LocalDateTime.of(2025, 11, 9, 10, 0)

        // t1이 해당 시간에 예약이 있다고 가정하고 테스트 배치
        val lesson =
            Lesson(course = course, teacher = t1, student = student, startAt = startAt, endAt = startAt.plusMinutes(20))
        lessonRepository.save(lesson)

        val available = lessonService.findAvailableTeachers(course.id!!, startAt)
        val ids = available.map { it.id }

        assertThat(ids).doesNotContain(t1.id)
        assertThat(ids).contains(t2.id, t3.id)
    }

    @Test
    @DisplayName("Task2: 한 선생님에 대해 여러 학생이 동시 신청 시 하나만 성공해야 한다")
    fun `concurrent booking - same teacher multiple students only one succeeds`() {
        val course = createCourse()
        val teacher = createTeacher("teacher")
        val students = (1..5).map { createStudent("s$it") }
        val startAt = LocalDateTime.of(2025, 11, 9, 11, 0)

        val attempts = students.size
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(attempts)
        val executor = Executors.newFixedThreadPool(attempts)
        val successes = ConcurrentLinkedQueue<Long>()
        val errors = ConcurrentLinkedQueue<Throwable>()

        for (s in students) {
            executor.submit {
                try {
                    startLatch.await()
                    val req = LessonRequest(startAt = startAt, courseId = course.id!!, teacherId = teacher.id!!)
                    val resp = lessonService.createLesson(req, s.id!!)
                    successes.add(resp.id)
                } catch (e: Throwable) {
                    errors.add(e)
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        // 모든 스레드를 동시에 시작
        startLatch.countDown()
        doneLatch.await()
        executor.shutdown()

        // 해당 선생님+시간에 대해 정확히 하나의 레슨만 영속화되어야 함
        val lessons = lessonRepository.findAll().filter { it.startAt == startAt }
        assertThat(lessons.size).isEqualTo(1)
        assertThat(successes.size + errors.size).isEqualTo(attempts)

        // 실패한 요청들은 서비스의 충돌 예외(ResponseStatusException) 또는 DB 제약에 따른 예외여야 함
        assertThat(successes.size).isEqualTo(1)
        assertThat(errors.size).isEqualTo(attempts - 1)
        // ensure saved lesson is booked and references the teacher
        val saved = lessons.first()
        assertThat(saved.teacher?.id).isEqualTo(teacher.id)
    }

    @Test
    @DisplayName("Task2: 한 학생이 동일 시간에 여러 선생님을 동시 신청 시 하나만 성공해야 한다")
    fun `concurrent booking - same student multiple teachers only one succeeds`() {
        val course = createCourse()
        val student = createStudent("student")
        val teachers = (1..3).map { createTeacher("t$it") }
        val startAt = LocalDateTime.of(2025, 11, 9, 12, 0)

        val attempts = teachers.size
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(attempts)
        val executor = Executors.newFixedThreadPool(attempts)
        val successes = ConcurrentLinkedQueue<Long>()
        val errors = ConcurrentLinkedQueue<Throwable>()

        for (t in teachers) {
            executor.submit {
                try {
                    startLatch.await()
                    val req = LessonRequest(startAt = startAt, courseId = course.id!!, teacherId = t.id!!)
                    val resp = lessonService.createLesson(req, student.id!!)
                    successes.add(resp.id)
                } catch (e: Throwable) {
                    errors.add(e)
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        // 모든 스레드 동시 시작
        startLatch.countDown()
        doneLatch.await()
        executor.shutdown()

        // 동일 학생+시간에 대해 하나의 예약만 생성되어야 함
        val lessons = lessonRepository.findAll().filter { it.startAt == startAt }
        assertThat(lessons.size).isEqualTo(1)
        assertThat(successes.size).isEqualTo(1)
        assertThat(errors.size).isEqualTo(attempts - 1)
    }

    @Test
    @DisplayName("DB 제약: 동일 선생님+시간으로 직접 중복 레슨이 생성 될 수 없다")
    fun `repository duplicate teacher startAt throws constraint`() {
        val course = createCourse()
        val teacher = createTeacher("t-dup")
        val s1 = createStudent("s1")
        val s2 = createStudent("s2")
        val startAt = LocalDateTime.of(2025, 11, 9, 13, 0)

        // 첫 번째 레슨은 정상 저장
        val l1 =
            Lesson(course = course, teacher = teacher, student = s1, startAt = startAt, endAt = startAt.plusMinutes(20))
        lessonRepository.saveAndFlush(l1)

        // 동일 teacher + startAt 로 두번째 레슨을 저장하려 하면 DB 제약 위반이 발생해야 함
        val l2 =
            Lesson(course = course, teacher = teacher, student = s2, startAt = startAt, endAt = startAt.plusMinutes(20))
        org.assertj.core.api.Assertions
            .assertThatThrownBy {
                lessonRepository.saveAndFlush(l2)
            }.isInstanceOf(org.springframework.dao.DataIntegrityViolationException::class.java)
    }

    @Test
    @DisplayName("DB 제약: 동일 학생+시간으로 직접 중복 레슨이 생성 될 수 없다")
    fun `repository duplicate student startAt throws constraint`() {
        val course = createCourse()
        val teacher1 = createTeacher("t-dup-1")
        val teacher2 = createTeacher("t-dup-2")
        val student = createStudent("s-dup")
        val startAt = LocalDateTime.of(2025, 11, 9, 14, 0)

        // 첫 번째 레슨은 정상 저장
        val l1 =
            Lesson(
                course = course,
                teacher = teacher1,
                student = student,
                startAt = startAt,
                endAt = startAt.plusMinutes(20),
            )
        lessonRepository.saveAndFlush(l1)

        // 동일 student + startAt 로 두번째 레슨을 저장하려 하면 DB 제약 위반이 발생해야 함
        val l2 =
            Lesson(
                course = course,
                teacher = teacher2,
                student = student,
                startAt = startAt,
                endAt = startAt.plusMinutes(20),
            )
        org.assertj.core.api.Assertions
            .assertThatThrownBy {
                lessonRepository.saveAndFlush(l2)
            }.isInstanceOf(org.springframework.dao.DataIntegrityViolationException::class.java)
    }
}
