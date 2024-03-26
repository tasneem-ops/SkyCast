package com.example.skycast.alert.alarm_manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.skycast.alert.model.dto.AlertDTO

private const val TAG = "AlarmScheduler"
class AlarmScheduler(val context: Context) :IAlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(alarmItem: AlertDTO, apiKey: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("DATA", alarmItem)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmItem.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.start,
            pendingIntent
        )
        Log.i(TAG, "schedule: ")
    }

    override fun cancel(alarmItem: AlertDTO) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmItem.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        Log.i(TAG, "cancel: ")
    }
}