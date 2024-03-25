package com.example.skycast.favorites.model.dto

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = arrayOf("latitude", "longitude"))
data class FavDTO(
    var cityName : String,
    var latitude : Double,
    var longitude : Double
)
