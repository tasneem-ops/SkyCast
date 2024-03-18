package com.example.skycast.home.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.home.model.dto.DailyWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWeatherDao {
    @Query("SELECT * FROM daily_weather WHERE dt >= :currentDt AND lang = :lang ORDER BY dt ASC")
    fun getDailyWeatherForecast(currentDt : Int, lang : String) : Flow<List<DailyWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg list :DailyWeather) : List<Long>

    @Query("DELETE FROM daily_weather")
    suspend fun clear() : Int
}