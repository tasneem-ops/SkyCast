package com.example.skycast.alert.alarm_manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.skycast.R
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.dto.NotificationType
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.NoSuchElementException

private const val TAG = "AlarmReceiver"
class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive: ")
        val item : AlertDTO = intent?.extras?.getParcelable("DATA") ?: return
        if (context == null){
            return
        }
        val testLatLng = LatLng(55.7504461, 37.6174943)
        val repository = WeatherRepository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(context))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                repository.getAlert(testLatLng, context.getString(R.string.apiKey))
                    .first {
                        if (item.notificationEnabled){
                            when(item.notificationType){
                                NotificationType.NOTIFICATION -> {
                                    Log.i(TAG, "Notification: ")
                                    sendDataNotification(context, it.event)
                                }
                                NotificationType.ALARM ->{
                                    Log.i(TAG, "Alarm: ")
                                }
                            }
                        }
                        true
                    }
            } catch (e: NoSuchElementException){
                sendDataNotification(context, "No Alerts" +
                        "")
            }

        }
    }
}
