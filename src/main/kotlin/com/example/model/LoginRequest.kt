package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
enum class EmployeeGender { MALE, FEMALE }