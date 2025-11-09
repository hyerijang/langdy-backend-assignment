package com.langdy.assignment.service

import com.langdy.assignment.domain.Course
import com.langdy.assignment.domain.Student
import com.langdy.assignment.domain.Teacher
import com.langdy.assignment.repository.CourseRepository
import com.langdy.assignment.repository.LessonRepository
import com.langdy.assignment.repository.StudentRepository
import com.langdy.assignment.repository.TeacherRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var lessonService: LessonService

    @Autowired
    protected lateinit var teacherService: TeacherService

    @Autowired
    protected lateinit var lessonRepository: LessonRepository

    @Autowired
    protected lateinit var teacherRepository: TeacherRepository

    @Autowired
    protected lateinit var studentRepository: StudentRepository

    @Autowired
    protected lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun clearDatabase() {
        // 외래키 제약을 피하기 위해 Lesson을 먼저 삭제
        lessonRepository.deleteAll()
        lessonRepository.flush()

        // 나머지 엔티티 삭제 후 flush
        teacherRepository.deleteAll()
        teacherRepository.flush()

        studentRepository.deleteAll()
        studentRepository.flush()

        courseRepository.deleteAll()
        courseRepository.flush()
    }

    protected fun createTeacher(name: String) =
        teacherRepository.save(
            Teacher(name = name),
        )

    protected fun createStudent(name: String) =
        studentRepository.save(
            Student(name = name),
        )

    protected fun createCourse(name: String = "course") =
        courseRepository.save(
            Course(name = name),
        )
}
