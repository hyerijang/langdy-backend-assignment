package com.langdy.langdy_backend_assignment

import com.langdy.langdy_backend_assignment.domain.Course
import com.langdy.langdy_backend_assignment.domain.Os
import com.langdy.langdy_backend_assignment.domain.Student
import com.langdy.langdy_backend_assignment.domain.Teacher
import com.langdy.langdy_backend_assignment.repository.CourseRepository
import com.langdy.langdy_backend_assignment.repository.StudentRepository
import com.langdy.langdy_backend_assignment.repository.TeacherRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * 개발/테스트용 데이터 로더
 * - 애플리케이션 시작 시 TEACHER, STUDENT, COURSE에 샘플 데이터를 삽입합니다 (없을 때만)
 * - 실무에서는 별도의 데이터 마이그레이션/시드 처리(예: Flyway)를 권장합니다.
 */
@Component
class DataLoader(
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (teacherRepository.count() == 0L) {
            val t1 = Teacher(name = "Teacher A")
            val t2 = Teacher(name = "Teacher B")
            val t3 = Teacher(name = "Teacher C")
            teacherRepository.saveAll(listOf(t1, t2, t3))
            println("[DataLoader] Inserted sample teachers")
        } else {
            println("[DataLoader] Teachers already present: ${teacherRepository.count()}")
        }

        if (studentRepository.count() == 0L) {
            val s1 = Student(name = "Student X", os = Os.ANDROID)
            val s2 = Student(name = "Student Y", os = Os.IOS)
            studentRepository.saveAll(listOf(s1, s2))
            println("[DataLoader] Inserted sample student")
        } else {
            println("[DataLoader] Students already present: ${studentRepository.count()}")
        }

        if (courseRepository.count() == 0L) {
            val c = Course(name = "Java Basic")
            courseRepository.save(c)
            println("[DataLoader] Inserted sample course")
        } else {
            println("[DataLoader] Courses already present: ${courseRepository.count()}")
        }
    }
}
