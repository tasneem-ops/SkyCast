package com.example.skycast.home.model.dto

import com.example.skycast.alert.model.dto.Alert

data class Forecast(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>
)