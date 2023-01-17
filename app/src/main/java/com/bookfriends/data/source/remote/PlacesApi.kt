package com.bookfriends.data.source.remote

import com.bookfriends.data.source.remote.dto.PlacesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("")
    suspend fun getPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") key: String,

        ): PlacesDto
}