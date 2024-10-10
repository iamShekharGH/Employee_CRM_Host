package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val authKey: String,
    val userInformation: UserInformation
)