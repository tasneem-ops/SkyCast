package com.example.skycast.home.model.dto

data class Daily(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val moon_phase: Double,
    val summary: String,
    val temp: Temp,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Double,
    val weather: List<Weather>,
    val clouds: Int,
    val uvi: Double
)