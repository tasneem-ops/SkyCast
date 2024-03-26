package com.example.skycast.location.model.network

import com.example.skycast.location.model.Place
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("search?")
    suspend fun getSearchSuggestions(@Query("q") query: String,
                             @Query("format") format : String,
                             @Query("accept-language") lang : String,
                             @Query("limit") limit : Int) : Response<List<Place>>
}