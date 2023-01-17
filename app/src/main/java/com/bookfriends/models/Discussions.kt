package com.bookfriends.models

data class Discussions (
        var id: String? = null,
        var commentsIds: List<Comments>? = null,
        var topic: String? = null,
)