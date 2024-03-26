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
import com.example.skycast.model.repository.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.NoSuchElementException

private const val TAG = "AlarmReceiver"
class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive: ")
        val item : AlertDTO = intent?.extras?.getParcelable("DATA") ?: return
        val intent = Intent(context, AlertService::class.java)
        intent.putExtra("DATA", item)
        if (context == null){
            return
        }
        val repository = Repository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(context))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                repository.getAlert(LatLng(item.latitude, item.longitude), context.getString(R.string.apiKey))
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
