package com.example.skycast.favorites.model

import com.example.skycast.favorites.model.dto.FavDTO
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {
    suspend fun addFav(favDTO: FavDTO) : Long
    fun getAllFav() : Flow<List<FavDTO>>
    suspend fun deleteFav(favDTO: FavDTO) : Int
}