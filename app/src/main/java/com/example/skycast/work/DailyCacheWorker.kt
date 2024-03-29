package com.example.skycast.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycast.R
import com.example.skycast.alert.model.db.AlertsDB
import com.example.skycast.favorites.model.db.FavDB
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository

class DailyCacheWorker(val context: Context, var workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val repository = WeatherRepository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(
            WeatherDB.getInstance(context).getDailyWeatherDao(),
            WeatherDB.getInstance(context).getHourlyWeatherDao(),
            AlertsDB.getInstance(context).getAlertsDao(),
            FavDB.getInstance(context).getFavDao()))
        val settingsDataSource = UserSettingsDataSource.getInstance(context)
        val latLng = settingsDataSource.getSavedLocation()
        if(latLng.latitude == 0.0 || latLng.longitude == 0.0){
            return Result.failure()
        }
        repository.updateWeatherCache(latLng, context.getString(R.string.apiKey))
        return Result.success()
    }
}