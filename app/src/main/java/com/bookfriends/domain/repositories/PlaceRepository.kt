package com.bookfriends.domain.repositories

import com.bookfriends.domain.model.Places
import kotlinx.coroutines.flow.Flow
import com.bookfriends.data.Result

interface PlaceRepository {
    fun getPlaces(location: String, radius: Int, type: String, key: String): Flow<Result<List<Places>>>
}