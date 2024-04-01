package com.example.skycast.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.favorites.model.FavoritesLocalDataSource
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: FavoritesLocalDataSource,
                         private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    private val _responseDataState = MutableStateFlow<Response<List<FavDTO>>>(Response.Loading())
    val respnseDataState: StateFlow<Response<List<FavDTO>>> = _responseDataState.asStateFlow()

    fun addFav(favDTO: FavDTO){
        viewModelScope.launch(ioDispatcher) {
            repository.addFav(favDTO)
        }
    }
    fun getAllFav(){
        viewModelScope.launch(ioDispatcher) {
            repository.getAllFav()
                .catch {
                    _responseDataState.value = Response.Failure(it.message.toString())
                }
                .collect{
                    _responseDataState.value = Response.Success(it)
                }
        }
    }
    fun deleteFav(favDTO: FavDTO){
        viewModelScope.launch(ioDispatcher){
            repository.deleteFav(favDTO)
        }
    }
}