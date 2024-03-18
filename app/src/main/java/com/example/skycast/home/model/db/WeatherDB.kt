package com.example.skycast.home.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather

@Database(entities = arrayOf(HourlyWeather::class, DailyWeather::class), version = 1)
abstract class WeatherDB : RoomDatabase(){
    abstract fun getHourlyWeatherDao(): HourlyWeatherDao
    abstract fun getDailyWeatherDao(): DailyWeatherDao
    companion object{
        @Volatile
        private var INSTANCE: WeatherDB? = null
        fun getInstance (context: Context): WeatherDB{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDB::class.java, "weather_database")
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}