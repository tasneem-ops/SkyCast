package com.example.skycast.favorites.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skycast.favorites.model.dto.FavDTO

@Database(entities = arrayOf(FavDTO::class), version = 1)
abstract class FavDB : RoomDatabase(){
    abstract fun getFavDao() : FavDao
    companion object{
        @Volatile
        private var INSTANCE: FavDB? = null
        fun getInstance (context: Context): FavDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, FavDB::class.java, "fav_database")
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}