package com.example.skycast.alert.alarm_manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.skycast.MainActivity
import com.example.skycast.R

const val CHANNEL_ID = "CHANNEL_ID"
const val NOTIFICATION_ID = 123456
const val DATA_NOTIFICATION_ID = 1234

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.app_name)
        val description: String = context.getString(R.string.app_name)
        val importance : Int = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        val notificationSound = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.alarm}")
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(notificationSound, attributes)
        channel.enableVibration(true)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
fun sendDataNotification(context: Context, data : String) : Notification {
    val intent = Intent(context, MainActivity::class.java)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE)
    val notificationSound = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.alarm}")
    val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()
    val vibrate = LongArray(4)
    vibrate[0] = 0L
    vibrate[1] = 100L
    vibrate[2] = 200L
    vibrate[3] = 300L
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.scattered_clouds)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("$data")
            .setSound(notificationSound)
            .setVibrate(vibrate)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)

    createNotificationChannel(context)
    notificationManager.notify(DATA_NOTIFICATION_ID, builder.build())
    return  builder.build()
}