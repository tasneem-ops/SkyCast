package com.example.skycast.settings.model

import com.google.android.gms.maps.model.LatLng

interface IUserSettingsDataSource {
    fun getLocationSource(): String?
    fun setLocationSource(source: String)
    fun getPreferredLanguage(): String
    fun setPreferredLanguage(language: String)
    fun getTempUnit(): String
    fun setTempUnit(unit: String)
    fun getSpeedUnit(): String
    fun setSpeedUnit(unit: String)
    fun getSavedLocation(): LatLng
    fun setSavedLocation(latLng: LatLng)

    fun isNotificationEnabled(): Boolean
    fun setNotificationEnabled(enable : Boolean)
}