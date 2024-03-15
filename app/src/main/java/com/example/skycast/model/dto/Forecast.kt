package com.example.skycast.model.dto

import com.example.skycast.model.dto.DayWeather
import com.google.gson.annotations.SerializedName

data class Forecast (
    @SerializedName("list") var list: ArrayList<DayWeather> = arrayListOf(),
    @SerializedName("city") var city : City? = City()
)
