package com.example.skycast.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.home.model.dto.WeatherResult
import com.example.skycast.model.Response
import com.example.skycast.model.local.IUserSettingsDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.repository.IWeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: IWeatherRepository,
                       private val settingsDataSource: IUserSettingsDataSource,
                       private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel(){

    private val _responseDataState = MutableStateFlow<Response<WeatherResult>>(Response.Loading())
    val respnseDataState: StateFlow<Response<WeatherResult>> = _responseDataState.asStateFlow()

    fun getWeatherForecast(latLng: LatLng, apiKey : String,
                           connectionStatus: Boolean, freshData : Boolean) {
        val forceUpdate = connectionStatus && freshData
        val lang = settingsDataSource.getPreferredLanguage()
        viewModelScope.launch (ioDispatcher){
            val currentFlow = repository.getCurrentWeather(latLng, apiKey, lang, forceUpdate)
            val dailyFlow = repository.getDailyWeather(latLng, apiKey, lang, forceUpdate)
            val hourlyFlow = repository.getHourlyWeather(latLng, apiKey, lang, forceUpdate)
            combine(currentFlow, dailyFlow, hourlyFlow) { current, day, hour ->
                WeatherResult(current, hour.take(24), day.take(7))
            }
                .catch {
                    _responseDataState.value = Response.Failure("Couldn't Fetch Data")
                }
                .collect {
                    _responseDataState.value = Response.Success(it)
                }
        }
    }
    fun getWeatherForecastForSavedLocation(apiKey : String) {
        val latLng = settingsDataSource.getSavedLocation()
        val lang = settingsDataSource.getPreferredLanguage()
        if(!isLocationSaved()){
            _responseDataState.value = Response.Failure("No Data Saved!")
        }
        else{
            viewModelScope.launch(ioDispatcher) {
                val currentFlow = repository.getCurrentWeather(latLng, apiKey, lang, false)
                val dailyFlow = repository.getDailyWeather(latLng, apiKey, lang, false)
                val hourlyFlow = repository.getHourlyWeather(latLng, apiKey, lang, false)
                combine(currentFlow, dailyFlow, hourlyFlow) { current, day, hour ->
                    WeatherResult(current, hour.take(24), day.take(7))
                }
                    .catch {
                        _responseDataState.value = Response.Failure("Couldn't Fetch Data")
                    }
                    .collect {
                        _responseDataState.value = Response.Success(it)
                    }
            }
        }
    }

    fun isLocationSaved() : Boolean{
        val latLng = settingsDataSource.getSavedLocation()
        if(latLng.latitude == 0.0 || latLng.longitude == 0.0){
            return false
        }
        return true
    }
    fun getLocationPreferences() : String?{
        return settingsDataSource.getLocationSource()
    }
    fun setLocationPreferences(source : String){
        settingsDataSource.setLocationSource(source)
    }
    fun updateLocationAndCache(latLng: LatLng, apiKey: String){
        settingsDataSource.setSavedLocation(latLng)
        updateWeatherCache(latLng, apiKey)
    }
    private fun updateWeatherCache(latLng: LatLng, apiKey: String){
        viewModelScope.launch (ioDispatcher){
            repository.updateWeatherCache(latLng, apiKey)
        }
    }

}