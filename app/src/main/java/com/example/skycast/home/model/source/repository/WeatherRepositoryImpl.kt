package com.example.skycast.home.model.source.repository

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.example.skycast.home.model.source.local.WeatherLocalDataSource
import com.example.skycast.home.model.source.network.WeatherRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.Date

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    WeatherRepository {
    companion object{
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null
        fun getInstance (remoteDataSource: WeatherRemoteDataSource, localDataSource: WeatherLocalDataSource,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this){
                val instance = WeatherRepositoryImpl(remoteDataSource, localDataSource, ioDispatcher)
                INSTANCE = instance
                instance
            }
        }
    }
    override fun getDailyWeather(latLng: LatLng, apiKey: String, lang : String, forceUpdate : Boolean) : Flow<List<DailyWeather>> {
        if(forceUpdate){
            return remoteDataSource.getDailyForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt() - (15* 3600)
            return localDataSource.getDailyWeather(dt, lang)
        }
    }
    override fun getHourlyWeather(latLng: LatLng, apiKey: String, lang: String, forceUpdate: Boolean) : Flow<List<HourlyWeather>>{
        if(forceUpdate){
            return remoteDataSource.getHourlyForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
            return localDataSource.getHourlyWeatherForecast(dt, lang)
        }
    }
    override fun getCurrentWeather(latLng: LatLng, apiKey: String, lang: String, forceUpdate: Boolean) : Flow<HourlyWeather?>{
        if(forceUpdate){
            return remoteDataSource.getCurrentForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
            return localDataSource.getCurrentWeatherForecast(dt, lang)
        }
    }
    override suspend fun updateWeatherCache(latLng: LatLng, apiKey: String) = withContext(ioDispatcher){
        localDataSource.clearDailyWeather()
        localDataSource.clearHourlyWeather()
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


    override fun getSearchSuggestions(
        query: String,
        format: String,
        lang: String,
        limit: Int
    ): Flow<List<Place>> {
        return remoteDataSource.getSearchSuggestions(query, format, lang, limit)
    }

}