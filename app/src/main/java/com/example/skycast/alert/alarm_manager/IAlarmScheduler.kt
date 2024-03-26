package com.example.skycast.alert.alarm_manager

import com.example.skycast.alert.model.dto.AlertDTO

interface IAlarmScheduler {
    fun schedule(alarmItem :AlertDTO, apiKey: String)
    fun cancel(alarmItem :AlertDTO)
}