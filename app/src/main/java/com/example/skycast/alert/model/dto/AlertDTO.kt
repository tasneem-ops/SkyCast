package com.example.skycast.alert.model.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "alerts", primaryKeys = ["latitude", "longitude", "start"])
data class AlertDTO(
    var latitude: Double,
    var longitude: Double,
    var cityName: String?,
    var start: Long,
    var end: Long,
    var notificationEnabled: Boolean,
    var notificationType: NotificationType,
    var lang: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        NotificationType.valueOf(parcel.readString() ?: NotificationType.NOTIFICATION.name),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(cityName)
        parcel.writeLong(start)
        parcel.writeLong(end)
        parcel.writeByte(if (notificationEnabled) 1 else 0)
        parcel.writeString(notificationType.name)
        parcel.writeString(lang)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlertDTO> {
        override fun createFromParcel(parcel: Parcel): AlertDTO {
            return AlertDTO(parcel)
        }

        override fun newArray(size: Int): Array<AlertDTO?> {
            return arrayOfNulls(size)
        }
    }
}

enum class NotificationType{
    ALARM, NOTIFICATION
}