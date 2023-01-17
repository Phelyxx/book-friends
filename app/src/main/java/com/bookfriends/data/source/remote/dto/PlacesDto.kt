package com.bookfriends.data.source.remote.dto

import com.bookfriends.domain.model.Places

data class PlacesDto(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>,
    val status: String
)

fun PlacesDto.toListPlaces(): List<Places> {
    val resultEntries = results.mapIndexed { _, entries ->
        Places(
            name = entries.name
        )
    }
    return resultEntries
}