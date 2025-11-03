package com.langdy.assignment.controller

import com.langdy.assignment.dto.LessonRequest
import com.langdy.assignment.dto.LessonResponse
import com.langdy.assignment.dto.TeacherDto
import com.langdy.assignment.service.LessonService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class LessonController(
    private val lessonService: LessonService,
) {
    @GetMapping("/teachers/available")
    fun getAvailableTeachers(
        @RequestParam courseId: Long,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") startAt: LocalDateTime,
    ): List<TeacherDto> = lessonService.findAvailableTeachers(courseId, startAt)

    @PostMapping("/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    fun createLesson(
        @RequestHeader("X-Student-Id") studentId: Long,
        @Valid @RequestBody request: LessonRequest,
    ): LessonResponse = lessonService.createLesson(request, studentId)
}
