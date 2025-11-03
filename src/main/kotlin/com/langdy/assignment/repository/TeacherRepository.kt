package com.langdy.assignment.repository

import com.langdy.assignment.domain.Teacher
import org.springframework.data.jpa.repository.JpaRepository

interface TeacherRepository :
    JpaRepository<Teacher, Long>,
    TeacherRepositoryCustom
