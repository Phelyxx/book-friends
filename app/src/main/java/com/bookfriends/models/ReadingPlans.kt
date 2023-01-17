package com.bookfriends.models

import com.google.firebase.Timestamp

data class ReadingPlans (
        var id: String = "",
        var bookId: String = "",
        var discussionsIds: List<String>? = null,
        var endDate: Timestamp = Timestamp.now(),
        var startDate: Timestamp = Timestamp.now(),
        var title: String = "",
        var usersIds: List<String>? = null,
)