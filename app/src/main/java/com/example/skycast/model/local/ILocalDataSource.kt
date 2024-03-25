package com.example.skycast.model.local

import androidx.room.Delete
import androidx.room.Query
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.dto.FavDTO
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
    fun getAlerts() : Flow<List<AlertDTO>>
    suspend fun addAlert(alert: AlertDTO) : Long
    suspend fun delete(alert: AlertDTO) : Int
    suspend fun addFav(favDTO: FavDTO) : Long
    fun getAllFav() : Flow<List<FavDTO>>
    suspend fun deleteFav(favDTO: FavDTO) : Int

}