package com.bookfriends.repository

import com.bookfriends.models.Note
import com.bookfriends.models.User
import com.bookfriends.network.NetworkServiceAdapter

class UserRepository {
    /*

    fun updateUser(
        firstName: String,
        lastName:String,
        birthDate: Timestamp,
        imageUrl: String,
        userId: String,
        onResult:(Boolean) -> Unit
    ) {
        NetworkServiceAdapter.updateUser(firstName, lastName, birthDate, imageUrl, userId, onResult)
    }*/

    /*suspend fun getUser(
        userId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (User?) -> Unit
    ) {
        NetworkServiceAdapter.getUser(userId, onError, onSuccess)
    }*/

    suspend fun getProfile(
        userEmail: String,
        onError:(Throwable?) -> Unit,
        onSuccess: (User?) -> Unit
    ) {
        NetworkServiceAdapter.getProfile(userEmail, onError, onSuccess)
    }

    fun updateUser(
        userEmail: String,
        firstName: String,
        lastName:String,
        imageUrl: String,
        onResult:(Boolean) -> Unit
    ) {
        NetworkServiceAdapter.updateUser(userEmail, firstName, lastName, imageUrl, onResult)
    }
}
