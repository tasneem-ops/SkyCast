package com.example.skycast.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.HourForecast
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getAllForecast() : Flow<List<HourForecast>>
    suspend fun getTodayForecast() : Flow<List<HourForecast>>
    suspend fun insertHourForecast(hourForecast: HourForecast) : Long
    suspend fun insertAllForecast(vararg hourForecast: HourForecast) : List<Long>
    suspend fun deleteHourForecast(hourForecast: HourForecast) : Int
    suspend fun deleteAllForecast() : Int
}