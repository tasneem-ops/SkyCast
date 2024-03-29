package com.example.skycast.model.local

import android.content.Context
import com.example.skycast.alert.model.db.AlertsDB
import com.example.skycast.alert.model.db.AlertsDao
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.db.FavDB
import com.example.skycast.favorites.model.db.FavDao
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.home.model.db.DailyWeatherDao
import com.example.skycast.home.model.db.HourlyWeatherDao
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSource private constructor(private val dailyWeatherDao : DailyWeatherDao,
                                          private val hourlyWeatherDao : HourlyWeatherDao,
                                          private var alertDao : AlertsDao,
                                          private val favDao : FavDao,
                                          private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    ) : ILocalDataSource {
    companion object{
        @Volatile
        private var INSTANCE: LocalDataSource? = null
        fun getInstance (dailyWeatherDao : DailyWeatherDao,
                         hourlyWeatherDao : HourlyWeatherDao,
                         alertDao : AlertsDao,
                         favDao : FavDao,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): LocalDataSource{
            return INSTANCE ?: synchronized(this){
                val instance = LocalDataSource(dailyWeatherDao, hourlyWeatherDao, alertDao, favDao, ioDispatcher)
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
        return hourlyWeatherDao.getCurrentWeatherForecast(dt, lang)
    }
    override suspend fun insertHourlyWeather(vararg list : HourlyWeather) : List<Long> = withContext(ioDispatcher){
        return@withContext hourlyWeatherDao.insertAll(*list)
    }
    override suspend fun clearHourlyWeather() : Int = withContext(ioDispatcher){
        return@withContext hourlyWeatherDao.clear()
    }

    override fun getAlerts(): Flow<List<AlertDTO>> {
        return alertDao.getAllAlerts()
    }

    override suspend fun addAlert(alert: AlertDTO): Long = withContext(ioDispatcher){
        return@withContext alertDao.addAlert(alert)
    }

    override suspend fun delete(alert: AlertDTO): Int = withContext(ioDispatcher){
        return@withContext alertDao.deleteAlert(alert)
    }

    override suspend fun addFav(favDTO: FavDTO): Long = withContext(ioDispatcher){
        return@withContext favDao.addFav(favDTO)
    }

    override fun getAllFav(): Flow<List<FavDTO>> {
        return favDao.getAllFav()
    }

    override suspend fun deleteFav(favDTO: FavDTO): Int = withContext(ioDispatcher){
        return@withContext favDao.deleteFav(favDTO)
    }
}