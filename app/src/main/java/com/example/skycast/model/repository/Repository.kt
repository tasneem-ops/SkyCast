package com.example.skycast.model.repository

import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.example.skycast.model.local.ILocalDataSource
import com.example.skycast.model.network.IRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Date

class Repository(val remoteDataSource: IRemoteDataSource, val localDataSource: ILocalDataSource) :
    IRepository {
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
    override fun getDailyWeather(latLng: LatLng, apiKey: String, lang : String, forceUpdate : Boolean) : Flow<List<DailyWeather>> {
        if(forceUpdate){
            return remoteDataSource.getDailyForecast(latLng, apiKey, "metric", lang)
        }
        else{
            val dt = (Date().time /1000).toInt()
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
    override suspend fun updateWeatherCache(latLng: LatLng, apiKey: String){
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        val clearDatabaseJob = scope.launch {
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
        job.cancel()
    }

    override fun getAlerts(): Flow<List<AlertDTO>> {
        return localDataSource.getAlerts()
    }

    override suspend fun addAlert(alertDTO: AlertDTO): Long {
        return localDataSource.addAlert(alertDTO)
    }

    override suspend fun deleteAlert(alertDTO: AlertDTO): Int {
        return localDataSource.delete(alertDTO)
    }

    override suspend fun addFav(favDTO: FavDTO): Long {
        return localDataSource.addFav(favDTO)
    }

    override fun getAllFav(): Flow<List<FavDTO>> {
        return localDataSource.getAllFav()
    }

    override suspend fun deleteFav(favDTO: FavDTO): Int {
        return localDataSource.deleteFav(favDTO)
    }

    override fun getAlert(latLng: LatLng, apiKey: String): Flow<Alert> {
        return remoteDataSource.getAlert(latLng, apiKey)
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