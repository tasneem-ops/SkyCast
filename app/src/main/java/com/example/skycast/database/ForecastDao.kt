package com.example.skycast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.HourForecast
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Query("SELECT * FROM hour_forecast")
    fun getAllForecast() : Flow<List<HourForecast>>

    @Query("SELECT * FROM hour_forecast ORDER BY dt ASC LIMIT 8")
    fun getTodayForecast() : Flow<List<HourForecast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourForecast(hourForecast: HourForecast) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForecast(vararg hourForecast: HourForecast) : List<Long>

    @Delete
    suspend fun deleteHourForecast(hourForecast: HourForecast) : Int

    @Query("DELETE FROM hour_forecast")
    suspend fun deleteAllForecast() : Int
}