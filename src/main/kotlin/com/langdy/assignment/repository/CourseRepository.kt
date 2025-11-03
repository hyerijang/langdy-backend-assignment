package com.langdy.assignment.repository

import com.langdy.assignment.domain.Course
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course, Long>
