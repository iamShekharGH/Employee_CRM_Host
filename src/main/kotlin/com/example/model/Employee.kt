package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val eid: Int,
    val name: String,
    val gender: Gender,
    val title: String,
    val photoUrl: String = "",
    val presentToday: Boolean,
    val salaryCredited: Boolean,
)

@Serializable
enum class Gender { MALE, FEMALE }