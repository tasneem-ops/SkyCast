package com.example.skycast.alert.model.network

import com.example.skycast.home.model.network.ForecastApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AlertRetrofitHelper {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/3.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitService: AlertApiService by lazy {
        retrofit.create(AlertApiService::class.java)
    }
}