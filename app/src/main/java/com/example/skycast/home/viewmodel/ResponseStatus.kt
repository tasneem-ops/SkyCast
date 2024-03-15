package com.example.skycast.home.viewmodel

import com.example.skycast.model.dto.CurrentForecast
import com.example.skycast.model.dto.Forecast

sealed class ResponseStatus {
    class Success(var data : CurrentForecast) : ResponseStatus()
    class Failure(val msg : String) : ResponseStatus()
    class Loading () : ResponseStatus()
}