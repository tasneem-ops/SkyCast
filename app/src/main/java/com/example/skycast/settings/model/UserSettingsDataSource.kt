package com.example.skycast.settings.model

import android.content.Context
import com.google.android.gms.maps.model.LatLng

class UserSettingsDataSource private constructor(val context: Context) : IUserSettingsDataSource {

    override fun getLocationSource() : String?{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(LOCATION_SOURCE, SOURCE_UNKNOWN)
    }
    override fun setLocationSource(source: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(LOCATION_SOURCE, source)
            .apply()
    }

    override fun getPreferredLanguage() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(LANGUAGE, ENGLISH) ?: ENGLISH
    }
    override fun setPreferredLanguage(language: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE, language)
            .apply()
    }

    override fun getTempUnit() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(TEMP_UNIT, UNIT_CELSIUS) ?: UNIT_CELSIUS
    }
    override fun setTempUnit(unit: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(TEMP_UNIT, unit)
            .apply()
    }

    override fun getSpeedUnit() : String{
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getString(SPEED_UNIT, UNIT_MPS) ?: UNIT_MPS
    }
    override fun setSpeedUnit(unit: String){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putString(SPEED_UNIT, unit)
            .apply()
    }

    override fun getSavedLocation() : LatLng{
        val latitude = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getFloat(LOCATION_LAT, 0.0f)
        val longitude = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getFloat(LOCATION_LNG, 0.0f)
        return LatLng(latitude.toDouble(), longitude.toDouble())
    }
    override fun setSavedLocation(latLng: LatLng){
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putFloat(LOCATION_LAT, latLng.latitude.toFloat())
            .putFloat(LOCATION_LNG, latLng.longitude.toFloat())
            .apply()
    }

    override fun isNotificationEnabled(): Boolean {
        return context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .getBoolean(NOTIFICATION, true)
    }

    override fun setNotificationEnabled(enable: Boolean) {
        context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(NOTIFICATION, enable)
            .apply()
    }

    companion object{
        @Volatile
        private var INSTANCE: UserSettingsDataSource? = null
        fun getInstance (context: Context): UserSettingsDataSource {
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

        private const val NOTIFICATION = "NOTIFICATION"
    }
}