package com.example.skycast.alert.alarm_manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.skycast.R

class AlarmService : Service() {
    private lateinit var alarmView : View
    private lateinit var windowManager: WindowManager
    lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        alarmView = LayoutInflater.from(this).inflate(R.layout.alarm_layout, null)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.CENTER }
        windowManager.addView(alarmView, params)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        val eventText = alarmView.findViewById<TextView>(R.id.alert_message)
        val cityText = alarmView.findViewById<TextView>(R.id.alarm_city)
        val dismissBtn = alarmView.findViewById<Button>(R.id.dismissBtn)
        eventText.text = intent?.getStringExtra("MESSAGE")
        cityText.text = intent?.getStringExtra("CITY")
        dismissBtn.setOnClickListener {
            windowManager.removeView(alarmView)
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(alarmView != null && windowManager != null)
            windowManager.removeView(alarmView)
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}