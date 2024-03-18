package com.example.skycast.home.model.dto

import androidx.room.Entity

@Entity(tableName = "daily_weather", primaryKeys = ["dt", "lang"])
data class DailyWeather(
    val dt: Int,
    val moon_phase: Double,
    val summary: String,
    val minTemp: Double,
    val maxTemp: Double,
    val pressure: Int,
    val humidity: Int,
    val wind_speed: Double,
    val weatherId : Int,
    val weatherDescription : String,
    val clouds: Int,
    val uvi: Double,
    var lang : String)
