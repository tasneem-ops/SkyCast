package com.example.skycast.location.model.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchRetrofitHelper {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitService: SearchApiService by lazy {
        retrofit.create(SearchApiService::class.java)
    }
}