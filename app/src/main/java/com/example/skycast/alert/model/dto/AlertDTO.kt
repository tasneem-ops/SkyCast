package com.example.skycast.alert.model.dto

import androidx.room.Entity

@Entity(tableName = "alerts", primaryKeys = ["latitude", "longitude", "start"])
data class AlertDTO(
    var latitude : Double,
    var longitude : Double,
    val cityName : String,
    var start: Long,
    var end : Long,
    var notificationEnabled : Boolean,
    val notificationType : NotificationType
)
enum class NotificationType{
    ALARM, NOTIFICATION
}