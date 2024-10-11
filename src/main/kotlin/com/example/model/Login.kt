package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val id: Int,
    val username: String,
    val password: String,
    val authToken: String
)

@Serializable
data class LoginSqlState(
    val userFound: Boolean,
    val login: Login? = null,
)