package com.langdy.assignment.repository

import com.langdy.assignment.domain.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long>

