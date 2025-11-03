package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.Teacher
import org.springframework.data.jpa.repository.JpaRepository

interface TeacherRepository : JpaRepository<Teacher, Long>, TeacherRepositoryCustom
