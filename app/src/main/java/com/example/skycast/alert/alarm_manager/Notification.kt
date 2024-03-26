package com.example.skycast.alert.alarm_manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.skycast.MainActivity
import com.example.skycast.R

const val CHANNEL_ID = "CHANNEL_ID"
const val NOTIFICATION_ID = 123456
const val DATA_NOTIFICATION_ID = 1234

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.app_name)
        val description: String = context.getString(R.string.app_name)
        val importance : Int = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
fun sendDataNotification(context: Context, data : String) : Notification {
    val intent = Intent(context, MainActivity::class.java)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE)

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    // some code goes here
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.scattered_clouds)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("$data")
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    createNotificationChannel(context)
    notificationManager.notify(DATA_NOTIFICATION_ID, builder.build())
    return  builder.build()
}