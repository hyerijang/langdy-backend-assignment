package com.langdy.assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

/**
 * 강사를 나타내는 엔티티입니다.
 *
 * @property id DB 상의 식별자 (자동 생성)
 * @property name 강사 이름
 */
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
