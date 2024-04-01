package com.example.skycast.home.model.source.network

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
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
    fun getSearchSuggestions(query : String, format: String, lang : String, limit : Int) : Flow<List<Place>>

}