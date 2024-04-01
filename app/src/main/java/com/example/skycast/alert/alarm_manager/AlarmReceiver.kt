package com.example.skycast.alert.alarm_manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.skycast.R
import com.example.skycast.alert.model.db.AlertsDB
import com.example.skycast.alert.model.db.AlertsLocalDataSourceImpl
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.model.dto.NotificationType
import com.example.skycast.alert.model.network.AlertsRemoteDataSourceImpl
import com.example.skycast.alert.model.repository.AlertsRepositoryImpl
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
        val repository = AlertsRepositoryImpl.getInstance(
            AlertsRemoteDataSourceImpl.getInstance(),
            AlertsLocalDataSourceImpl.getInstance(AlertsDB.getInstance(context).getAlertsDao()))
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
                                    val intent = Intent(context, AlarmService::class.java)
                                    intent.putExtra("MESSAGE", it.event)
                                    intent.putExtra("CITY",item.cityName)
                                    context.startService(intent)
                                }
                            }
                        }
                        true
                    }
            } catch (e: NoSuchElementException){
                if (item.notificationEnabled){
                    when(item.notificationType){
                        NotificationType.NOTIFICATION -> {
                            Log.i(TAG, "Notification: ")
                            sendDataNotification(context, context.getString(R.string.no_alerts))
                        }
                        NotificationType.ALARM ->{
                            Log.i(TAG, "Alarm: ")
                            val intent = Intent(context, AlarmService::class.java)
                            intent.putExtra("MESSAGE", context.getString(R.string.no_alerts))
                            intent.putExtra("CITY",item.cityName)
                            context.startService(intent)
                        }
                    }
                }
            }
        }
    }
}
