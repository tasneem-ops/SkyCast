package com.example.skycast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.model.repository.IRepository

class AlertViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repository) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}