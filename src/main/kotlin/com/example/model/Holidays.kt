package com.example.model

import kotlinx.serialization.Serializable


@Serializable
data class EmployeeHolidays(
    val id: Int = 0,
    val holidayName: String,
    val holidayDate: String,
    val holidayType: String,
    val holidayReason: String,
    val isHolidayTaken: Boolean,
)