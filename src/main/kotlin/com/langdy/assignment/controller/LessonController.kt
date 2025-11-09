package com.langdy.assignment.controller

import com.langdy.assignment.dto.api.LessonRequest
import com.langdy.assignment.dto.api.LessonResponse
import com.langdy.assignment.service.LessonService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/lessons")
class LessonController(
    private val lessonService: LessonService,
) {
    @PostMapping
    fun createLesson(
        @RequestHeader("X-Student-Id") studentId: Long,
        @Valid @RequestBody request: LessonRequest,
    ): ResponseEntity<LessonResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(request, studentId))
}
