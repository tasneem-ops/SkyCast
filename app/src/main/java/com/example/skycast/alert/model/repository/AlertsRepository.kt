package com.example.skycast.alert.model.repository

import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface AlertsRepository {
    fun getAlerts() : Flow<List<AlertDTO>>
    suspend fun addAlert(alertDTO: AlertDTO) : Long
    suspend fun deleteAlert(alertDTO: AlertDTO) : Int
    fun getAlert(latLng: LatLng, apiKey: String) : Flow<Alert>
}