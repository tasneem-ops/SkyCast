package com.example.skycast.alert.model.repository

import com.example.skycast.alert.model.db.AlertsLocalDataSource
import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.network.AlertsRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlertsRepositoryImpl private constructor(private val remoteDataSource: AlertsRemoteDataSource,
                                               private val localDataSource: AlertsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : AlertsRepository {

    companion object{
        @Volatile
        private var INSTANCE: AlertsRepositoryImpl? = null
        fun getInstance (remoteDataSource: AlertsRemoteDataSource, localDataSource: AlertsLocalDataSource,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AlertsRepositoryImpl {
            return INSTANCE ?: synchronized(this){
                val instance = AlertsRepositoryImpl(remoteDataSource, localDataSource, ioDispatcher)
                INSTANCE = instance
                instance
            }
        }
    }
    override fun getAlerts(): Flow<List<AlertDTO>> {
        return localDataSource.getAlerts()
    }

    override suspend fun addAlert(alertDTO: AlertDTO): Long = withContext(ioDispatcher) {
        return@withContext localDataSource.addAlert(alertDTO)
    }

    override suspend fun deleteAlert(alertDTO: AlertDTO): Int  = withContext(ioDispatcher){
        return@withContext localDataSource.delete(alertDTO)
    }

    override fun getAlert(latLng: LatLng, apiKey: String): Flow<Alert> {
        return remoteDataSource.getAlert(latLng, apiKey)
    }
}