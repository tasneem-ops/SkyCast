package com.example.skycast.model.local

import android.content.Context
import com.example.skycast.home.model.db.DailyWeatherDao
import com.example.skycast.home.model.db.HourlyWeatherDao
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.flow.Flow

class LocalDataSource private constructor(val context: Context) : ILocalDataSource {
    private var dailyWeatherDao: DailyWeatherDao = WeatherDB.getInstance(context).getDailyWeatherDao()
    private var hourlyWeatherDao: HourlyWeatherDao = WeatherDB.getInstance(context).getHourlyWeatherDao()
    companion object{
        @Volatile
        private var INSTANCE: LocalDataSource? = null
        fun getInstance (context: Context): LocalDataSource{
            return INSTANCE ?: synchronized(this){
                val instance = LocalDataSource(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getDailyWeather(dt: Int, lang : String) : Flow<List<DailyWeather>> {
        return dailyWeatherDao.getDailyWeatherForecast(dt, lang)
    }
    override suspend fun clearDailyWeather() : Int{
        return dailyWeatherDao.clear()
    }
    override suspend fun insertDailyWeather(vararg dailyWeather: DailyWeather) : List<Long>{
        return dailyWeatherDao.insertAll(*dailyWeather)
    }
    override fun getHourlyWeatherForecast(dt : Int, lang : String) : Flow<List<HourlyWeather>>{
        return hourlyWeatherDao.getHourlyWeatherForecast(dt, lang)
    }
    override fun getCurrentWeatherForecast(dt : Int, lang : String) : Flow<HourlyWeather>{
        return hourlyWeatherDao.getCurrentWeatherForecast(dt, lang)
    }
    override suspend fun insertHourlyWeather(vararg list : HourlyWeather) : List<Long>{
        return hourlyWeatherDao.insertAll(*list)
    }
    override suspend fun clearHourlyWeather() : Int{
        return hourlyWeatherDao.clear()
    }
}