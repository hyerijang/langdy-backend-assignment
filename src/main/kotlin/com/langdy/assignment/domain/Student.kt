package com.langdy.assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @Enumerated(EnumType.STRING)
    var os: Os = Os.ANDROID,
) {
    // JPA requires a no-arg constructor

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    val lessons: MutableList<Lesson> = mutableListOf()

    fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
        lesson.student = this
    }

    fun removeLesson(lesson: Lesson) {
        lessons.remove(lesson)
        if (lesson.student == this) {
            lesson.student = null
        }
    }
}
