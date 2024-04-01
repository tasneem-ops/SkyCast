package com.example.skycast.alert.model.db

import com.example.skycast.alert.model.dto.AlertDTO
import kotlinx.coroutines.flow.Flow

interface AlertsLocalDataSource {
    fun getAlerts(): Flow<List<AlertDTO>>
    suspend fun addAlert(alert: AlertDTO): Long
    suspend fun delete(alert: AlertDTO): Int
}