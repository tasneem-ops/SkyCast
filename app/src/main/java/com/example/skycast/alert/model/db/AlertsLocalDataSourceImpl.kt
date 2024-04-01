package com.example.skycast.alert.model.db

import com.example.skycast.alert.model.dto.AlertDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlertsLocalDataSourceImpl (private val alertDao : AlertsDao,
                                 private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : AlertsLocalDataSource {
    companion object{
        @Volatile
        private var INSTANCE: AlertsLocalDataSourceImpl? = null
        fun getInstance (alertDao : AlertsDao,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AlertsLocalDataSourceImpl {
            return INSTANCE ?: synchronized(this){
                val instance = AlertsLocalDataSourceImpl(alertDao,ioDispatcher)
                INSTANCE = instance
                instance
            }
        }
    }
    override fun getAlerts(): Flow<List<AlertDTO>> {
        return alertDao.getAllAlerts()
    }

    override suspend fun addAlert(alert: AlertDTO): Long = withContext(ioDispatcher){
        return@withContext alertDao.addAlert(alert)
    }

    override suspend fun delete(alert: AlertDTO): Int = withContext(ioDispatcher){
        return@withContext alertDao.deleteAlert(alert)
    }
}