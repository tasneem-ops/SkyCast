package com.example.skycast.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.favorites.model.FavoritesLocalDataSource

class FavoritesViewModelFactory(private val repository : FavoritesLocalDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoritesViewModel::class.java)){
            FavoritesViewModel(repository) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}