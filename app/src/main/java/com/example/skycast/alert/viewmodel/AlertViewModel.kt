package com.example.skycast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.dto.NotificationType
import com.example.skycast.alert.model.repository.AlertsRepository
import com.example.skycast.Response
import com.example.skycast.settings.model.UserSettingsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlertViewModel(private val repository: AlertsRepository, private val settingsDataSource: UserSettingsDataSource) : ViewModel() {
    private val _responseDataState = MutableStateFlow<Response<List<AlertDTO>>>(Response.Loading())
    val respnseDataState: StateFlow<Response<List<AlertDTO>>> = _responseDataState.asStateFlow()
    val lang = settingsDataSource.getPreferredLanguage()
    var alert = AlertDTO(0.0, 0.0, "", 0L, 0L,
        true, NotificationType.NOTIFICATION, lang)
    private val _saveState = MutableStateFlow<Response<String>>(Response.Loading())
    val saveState : StateFlow<Response<String>> = _saveState.asStateFlow()
    fun getAlerts(){
        viewModelScope.launch {
            repository.getAlerts()
                .catch {
                    _responseDataState.value = Response.Failure(it.message.toString())
                }
                .collectLatest {
                    _responseDataState.value = Response.Success(it)
                }
        }
    }

    fun addAlert(){
        if(validateData()){
            viewModelScope.launch(Dispatchers.IO) {
                repository.addAlert(alert)
                _saveState.value = Response.Success("Success")
            }
        }
        else{
            _saveState.value = Response.Failure("Couldn't Save Alert")
        }
    }

    private fun validateData() : Boolean{
        if(alert.latitude == 0.0 || alert.longitude  == 0.0 || alert.start == 0L
            ||alert.end == 0L)
            return false
        return true
    }

    fun deleteAlert(alertDTO: AlertDTO){
        viewModelScope.launch {
            repository.deleteAlert(alertDTO)
        }
    }
}