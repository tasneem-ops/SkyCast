package com.example.skycast.model.dto

import com.google.gson.annotations.SerializedName

data class Weather (
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description : String? = null,
    @SerializedName("icon") var icon: String? = null
)