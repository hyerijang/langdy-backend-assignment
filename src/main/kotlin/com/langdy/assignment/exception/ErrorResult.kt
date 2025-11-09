package com.langdy.assignment.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResult(
    val code: String,
    val message: String,
    val details: String? = null,
)
