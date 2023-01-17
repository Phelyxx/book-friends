package com.bookfriends.models

data class Books(
    var author: String = "",
    val genre : String = "",
    var imageUrl: String = "",
    var isbn: String = "",
    var name: String = "",
    var numPages: Int = 0,

)
