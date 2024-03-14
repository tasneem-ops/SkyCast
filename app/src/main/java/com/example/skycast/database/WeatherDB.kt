package com.example.skycast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.HourForecast

@Database(entities = arrayOf(HourForecast::class), version = 1 )
@TypeConverters(ListConverter::class)
abstract class WeatherDB : RoomDatabase() {
    abstract fun getForecastDao(): ForecastDao
    companion object{
        @Volatile
        private var INSTANCE: WeatherDB? = null
        fun getInstance (context: Context): WeatherDB{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDB::class.java, "forecast")
                    .build()
                INSTANCE = instance
                // return instance
                instance }
        }
    }
}