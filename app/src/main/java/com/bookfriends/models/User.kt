package com.bookfriends.models

import com.google.firebase.Timestamp

data class User(
        var birthDate: Timestamp = Timestamp.now(),
        var email: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var imageUrl: String? = "",
        var friendsIds: List<String> = listOf(),
        var registrationDate: Timestamp = Timestamp.now(),
)