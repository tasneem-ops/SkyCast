package com.example.skycast.home.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_weather", primaryKeys = ["dt", "lang"])
data class HourlyWeather(
    val dt: Int,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val weatherId : Int,
    val weatherDescription : String,
    var lang : String)
