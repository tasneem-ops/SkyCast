package com.example.skycast.favorites.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.favorites.model.dto.FavDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFav(favDTO: FavDTO) : Long

    @Query("SELECT * FROM favorites")
    fun getAllFav() : Flow<List<FavDTO>>

    @Delete
    suspend fun deleteFav(favDTO: FavDTO) : Int
}