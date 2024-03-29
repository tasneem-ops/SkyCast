package com.example.skycast.home.model.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface HourlyWeatherDao {
    @Query("SELECT * FROM hourly_weather WHERE dt >= :currentDt AND lang= :lang ORDER BY dt ASC LIMIT 24")
    fun getHourlyWeatherForecast(currentDt : Int, lang : String) : Flow<List<HourlyWeather>>

    @Query("SELECT * FROM hourly_weather WHERE dt >= :currentDt AND lang= :lang ORDER BY dt ASC LIMIT 1")
    fun getCurrentWeatherForecast(currentDt : Int, lang: String) : Flow<HourlyWeather>
    @Query("SELECT * FROM hourly_weather")
    fun getAll() : Flow<List<HourlyWeather>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg list : HourlyWeather) : List<Long>

    @Query("DELETE FROM hourly_weather")
    suspend fun clear() : Int
}