package com.example.skycast.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycast.R
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.model.source.local.WeatherLocalDataSourceImpl
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.home.model.source.network.WeatherRemoteDataSourceImpl
import com.example.skycast.home.model.source.repository.WeatherRepositoryImpl

class DailyCacheWorker(val context: Context, var workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val repository = WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(), WeatherLocalDataSourceImpl.getInstance(
            WeatherDB.getInstance(context).getDailyWeatherDao(),
            WeatherDB.getInstance(context).getHourlyWeatherDao()))
        val settingsDataSource = UserSettingsDataSource.getInstance(context)
        val latLng = settingsDataSource.getSavedLocation()
        if(latLng.latitude == 0.0 || latLng.longitude == 0.0){
            return Result.failure()
        }
        repository.updateWeatherCache(latLng, context.getString(R.string.apiKey))
        return Result.success()
    }
}