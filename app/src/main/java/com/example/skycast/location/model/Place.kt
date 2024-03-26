package com.example.skycast.location.model

import com.google.gson.annotations.SerializedName

data class Place(
    var lat : String?,
    var lon : String?,
    var name : String?,
    @SerializedName("display_name")
    var displayName : String?
)