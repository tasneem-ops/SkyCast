package com.example.skycast.alert.model.network

import com.example.skycast.alert.model.dto.Alert
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlertsRemoteDataSourceImpl : AlertsRemoteDataSource {
    companion object{
        @Volatile
        private var INSTANCE: AlertsRemoteDataSourceImpl? = null
        fun getInstance (): AlertsRemoteDataSourceImpl {
            return INSTANCE ?: synchronized(this){
                val instance = AlertsRemoteDataSourceImpl()
                INSTANCE = instance
                instance
            }
        }
    }
    override fun getAlert(latLng: LatLng, apiKey: String): Flow<Alert> {
        return flow {
            val responseBody = AlertRetrofitHelper.retrofitService
                .getAlert(latLng.latitude, latLng.longitude, apiKey).body()
            if(responseBody?.alerts?.isEmpty() == true){
                emit(Alert("", "No Alerts For Now", Int.MIN_VALUE, Int.MAX_VALUE, "", emptyList<String>()))
            }
            else{
                responseBody?.alerts?.get(0)?.let { emit(it) }
            }
        }
    }
}