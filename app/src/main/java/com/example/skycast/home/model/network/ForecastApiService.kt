package com.example.skycast.home.model.network

import com.example.skycast.home.model.dto.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiService {
    @GET("onecall?")
    suspend fun getWeatherForecast(@Query("lat") lat : Double, @Query("lon") lng : Double,
                                   @Query("appid") apiKey: String, @Query("units") units : String,
                                   @Query("lang") language : String) : Response<Forecast>
}