package com.example.skycast.database

import android.content.Context
import com.example.skycast.model.dto.HourForecast
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl private constructor(val context : Context) : LocalDataSource {
    lateinit var dao: ForecastDao
    init {
        dao = WeatherDB.getInstance(context).getForecastDao()
    }
    companion object{
        @Volatile
        private var INSTANCE: LocalDataSourceImpl? = null
        fun getInstance (context: Context): LocalDataSourceImpl{
            return INSTANCE ?: synchronized(this){
                val instance = LocalDataSourceImpl(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getAllForecast(): Flow<List<HourForecast>> {
        return dao.getAllForecast()
    }

    override suspend fun getTodayForecast(): Flow<List<HourForecast>> {
        return dao.getTodayForecast()
    }

    override suspend fun insertHourForecast(hourForecast: HourForecast): Long {
        return dao.insertHourForecast(hourForecast)
    }

    override suspend fun insertAllForecast(vararg hourForecast: HourForecast): List<Long> {
        return dao.insertAllForecast(*hourForecast)
    }

    override suspend fun deleteHourForecast(hourForecast: HourForecast): Int {
        return dao.deleteHourForecast(hourForecast)
    }

    override suspend fun deleteAllForecast(): Int {
        return dao.deleteAllForecast()
    }

}