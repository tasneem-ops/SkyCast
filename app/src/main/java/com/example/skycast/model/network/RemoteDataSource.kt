package com.example.skycast.model.network

import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.network.AlertRetrofitHelper
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.home.model.network.WeatherRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
    override fun getAlert(latLng: LatLng, apiKey: String, units: String, lang: String) : Flow<Alert>{
        return flow {
            val responseBody = AlertRetrofitHelper.retrofitService
                .getAlert(latLng.latitude, latLng.longitude, apiKey, units, lang).body()
            if(responseBody?.alerts?.isEmpty() == true){
                emit(Alert("", "No Alerts For Now", 1, 5646536, "", emptyList<String>()))
            }
            else{
                responseBody?.alerts?.get(0)?.let { emit(it) }
            }
        }

    }
}