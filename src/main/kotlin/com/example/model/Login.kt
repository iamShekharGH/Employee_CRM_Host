package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val id: Int,
    val username: String,
    val password: String,
    val authToken: String
)