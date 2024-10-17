package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val status: Int,
    val authKey: String,
    val data: UserInformation
)


@Serializable
data class EmployeeResponse(
    val status: Int,
    val data:  List<Employee>
)