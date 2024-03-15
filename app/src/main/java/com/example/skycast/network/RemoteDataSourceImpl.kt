package com.example.skycast.network

import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.Forecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl private constructor() : RemoteDataSource {
    companion object {
        @Volatile
        private var INSTANCE: RemoteDataSourceImpl? = null
        fun getInstance(): RemoteDataSourceImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = RemoteDataSourceImpl()
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lng: Double,
        apiKey: String,
        units: String,
        lang: String
    ) : Flow<Forecast> {
        return flow{
            RetrofitHelper.retrofitService.getForecast(lat, lng, apiKey, units, lang).body()
                ?.let { emit(it) }
        }
    }
}