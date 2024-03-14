package com.example.skycast.network

import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.Forecast
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getForecast(lat: Double, lng: Double, apiKey: String,
                              units: String, lang: String) : Flow<Forecast>
}