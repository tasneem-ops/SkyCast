package com.example.skycast.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.dto.CurrentForecast
import com.example.skycast.model.dto.Forecast
import com.example.skycast.model.dto.HourForecast
import com.example.skycast.model.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Date
import java.util.HashMap

private const val TAG = "WeatherViewModel"
class WeatherViewModel(private val repository: IRepository) : ViewModel() {
    var index = 0
    private val _responseDataState = MutableStateFlow<ResponseStatus>(ResponseStatus.Loading())
    val responseDataState: StateFlow<ResponseStatus> = _responseDataState.asStateFlow()

    init {

    }

    fun getWeatherForecast(lat : Double, lng: Double, connectionStatus : Boolean){
        Log.i(TAG, "getWeatherForecast: ")
        if(connectionStatus == false){
            getWeatherFromLocal()
        }
        else{
            getWeatherFromRemote(lat, lng)
        }
    }

    private fun getWeatherFromRemote(lat : Double, lng: Double) {
        Log.i(TAG, "getWeatherFromRemote: ")
        viewModelScope.launch(Dispatchers.IO){
            repository.getApiForecast(lat, lng, "2323b571a2b3fff232943282a6ec4b26", "metric", "en" )
                .catch {
                    Log.i(TAG, "getWeatherFromRemote: Failed ${it.message.toString()}")
                    _responseDataState.value = ResponseStatus.Failure(it.message.toString())
                }
                .collect{
                    Log.i(TAG, "getWeatherFromRemote: Collect")
                    val hourForecastList = mapApiData(it)
                    processDataToCurrentForecast(hourForecastList)
                    Log.i(TAG, "getWeatherFromRemote: ")
                }
        }
    }

    private fun mapApiData(forecast: Forecast) : List<HourForecast>{
        Log.i(TAG, "mapApiData: ")
        val hourForecastList = arrayListOf<HourForecast>()
        for(dayWeather in forecast.list){
            val weather = dayWeather.weather.get(0)
            val main = dayWeather.main
            val city = forecast.city
            val hourForecast = HourForecast(weather.main, weather.description, weather.icon, main?.temp,
                main?.feelsLike, main?.tempMin, main?.tempMax, main?.pressure, main?.humidity, dayWeather.visibility,
                dayWeather.wind?.speed, dayWeather.clouds?.all, dayWeather.dt, city?.id, city?.name, city?.country)
            hourForecastList.add(hourForecast)
        }
        return hourForecastList
    }

    private fun getWeatherFromLocal(){
        viewModelScope.launch {
            repository.getLocalForecast()
                .catch {
                    _responseDataState.value = ResponseStatus.Failure(it.message.toString())
                }
                .collect{ list ->
                    if(list.size < 40){
                        _responseDataState.value = ResponseStatus.Failure("No Data Found")
                    }
                    else{
                        processDataToCurrentForecast(list)
                    }
                }
        }
    }

    private fun processDataToCurrentForecast(list: List<HourForecast>){
        Log.i(TAG, "processDataToCurrentForecast: ")
        var currentWeather = getCurrentWeather(list)
        if(currentWeather == null){
            _responseDataState.value = ResponseStatus.Failure("No Data")
        }
        else{
            val dayForecast = list.subList(index, index+8)
            val fiveDayForecast = getDailyForecast(list)
            val currentForecast = CurrentForecast(currentWeather, dayForecast, fiveDayForecast)
            _responseDataState.value = ResponseStatus.Success(currentForecast)
            Log.i(TAG, "processDataToCurrentForecast: ")
        }
    }

    private fun getCurrentWeather(list: List<HourForecast>) : HourForecast?{
        index = 0
        val currentTimestamp: Int = (Date().time /1000).toInt()
        for(i in list.indices){
            val hourForecast = list.get(i)
            if(currentTimestamp > hourForecast.dt){
                continue
            }
            else{
                index = i
                return hourForecast
            }
        }
        return null
    }
    //This Method Calculates Avg temperature and most common weather description to get breif description of the day
    private fun getDailyForecast(list: List<HourForecast>) : List<HourForecast>{
        val fiveDayForecast = arrayListOf<HourForecast>()
        var i = 0
        for(y in 0..4){
            val weatherDesc :HashMap<String, Int> = hashMapOf("clear sky" to 0, "few clouds" to 0, "mist" to 0,
                "scattered clouds" to 0, "broken clouds" to 0, "shower rain" to 0, "rain" to 0, "thunderstorm" to 0, "snow" to 0)
            var minTempSum = arrayListOf<Double>()
            var maxTempSum = arrayListOf<Double>()
            var tempSum = 0.0
            var weather_desc = ""
            for(x in 0..7){
                maxTempSum.add(list.get(i).tempMax ?: Double.MIN_VALUE)
                minTempSum.add(list.get(i).tempMin ?: Double.MAX_VALUE)
                tempSum += list.get(i).temp ?: 0.0
                weatherDesc.get(list.get(i).description)?.plus(1)
                i++
            }
            val maxValue = Collections.max(weatherDesc.values)
            for(entry in weatherDesc.entries){
                if(entry.value == maxValue){
                    weather_desc = entry.key
                }
            }
            fiveDayForecast.add(HourForecast(list.get(i-3).mainWeather, weather_desc, weather_desc, tempSum/8.0, tempSum/8.0,
                minTempSum.min(), maxTempSum.max(), list.get(i-3).pressure, list.get(i-3).humidity, list.get(i-3).visibility,
                list.get(i-3).windSpeed, list.get(i-3).clouds, list.get(i-3).dt, list.get(i-3).cityId, list.get(i-3).cityName, list.get(i-3).country))
        }
        return fiveDayForecast
    }

}