package com.langdy.assignment.controller

import com.langdy.assignment.dto.api.LessonRequest
import com.langdy.assignment.dto.api.LessonResponse
import com.langdy.assignment.service.LessonService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lessons")
class LessonController(
    private val lessonService: LessonService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createLesson(
        @RequestHeader("X-Student-Id") studentId: Long,
        @Valid @RequestBody request: LessonRequest,
    ): LessonResponse = lessonService.createLesson(request, studentId)
}
