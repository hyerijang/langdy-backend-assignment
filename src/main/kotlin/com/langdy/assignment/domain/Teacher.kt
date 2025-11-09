package com.langdy.assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Teacher(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
) {
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    val lessons: MutableList<Lesson> = mutableListOf()

    fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
        lesson.teacher = this
    }

    fun removeLesson(lesson: Lesson) {
        lessons.remove(lesson)
        if (lesson.teacher == this) {
            lesson.teacher = null
        }
    }
}
