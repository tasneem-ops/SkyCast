package com.example.skycast.model.repository

import com.example.skycast.database.LocalDataSource
import com.example.skycast.model.dto.DayWeather
import com.example.skycast.model.dto.Forecast
import com.example.skycast.model.dto.HourForecast
import com.example.skycast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(val localDataSource: LocalDataSource, val remoteDataSource: RemoteDataSource) : IRepository{
    companion object{
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance (localDataSource: LocalDataSource, remoteDataSource: RemoteDataSource): Repository {
            return INSTANCE ?: synchronized(this){
                val instance = Repository(localDataSource, remoteDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getApiForecast(lat: Double, lng: Double, apiKey: String,
                                        units: String, lang: String): Flow<Forecast> {
        return remoteDataSource.getForecast(lat, lng, apiKey, units, lang)
    }

    override suspend fun getLocalForecast(): Flow<List<HourForecast>> {
        return localDataSource.getAllForecast()
    }

    override suspend fun getTodayForecast(): Flow<List<HourForecast>> {
        return localDataSource.getTodayForecast()
    }

    override suspend fun insertHourForecast(hourForecast: HourForecast): Long {
        return localDataSource.insertHourForecast(hourForecast)
    }

    override suspend fun insertAllForecast(vararg hourForecast: HourForecast): List<Long> {
        return localDataSource.insertAllForecast(*hourForecast)
    }

    override suspend fun deleteHourForecast(hourForecast: HourForecast): Int {
        return localDataSource.deleteHourForecast(hourForecast)
    }

    override suspend fun deleteAllForecast(): Int {
        return localDataSource.deleteAllForecast()
    }
}