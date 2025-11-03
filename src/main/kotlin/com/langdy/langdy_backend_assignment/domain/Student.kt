package com.langdy.langdy_backend_assignment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @Enumerated(EnumType.STRING)
    var os: Os = Os.ANDROID
) {
    // JPA requires a no-arg constructor
    constructor() : this(null, "", Os.ANDROID)
}
