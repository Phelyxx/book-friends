package com.bookfriends.data.repositories

import com.bookfriends.data.Result
import com.bookfriends.data.source.remote.PlacesApi
import com.bookfriends.data.source.remote.dto.toListPlaces
import com.bookfriends.domain.model.Places
import com.bookfriends.domain.repositories.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val api: PlacesApi
): PlaceRepository{

    override fun getPlaces(location: String, radius: Int, type: String, key: String): Flow<Result<List<Places>>> = flow{
        emit(Result.Loading())
        try {
            val response = api.getPlaces(location, radius, type, key).toListPlaces()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(
                message = "Oops, something went wrong",
                data = null
            ))
        } catch (e: IOException) {
            emit(Result.Error(
                message = "Couldn't reach server, check your internet connection",
                data = null
            ))
        }
    }
}