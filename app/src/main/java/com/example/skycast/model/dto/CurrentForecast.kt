package com.example.skycast.model.dto

data class CurrentForecast(
    var currentWeather : HourForecast,
    var todayForecast: List<HourForecast>,
    var dailyForecast: List<HourForecast>
)
