package com.example.skycast.home.model.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ForecastRetrofitHelper {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/3.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitService: ForecastApiService by lazy {
        retrofit.create(ForecastApiService::class.java)
    }
}