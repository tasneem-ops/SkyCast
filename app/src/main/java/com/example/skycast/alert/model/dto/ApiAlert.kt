package com.example.skycast.alert.model.dto

import com.example.skycast.alert.model.dto.Alert

data class ApiAlert(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val alerts: List<Alert>
)