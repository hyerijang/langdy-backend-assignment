package com.langdy.langdy_backend_assignment.controller

import com.langdy.langdy_backend_assignment.dto.LessonRequest
import com.langdy.langdy_backend_assignment.dto.LessonResponse
import com.langdy.langdy_backend_assignment.dto.TeacherDto
import com.langdy.langdy_backend_assignment.service.LessonService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class LessonController(
    private val lessonService: LessonService
) {
    @GetMapping("/teachers/available")
    fun getAvailableTeachers(
        @RequestParam courseId: Long,
        @RequestParam startAt: String
    ): List<TeacherDto> {
        return lessonService.findAvailableTeachers(courseId, startAt)
    }

    @PostMapping("/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    fun createLesson(
        @RequestHeader("X-Student-Id") studentId: Long,
        @Valid @RequestBody request: LessonRequest
    ): LessonResponse {
        return lessonService.createLesson(request, studentId)
    }
}

