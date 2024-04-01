package com.example.skycast.favorites.model

import com.example.skycast.favorites.model.dto.FavDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFavoritesLocalDataSource : FavoritesLocalDataSource {
    val favList = arrayListOf<FavDTO>()
    override suspend fun addFav(favDTO: FavDTO): Long {
        favList.add(favDTO)
        return 1L
    }

    override fun getAllFav(): Flow<List<FavDTO>> {
        return flow{
            emit(favList)
        }
    }

    override suspend fun deleteFav(favDTO: FavDTO): Int {
        favList.remove(favDTO)
        return 1
    }
}