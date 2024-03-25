package com.example.skycast.model.local

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.intellij.lang.annotations.Language

class UserSettingsDataSource private constructor(val context: Context) {

    fun getLocationSource() : String?{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(LOCATION_SOURCE, SOURCE_GPS)
    }
    fun setLocationSource(source: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(LOCATION_SOURCE, source)
            .apply()
    }

    fun getPreferredLanguage() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(LANGUAGE, ENGLISH) ?: ENGLISH
    }
    fun setPreferredLanguage(language: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE, language)
            .apply()
    }

    fun getTempUnit() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(TEMP_UNIT, UNIT_CELSIUS) ?: UNIT_CELSIUS
    }
    fun setTempUnit(unit: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(TEMP_UNIT, unit)
            .apply()
    }

    fun getSpeedUnit() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(SPEED_UNIT, UNIT_MPS) ?: UNIT_MPS
    }
    fun setSpeedUnit(unit: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(SPEED_UNIT, unit)
            .apply()
    }

    fun getSavedLocation() : LatLng{
        val latitude = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getFloat(LOCATION_LAT, 0.0f)
        val longitude = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getFloat(LOCATION_LNG, 0.0f)
        return LatLng(latitude.toDouble(), longitude.toDouble())
    }
    fun setSavedLocation(latLng: LatLng){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putFloat(LOCATION_LAT, latLng.latitude.toFloat())
            .putFloat(LOCATION_LNG, latLng.longitude.toFloat())
            .apply()
    }

    companion object{
        @Volatile
        private var INSTANCE: UserSettingsDataSource? = null
        fun getInstance (context: Context): UserSettingsDataSource{
            return INSTANCE ?: synchronized(this){
                val instance = UserSettingsDataSource(context)
                INSTANCE = instance
                instance
            }
        }
        private const val USER_SETTINGS = "USER_SETTINGS"
        private const val LOCATION_SOURCE = "LOCATION_SOURCE"
        const val SOURCE_MAP = "MAP"
        const val SOURCE_GPS = "GPS"

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