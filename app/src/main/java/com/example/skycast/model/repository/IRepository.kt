package com.example.skycast.model.repository

import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface IRepository {
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

    fun getAlerts() : Flow<List<AlertDTO>>
    suspend fun addAlert(alertDTO: AlertDTO) : Long
    suspend fun deleteAlert(alertDTO: AlertDTO) : Int
    suspend fun addFav(favDTO: FavDTO) : Long
    fun getAllFav() : Flow<List<FavDTO>>
    suspend fun deleteFav(favDTO: FavDTO) : Int
    fun getAlert(latLng: LatLng, apiKey: String) : Flow<Alert>
    fun getSearchSuggestions(query : String, format: String, lang : String, limit : Int) : Flow<List<Place>>
}