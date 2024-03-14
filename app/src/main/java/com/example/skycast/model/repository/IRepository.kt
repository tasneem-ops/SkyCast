package com.example.skycast.model.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.model.dto.Forecast
import com.example.skycast.model.dto.HourForecast
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getApiForecast(lat: Double, lng: Double, apiKey: String,
                               units: String, lang: String) : Flow<Forecast>

    suspend fun getLocalForecast() : Flow<List<HourForecast>>

    suspend fun getTodayForecast() : Flow<List<HourForecast>>

    suspend fun insertHourForecast(hourForecast: HourForecast) : Long

    suspend fun insertAllForecast(vararg hourForecast: HourForecast) : List<Long>

    suspend fun deleteHourForecast(hourForecast: HourForecast) : Int

    suspend fun deleteAllForecast() : Int
}