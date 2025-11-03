package com.langdy.assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String
) {
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    val lessons: MutableList<Lesson> = mutableListOf()

    // helper to keep bidirectional relation in sync
    fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
        lesson.course = this
    }

    fun removeLesson(lesson: Lesson) {
        lessons.remove(lesson)
        if (lesson.course == this) {
            lesson.course = null
        }
    }

    constructor() : this(null, "")
}
