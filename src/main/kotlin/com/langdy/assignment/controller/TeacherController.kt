package com.langdy.assignment.controller

import com.langdy.assignment.dto.projection.TeacherQueryDto
import com.langdy.assignment.service.TeacherService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/teachers")
class TeacherController(
    private val teacherService: TeacherService,
) {
    @GetMapping("/available")
    fun getAvailableTeachers(
        @RequestParam courseId: Long,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") startAt: LocalDateTime,
    ): List<TeacherQueryDto> = teacherService.findAvailableTeachers(courseId, startAt)
}
