package com.example.skycast.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.home.model.source.repository.WeatherRepository

class LocationViewModelFactory(private val repository: WeatherRepository,
                               private val settingsDataSource: UserSettingsDataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(LocationViewModel::class.java)){
            LocationViewModel(repository, settingsDataSource) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}