package com.bookfriends.models

import com.google.firebase.Timestamp


data class Posts (
        var id: String? = null,
        var likes: Int? = null,
        var numComments: Int? = null,
        var numShares: Int? = null,
        var publicationDate: Timestamp = Timestamp.now(),
        var readingPlanId: String? = null,
        var userId: String? = null
)