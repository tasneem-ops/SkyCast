package com.example.skycast.database

import androidx.room.TypeConverter
import com.example.skycast.model.dto.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ListConverter{
    @TypeConverter
    fun fromString(value: String?): List<Weather> {
        val listType = object : TypeToken<List<Weather>>() {}.type
        return Gson().fromJson<List<Weather>>(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<Weather>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
