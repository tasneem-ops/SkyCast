package com.example.skycast.alert.model.network

import com.example.skycast.alert.model.dto.Alert
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface AlertsRemoteDataSource {
    fun getAlert(latLng: LatLng, apiKey: String): Flow<Alert>
}