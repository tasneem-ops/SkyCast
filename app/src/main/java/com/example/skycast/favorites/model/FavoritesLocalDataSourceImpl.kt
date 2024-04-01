package com.example.skycast.favorites.model

import com.example.skycast.favorites.model.db.FavDao
import com.example.skycast.favorites.model.dto.FavDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoritesLocalDataSourceImpl private constructor(private val favDao : FavDao,
                                                       private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    :FavoritesLocalDataSource{

    companion object{
        @Volatile
        private var INSTANCE: FavoritesLocalDataSourceImpl? = null
        fun getInstance (favDao : FavDao,
                         ioDispatcher: CoroutineDispatcher = Dispatchers.IO): FavoritesLocalDataSourceImpl {
            return INSTANCE ?: synchronized(this){
                val instance = FavoritesLocalDataSourceImpl(favDao, ioDispatcher)
                INSTANCE = instance
                instance
            }
        }
    }
    override suspend fun addFav(favDTO: FavDTO): Long = withContext(ioDispatcher){
        return@withContext favDao.addFav(favDTO)
    }

    override fun getAllFav(): Flow<List<FavDTO>> {
        return favDao.getAllFav()
    }

    override suspend fun deleteFav(favDTO: FavDTO): Int = withContext(ioDispatcher){
        return@withContext favDao.deleteFav(favDTO)
    }

}