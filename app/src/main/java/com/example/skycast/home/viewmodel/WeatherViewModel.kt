package com.example.skycast.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.home.model.dto.WeatherResult
import com.example.skycast.Response
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.settings.model.IUserSettingsDataSource
import com.example.skycast.home.model.source.repository.WeatherRepository
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_CELSIUS
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_FAHRENHEIT
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_KELVIN
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPH
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPS
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository,
                       private val settingsDataSource: IUserSettingsDataSource,
                       private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel(){

    private val _responseDataState = MutableStateFlow<Response<WeatherResult>>(Response.Loading())
    val respnseDataState: StateFlow<Response<WeatherResult>> = _responseDataState.asStateFlow()

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _responseDataState.value = Response.Failure("Couldn't Fetch Data")
        throwable.printStackTrace()
    }

    fun getWeatherForecast(latLng: LatLng, apiKey : String,
                           connectionStatus: Boolean, freshData : Boolean) {
        val forceUpdate = connectionStatus && freshData
        val lang = settingsDataSource.getPreferredLanguage()
        viewModelScope.launch (ioDispatcher + coroutineExceptionHandler){
            val currentFlow = getCurrentWeather(latLng, apiKey, lang, forceUpdate)
            val dailyFlow = getDailyWeather(latLng, apiKey, lang, forceUpdate)
            val hourlyFlow = getHourlyWeather(latLng, apiKey, lang, forceUpdate)
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
            viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
                val currentFlow = getCurrentWeather(latLng, apiKey, lang, false)
                val dailyFlow = getDailyWeather(latLng, apiKey, lang, false)
                val hourlyFlow = getHourlyWeather(latLng, apiKey, lang, false)
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
    private fun getCurrentWeather(latLng : LatLng, apiKey : String, lang : String, forceUpdate: Boolean) : Flow<HourlyWeather?> {
        return repository.getCurrentWeather(latLng, apiKey, lang, forceUpdate)
            .map {current ->
                when{
                    (getTempUnit() == UNIT_FAHRENHEIT  && getSpeedUnit() == UNIT_MPH)  -> {
                        current?.copy(
                            temp = current.temp * 1.8 +32,
                            feelsLike = current.feelsLike * 1.8 +32,
                            windSpeed = current.windSpeed * 2.2369
                        )
                    }
                    (getTempUnit() == UNIT_FAHRENHEIT  && getSpeedUnit() == UNIT_MPS)  -> {
                        current?.copy(
                            temp = current.temp * 1.8 +32,
                            feelsLike = current.feelsLike * 1.8 +32
                        )
                    }
                    (getTempUnit() == UNIT_KELVIN  && getSpeedUnit() == UNIT_MPH)  -> {
                        current?.copy(
                            temp = current.temp + 273.15,
                            feelsLike = current.feelsLike + 273.15,
                            windSpeed = current.windSpeed * 2.2369
                        )
                    }
                    (getTempUnit() == UNIT_KELVIN  && getSpeedUnit() == UNIT_MPS)  -> {
                        current?.copy(
                            temp = current.temp + 273.15,
                            feelsLike = current.feelsLike + 273.15
                        )
                    }
                    (getTempUnit() == UNIT_CELSIUS  && getSpeedUnit() == UNIT_MPH)  -> {
                        current?.copy(
                            windSpeed = current.windSpeed * 2.2369
                        )
                    }
                    else -> {current}
                }
            }
    }
    private fun getDailyWeather(latLng : LatLng, apiKey : String, lang : String, forceUpdate : Boolean) : Flow<List<DailyWeather>>{
        return repository.getDailyWeather(latLng, apiKey, lang, forceUpdate)
            .map { daily ->
                modifyDailyTemp(daily, getTempUnit())
            }
    }
    private fun getHourlyWeather(latLng : LatLng, apiKey : String, lang : String, forceUpdate: Boolean) : Flow<List<HourlyWeather>>{
        return repository.getHourlyWeather(latLng, apiKey, lang, forceUpdate)
            .map { hourly ->
                modifyHourlyTemp(hourly, getTempUnit())
            }
    }
    private fun modifyHourlyTemp(list : List<HourlyWeather>, type :  String) : List<HourlyWeather>{
        when(type){
            UNIT_FAHRENHEIT -> {
                return list.map {
                    it.copy(temp =  it.temp * 1.8 +32)
                }
            }
            UNIT_KELVIN -> {
                return list.map {
                    it.copy(temp =  it.temp + 273.15)
                }
            }
            else ->{
                return list
            }
        }
    }
    private fun modifyDailyTemp(list : List<DailyWeather>, type :  String) : List<DailyWeather>{
        when(type){
            UNIT_FAHRENHEIT -> {
                return list.map {
                    it.copy(maxTemp =  it.maxTemp * 1.8 +32, minTemp = it.minTemp * 1.8 +32)
                }
            }
            UNIT_KELVIN -> {
                return list.map {
                    it.copy(maxTemp =  it.maxTemp + 273.15, minTemp = it.minTemp + 273.15)
                }
            }
            else ->{
                return list
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
    fun getTempUnit() : String{
        return settingsDataSource.getTempUnit()
    }
    fun getSpeedUnit(): String{
        return settingsDataSource.getSpeedUnit()
    }
    fun updateLocationAndCache(latLng: LatLng, apiKey: String){
        settingsDataSource.setSavedLocation(latLng)
        updateWeatherCache(latLng, apiKey)
    }
    fun getSavedLocation() : LatLng{
        return settingsDataSource.getSavedLocation()
    }
    private fun updateWeatherCache(latLng: LatLng, apiKey: String){
        viewModelScope.launch (ioDispatcher + coroutineExceptionHandler){
            repository.updateWeatherCache(latLng, apiKey)
        }
    }

}