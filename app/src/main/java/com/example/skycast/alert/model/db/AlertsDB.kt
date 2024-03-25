package com.example.skycast.alert.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.home.model.db.WeatherDB

@Database(entities = arrayOf(AlertDTO::class), version = 1)
abstract class AlertsDB : RoomDatabase(){
    abstract fun getAlertsDao() : AlertsDao
    companion object{
        @Volatile
        private var INSTANCE: AlertsDB? = null
        fun getInstance (context: Context): AlertsDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AlertsDB::class.java, "alerts_database")
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}