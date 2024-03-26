package com.example.skycast.model.network

import android.util.Log
import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.network.AlertRetrofitHelper
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.home.model.network.WeatherRemoteDataSource
import com.example.skycast.location.model.Place
import com.example.skycast.location.model.network.SearchRetrofitHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Arrays

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
    override fun getAlert(latLng: LatLng, apiKey: String) : Flow<Alert>{
        return flow {
            val responseBody = AlertRetrofitHelper.retrofitService
                .getAlert(latLng.latitude, latLng.longitude, apiKey).body()
            if(responseBody?.alerts?.isEmpty() == true){
                emit(Alert("", "No Alerts For Now", Int.MIN_VALUE, Int.MAX_VALUE, "", emptyList<String>()))
            }
            else{
                responseBody?.alerts?.get(0)?.let { emit(it) }
            }
        }

    }

    override fun getSearchSuggestions(
        query: String,
        format: String,
        lang: String,
        limit: Int
    ): Flow<List<Place>> {

        return flow {
            val responseBody = SearchRetrofitHelper.retrofitService
                .getSearchSuggestions(query, format, lang, limit).body()
            if(responseBody.isNullOrEmpty()){
                emit(emptyList())
            }
            else{
                emit(responseBody)
            }
        }
    }
}