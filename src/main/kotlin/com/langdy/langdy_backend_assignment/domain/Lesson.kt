package com.langdy.langdy_backend_assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Lesson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var courseId: Long = 0,
    var teacherId: Long = 0,
    var studentId: Long = 0,
    @Enumerated(EnumType.STRING)
    var status: LessonStatus = LessonStatus.BOOKED,
    var startAt: LocalDateTime = LocalDateTime.now(),
    var endAt: LocalDateTime = LocalDateTime.now().plusMinutes(20)
) {
    constructor() : this(null, 0, 0, 0, LessonStatus.BOOKED, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20))
}

