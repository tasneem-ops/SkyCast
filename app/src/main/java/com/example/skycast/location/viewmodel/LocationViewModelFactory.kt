package com.example.skycast.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.repository.IRepository

class LocationViewModelFactory(private val repository: IRepository,
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