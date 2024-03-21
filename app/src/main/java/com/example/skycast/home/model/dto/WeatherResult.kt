package com.example.skycast.home.model.dto

data class WeatherResult(
    var current: HourlyWeather?,
    var hourly : List<HourlyWeather>,
    var daily : List<DailyWeather>
)