package com.example.skycast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.alert.model.repository.AlertsRepository
import com.example.skycast.settings.model.UserSettingsDataSource
class AlertViewModelFactory(private val repository: AlertsRepository,
                            private val settingsDataSource: UserSettingsDataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repository, settingsDataSource) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}