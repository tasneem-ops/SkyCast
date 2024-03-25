package com.example.skycast.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.home.viewmodel.WeatherViewModel
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.repository.IRepository

class SettingsViewModelFactory(private val settingsDataSource: UserSettingsDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(settingsDataSource) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}