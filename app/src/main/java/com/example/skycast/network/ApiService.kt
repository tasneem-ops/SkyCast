package com.example.skycast.network

import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("forecast?")
    suspend fun getForecast(@Query("lat") lat : Double, @Query("lon") lng : Double,
                              @Query("appid") apiKey: String, @Query("units") units : String,
                              @Query("lang") language : String) : Response<Forecast>
}