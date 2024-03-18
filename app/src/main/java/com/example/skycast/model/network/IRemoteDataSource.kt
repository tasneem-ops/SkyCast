package com.example.skycast.model.network

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    fun getDailyForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<List<DailyWeather>>

    fun getHourlyForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<List<HourlyWeather>>

    fun getCurrentForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<HourlyWeather?>
}