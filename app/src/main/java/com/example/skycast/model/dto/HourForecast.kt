package com.example.skycast.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "hour_forecast")
data class HourForecast(
    var mainWeather : String?,
    var description : String?,
    var icon : String?,
    var temp : Double?,
    var feelsLike : Double?,
    var tempMin : Double?,
    var tempMax : Double?,
    var pressure : Int?,
    var humidity : Int?,
    var visibility : Int?,
    var windSpeed : Double?,
    var clouds : Int?,
    @PrimaryKey
    var dt : Int,
    var cityId : Int?,
    var cityName : String?,
    var country : String?
    )
