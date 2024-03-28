package com.example.skycast.model.local

import android.content.Context
import com.google.android.gms.maps.model.LatLng

class FakeUserSettingsDataSource : IUserSettingsDataSource {
    private var locationSource : String = SOURCE_UNKNOWN
    private var preferredLanguage : String = ENGLISH
    private var tempUnit : String = UNIT_CELSIUS
    private var speedUnit : String = UNIT_MPS
    private var savedLocation = LatLng(0.0, 0.0)
    override fun getLocationSource(): String {
        return locationSource
    }

    override fun setLocationSource(source: String) {
        locationSource = source
    }

    override fun getPreferredLanguage(): String {
        return preferredLanguage
    }

    override fun setPreferredLanguage(language: String) {
        preferredLanguage = language
    }

    override fun getTempUnit() = tempUnit

    override fun setTempUnit(unit: String) {
        tempUnit = unit
    }

    override fun getSpeedUnit() = speedUnit

    override fun setSpeedUnit(unit: String) {
        speedUnit = unit
    }

    override fun getSavedLocation() = savedLocation

    override fun setSavedLocation(latLng: LatLng) {
        savedLocation = latLng
    }
    companion object{
        private const val USER_SETTINGS = "USER_SETTINGS"
        private const val LOCATION_SOURCE = "LOCATION_SOURCE"
        const val SOURCE_MAP = "MAP"
        const val SOURCE_GPS = "GPS"
        const val SOURCE_UNKNOWN = "UNKNOWN"

        private const val LOCATION_LAT = "LOCATION_LAT"
        private const val LOCATION_LNG = "LOCATION_LNG"

        private const val TEMP_UNIT = "TEMP_UNIT"
        const val UNIT_CELSIUS = "CELSIUS"
        const val UNIT_FAHRENHEIT = "FAHRENHEIT"
        const val UNIT_KELVIN = "KELVIN"

        private const val SPEED_UNIT = "SPEED_UNIT"
        const val UNIT_MPS = "MPS"
        const val UNIT_MPH = "MPH"

        private const val LANGUAGE = "LANGUAGE"
        const val ARABIC = "ar"
        const val ENGLISH = "en"
    }
}