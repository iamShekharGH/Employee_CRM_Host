package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val status: Int, val message: String)

@Serializable
data class Success<T>(val status: Int, val message: T)