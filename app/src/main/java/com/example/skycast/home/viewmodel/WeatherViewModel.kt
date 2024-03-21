package com.example.skycast.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.home.model.dto.WeatherResult
import com.example.skycast.model.Response
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WeatherViewModel(val repository: IRepository, val settingsDataSource: UserSettingsDataSource) : ViewModel(){
//    private val _currentDataState = MutableStateFlow<Response<HourlyWeather?>>(Response.Loading())
//    val currentDataState: StateFlow<Response<HourlyWeather?>> = _currentDataState.asStateFlow()
//
//    private val _hourlyDataState = MutableStateFlow<Response<List<HourlyWeather>>>(Response.Loading())
//    val hourlyDataState: StateFlow<Response<List<HourlyWeather>>> = _hourlyDataState.asStateFlow()
//
//    private val _dailyDataState = MutableStateFlow<Response<List<DailyWeather>>>(Response.Loading())
//    val dailyDataState: StateFlow<Response<List<DailyWeather>>> = _dailyDataState.asStateFlow()

    private val _responseDataState = MutableStateFlow<Response<WeatherResult>>(Response.Loading())
    val respnseDataState: StateFlow<Response<WeatherResult>> = _responseDataState.asStateFlow()

    fun getWeatherForecast(latLng: LatLng, apiKey : String,
                           connectionStatus: Boolean, freshData : Boolean){
        val forceUpdate = connectionStatus && freshData
        val lang = settingsDataSource.getPreferredLanguage()
        viewModelScope.launch {
            val currentFlow = repository.getCurrentWeather(latLng, apiKey, lang, forceUpdate)
            val dailyFlow = repository.getDailyWeather(latLng, apiKey, lang, forceUpdate)
            val hourlyFlow = repository.getHourlyWeather(latLng, apiKey, lang, forceUpdate)
            combine(currentFlow, dailyFlow, hourlyFlow){ current, day, hour ->
                WeatherResult(current, hour.take(24), day)
            }
                .catch {
                    _responseDataState.value = Response.Failure("Couldn't Fetch Data")
                }
                .collect{
                    _responseDataState.value = Response.Success(it)
                }
        }
//
//        getDailyForecast(latLng, apiKey, lang, forceUpdate)
//        getCurrentForecast(latLng, apiKey, lang, forceUpdate)
//        getHourlyForecast(latLng, apiKey, lang, forceUpdate)

    }
//    fun getDailyForecast(latLng: LatLng, apiKey : String, lang : String, forceUpdate: Boolean){
//        viewModelScope.launch {
//            repository.getDailyWeather(latLng, apiKey, lang, forceUpdate)
//                .catch {
//                    _dailyDataState.value = Response.Failure("Couldn't Fetch Data")
//                }
//                .collect{
//                    _dailyDataState.value = Response.Success(it)
//                }
//        }
//    }
//    fun getCurrentForecast(latLng: LatLng, apiKey : String, lang : String, forceUpdate: Boolean){
//        viewModelScope.launch {
//            repository.getCurrentWeather(latLng, apiKey, lang, forceUpdate)
//                .catch {
//                    _currentDataState.value = Response.Failure("Couldn't Fetch Data")
//                }
//                .collect{
//                    _currentDataState.value = Response.Success(it)
//                }
//        }
//    }
//    fun getHourlyForecast(latLng: LatLng, apiKey : String, lang : String, forceUpdate: Boolean){
//        viewModelScope.launch {
//            repository.getHourlyWeather(latLng, apiKey, lang, forceUpdate)
//                .catch {
//                    _hourlyDataState.value = Response.Failure("Couldn't Fetch Data")
//                }
//                .collect{
//                    _hourlyDataState.value = Response.Success(it)
//                }
//        }
//    }
}