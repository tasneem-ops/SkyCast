package com.example.skycast.model.repository

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.model.local.ILocalDataSource
import com.example.skycast.model.network.IRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class Repository(val remoteDataSource: IRemoteDataSource, val localDataSource: ILocalDataSource) {
    companion object{
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance (remoteDataSource: IRemoteDataSource,localDataSource: ILocalDataSource): Repository {
            return INSTANCE ?: synchronized(this){
                val instance = Repository(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
    fun getDailyWeather(latLng: LatLng, apiKey: String,lang : String, forceUpdate : Boolean = false) : Flow<List<DailyWeather>> {
        if(forceUpdate){
            return remoteDataSource.getDailyForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
            return localDataSource.getDailyWeather(dt, lang)
        }
    }
    fun getHourlyWeather(latLng: LatLng, apiKey: String, lang: String, forceUpdate: Boolean) : Flow<List<HourlyWeather>>{
        if(forceUpdate){
            return remoteDataSource.getHourlyForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
            return localDataSource.getHourlyWeatherForecast(dt, lang)
        }
    }
    fun getCurrentWeather(latLng: LatLng, apiKey: String, lang: String, forceUpdate: Boolean) : Flow<HourlyWeather?>{
        if(forceUpdate){
            return remoteDataSource.getCurrentForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
            return localDataSource.getCurrentWeatherForecast(dt, lang)
        }
    }
    suspend fun updateWeatherCache(latLng: LatLng, apiKey: String){
        val clearDatabaseJob = GlobalScope.launch {
            localDataSource.clearDailyWeather()
            localDataSource.clearHourlyWeather()
        }
        clearDatabaseJob.join()
        val hourlyEnFlow = remoteDataSource.getHourlyForecast(latLng, apiKey, "metric", "en")
        hourlyEnFlow.collectLatest {
            localDataSource.insertHourlyWeather(*it.toTypedArray())
        }
        val hourlyArFlow = remoteDataSource.getHourlyForecast(latLng, apiKey, "metric", "ar")
        hourlyArFlow.collectLatest {
            localDataSource.insertHourlyWeather(*it.toTypedArray())
        }
        val dailyEnFlow = remoteDataSource.getDailyForecast(latLng, apiKey, "metric", "en")
        dailyEnFlow.collectLatest {
            localDataSource.insertDailyWeather(*it.toTypedArray())
        }
        val dailyArFlow = remoteDataSource.getDailyForecast(latLng, apiKey, "metric", "ar")
        dailyArFlow.collectLatest {
            localDataSource.insertDailyWeather(*it.toTypedArray())
        }
    }


}