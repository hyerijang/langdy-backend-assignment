package com.langdy.assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

/**
 * 수업(레슨)을 나타내는 엔티티입니다.
 *
 * 고유 제약조건으로 동일한 강사/학생이 같은 시작 시각에 중복 예약되지 않도록 설정되어 있습니다.
 *
 * @property id DB 상의 식별자 (자동 생성)
 * @property course 레슨이 속한 코스
 * @property teacher 레슨을 진행하는 강사
 * @property student 수강생
 * @property status 레슨 상태 ([LessonStatus])
 * @property startAt 레슨 시작 시각
 * @property endAt 레슨 종료 시각
 */
@Entity
@Table(
    name = "lesson",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_lesson_teacher_start_at", columnNames = ["teacher_id", "start_at"]),
        UniqueConstraint(name = "uq_lesson_student_start_at", columnNames = ["student_id", "start_at"]),
    ],
)
class Lesson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    var course: Course? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    var teacher: Teacher? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student? = null,
    @Enumerated(EnumType.STRING)
    var status: LessonStatus = LessonStatus.BOOKED,
    var startAt: LocalDateTime = LocalDateTime.now(),
    var endAt: LocalDateTime = LocalDateTime.now().plusMinutes(20),
)
