package com.example.skycast.model.network

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.home.model.network.WeatherRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class RemoteDataSource private constructor() : IRemoteDataSource {
    val weatherRemoteDataSource = WeatherRemoteDataSource.getInstance()
    companion object{
        @Volatile
        private var INSTANCE: RemoteDataSource? = null
        fun getInstance (): RemoteDataSource {
            return INSTANCE ?: synchronized(this){
                val instance = RemoteDataSource()
                INSTANCE = instance
                instance
            }
        }
    }
    override fun getDailyForecast(latLng: LatLng, apiKey : String, units : String, lang : String) : Flow<List<DailyWeather>>{
        return weatherRemoteDataSource.getDailyForecast(latLng, apiKey, units, lang)
    }
    override fun getHourlyForecast(latLng: LatLng, apiKey: String, units: String, lang: String) : Flow<List<HourlyWeather>>{
        return weatherRemoteDataSource.getHourlyForecast(latLng, apiKey, units, lang)
    }
    override fun getCurrentForecast(latLng: LatLng, apiKey: String, units: String, lang: String) : Flow<HourlyWeather?>{
        return weatherRemoteDataSource.getCurrentForecast(latLng, apiKey, units, lang)
    }
}