package com.bookfriends.repository

data class DBretrieve<T, E : Exception?>(
        var data: T? = null,
        var e: E? = null
)