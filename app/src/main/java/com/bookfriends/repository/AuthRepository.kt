package com.bookfriends.repository


import com.bookfriends.network.NetworkServiceAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    fun user() = NetworkServiceAdapter.user()
    fun hasUser() = NetworkServiceAdapter.hasUser()
    fun getUserId() = NetworkServiceAdapter.getUserId()
    fun getUserEmail() = NetworkServiceAdapter.getUserEmail()

    suspend fun createUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        NetworkServiceAdapter.createUser(email, password, firstName, lastName, birthDate, onComplete)
    }

    suspend fun login(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        NetworkServiceAdapter.login(email, password, onComplete)
    }

    fun signOut() {
        NetworkServiceAdapter.signOut()
    }
}





















