package com.example.skycast.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.location.model.Place
import com.example.skycast.Response
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.ARABIC
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.ENGLISH
import com.example.skycast.home.model.source.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: WeatherRepository,
                        private val settingsDataSource: UserSettingsDataSource
) : ViewModel() {
    private val _responseDataState = MutableStateFlow<Response<List<Place>>>(Response.Loading())
    val respnseDataState: StateFlow<Response<List<Place>>> = _responseDataState.asStateFlow()

    fun getSuggestions(query : String){
        val lang = settingsDataSource.getPreferredLanguage()
        var accepted_language = "en"
        if(lang == ARABIC){
            accepted_language = "ar,en"
        }
        else if(lang == ENGLISH){
            accepted_language = "en,ar"
        }
        viewModelScope.launch {
            repository.getSearchSuggestions(query, "json", accepted_language, 20)
                .catch {
                    _responseDataState.value = Response.Failure("No Data Available")
                }
                .collect{
                    _responseDataState.value = Response.Success(it)
                }
        }
    }
}