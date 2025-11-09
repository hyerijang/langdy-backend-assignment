package com.langdy.assignment.repository

import com.langdy.assignment.domain.Lesson
import org.springframework.data.jpa.repository.JpaRepository

interface LessonRepository :
    JpaRepository<Lesson, Long>,
    LessonRepositoryCustom
