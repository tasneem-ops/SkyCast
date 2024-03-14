package com.example.skycast.model.dto

import com.google.gson.annotations.SerializedName

data class City (
    @SerializedName("id") var id : Int? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("country") var country : String? = null,
    @SerializedName("timezone") var timezone : Int?    = null,
    @SerializedName("sunrise") var sunrise : Int? = null,
    @SerializedName("sunset") var sunset : Int? = null
)