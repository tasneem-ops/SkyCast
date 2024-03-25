package com.example.skycast.alert.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.alert.model.dto.AlertDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Query("SELECT * FROM alerts")
    fun getAllAlerts() : Flow<List<AlertDTO>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlert(alert : AlertDTO) : Long

    @Delete
    suspend fun deleteAlert(alert: AlertDTO) : Int
}