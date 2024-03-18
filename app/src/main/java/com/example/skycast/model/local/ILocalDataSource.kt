package com.example.skycast.model.local

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getDailyWeather(dt: Int, lang : String): Flow<List<DailyWeather>>
    suspend fun clearDailyWeather(): Int
    suspend fun insertDailyWeather(vararg dailyWeather: DailyWeather): List<Long>
    fun getHourlyWeatherForecast(dt: Int, lang : String): Flow<List<HourlyWeather>>
    fun getCurrentWeatherForecast(dt: Int, lang : String): Flow<HourlyWeather>
    suspend fun insertHourlyWeather(vararg list: HourlyWeather): List<Long>
    suspend fun clearHourlyWeather(): Int
}