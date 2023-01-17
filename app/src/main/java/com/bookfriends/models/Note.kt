package com.bookfriends.models

import com.google.firebase.Timestamp

data class Note(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val documentId: String = "",
)
