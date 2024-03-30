package com.example.skycast.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.skycast.model.local.UserSettingsDataSource
import com.google.android.gms.maps.model.LatLng

class SettingsViewModel(private val settingsDataSource: UserSettingsDataSource) : ViewModel(){
    fun getLocationSource() : String?{
        return settingsDataSource.getLocationSource()
    }
    fun setLocationSource(source: String){
        settingsDataSource.setLocationSource(source)
    }

    fun getPreferredLanguage() : String{
        return settingsDataSource.getPreferredLanguage()
    }
    fun setPreferredLanguage(language: String){
        settingsDataSource.setPreferredLanguage(language)
    }

    fun getTempUnit() : String{
        return settingsDataSource.getTempUnit()
    }
    fun setTempUnit(unit: String){
        settingsDataSource.setTempUnit(unit)
    }

    fun getSpeedUnit() : String{
        return settingsDataSource.getSpeedUnit()
    }
    fun setSpeedUnit(unit: String){
        settingsDataSource.setSpeedUnit(unit)
    }

    fun getSavedLocation() : LatLng{
        return settingsDataSource.getSavedLocation()
    }
    fun setSavedLocation(latLng: LatLng){
        settingsDataSource.setSavedLocation(latLng)
    }
    fun isNotificationEnabled(): Boolean{
        return settingsDataSource.isNotificationEnabled()
    }
    fun setNotificationEnabled(enable : Boolean){
        settingsDataSource.setNotificationEnabled(enable)
    }

}