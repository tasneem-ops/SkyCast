package com.example.skycast.home.model.source.repository

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getDailyWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean = false
    ): Flow<List<DailyWeather>>

    fun getHourlyWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean
    ): Flow<List<HourlyWeather>>

    fun getCurrentWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean
    ): Flow<HourlyWeather?>

    suspend fun updateWeatherCache(latLng: LatLng, apiKey: String)
    fun getSearchSuggestions(query : String, format: String, lang : String, limit : Int) : Flow<List<Place>>
}