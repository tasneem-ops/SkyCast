package com.example.skycast.alert.model.network

import com.example.skycast.alert.model.dto.ApiAlert
import com.example.skycast.home.model.dto.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlertApiService {
    @GET("onecall?")
    suspend fun getAlert(@Query("lat") lat : Double, @Query("lon") lng : Double,
                                   @Query("appid") apiKey: String, @Query("units") units : String,
                                   @Query("lang") language : String) : Response<ApiAlert>
}