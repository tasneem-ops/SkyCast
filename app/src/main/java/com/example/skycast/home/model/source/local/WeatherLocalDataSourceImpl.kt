package com.example.skycast.home.model.source.local


import com.example.skycast.home.model.db.DailyWeatherDao
import com.example.skycast.home.model.db.HourlyWeatherDao
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSourceImpl private constructor(private val dailyWeatherDao : DailyWeatherDao,
                                                     private val hourlyWeatherDao : HourlyWeatherDao,
                                                     private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    ) : WeatherLocalDataSource {
    companion object{
        @Volatile
        private var INSTANCE: WeatherLocalDataSourceImpl? = null
        fun getInstance (dailyWeatherDao : DailyWeatherDao,
                         hourlyWeatherDao : HourlyWeatherDao,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): WeatherLocalDataSourceImpl {
            return INSTANCE ?: synchronized(this){
                val instance = WeatherLocalDataSourceImpl(dailyWeatherDao, hourlyWeatherDao, ioDispatcher)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getDailyWeather(dt: Int, lang : String) : Flow<List<DailyWeather>> {
        return dailyWeatherDao.getDailyWeatherForecast(dt, lang)
    }

    override fun getAllDailyWeather(): Flow<List<DailyWeather>> {
        return dailyWeatherDao.getAllDaily()
    }

    override suspend fun clearDailyWeather() : Int = withContext(ioDispatcher){
        return@withContext dailyWeatherDao.clear()
    }
    override suspend fun insertDailyWeather(vararg dailyWeather: DailyWeather) : List<Long> = withContext(ioDispatcher){
        return@withContext dailyWeatherDao.insertAll(*dailyWeather)
    }
    override fun getHourlyWeatherForecast(dt : Int, lang : String) : Flow<List<HourlyWeather>>{
        return hourlyWeatherDao.getHourlyWeatherForecast(dt, lang)
    }

    override fun getAllHourlyWeatherForecast(): Flow<List<HourlyWeather>> {
        return hourlyWeatherDao.getAll()
    }

    override fun getCurrentWeatherForecast(dt : Int, lang : String) : Flow<HourlyWeather>{
        return hourlyWeatherDao.getCurrentWeatherForecast(dt - 3600, lang)
    }
    override suspend fun insertHourlyWeather(vararg list : HourlyWeather) : List<Long> = withContext(ioDispatcher){
        return@withContext hourlyWeatherDao.insertAll(*list)
    }
    override suspend fun clearHourlyWeather() : Int = withContext(ioDispatcher){
        return@withContext hourlyWeatherDao.clear()
    }


}