package com.example.skycast.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.skycast.database.ListConverter
import com.google.gson.annotations.SerializedName

data class DayWeather (
    @SerializedName("weather") var weather : ArrayList<Weather> = arrayListOf(),
    @SerializedName("main") var main : MainData? = MainData(),
    @SerializedName("visibility") var visibility : Int? = null,
    @SerializedName("wind") var wind : Wind? = Wind(),
    @SerializedName("clouds") var clouds : Clouds? = Clouds(),
    @SerializedName("dt") var dt : Int,
    @SerializedName("dt_text") var dtText : String
)