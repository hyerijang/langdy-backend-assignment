package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long>

