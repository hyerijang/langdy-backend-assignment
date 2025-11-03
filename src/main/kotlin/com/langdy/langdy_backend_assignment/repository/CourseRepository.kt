package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.Course
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course, Long>

