package com.bookfriends

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

sealed class Destinations(
    val icon: Int,
    val title: String,
    val route: String,
) {
    object Home : Destinations(
        icon = com.bookfriends.R.drawable.ic_home,
        title = "Home",
        route = "Home",
    )

    object Note : Destinations(
        icon = com.bookfriends.R.drawable.ic_note,
        title = "Note",
        route = "Note",
    )

    object Filter : Destinations(
        icon = com.bookfriends.R.drawable.ic_outline_filter,
        title = "Filter",
        route = "Filter",
    )

    object Profile : Destinations(
        icon = com.bookfriends.R.drawable.ic_user,
        title = "Profile",
        route = "Profile",
    )

    object Discussionboard : Destinations(
        icon = com.bookfriends.R.drawable.ic_discussion_board,
        title = "Discussionboard",
        route = "Discussionboard",
    )

    object NearbyBookstores : Destinations(
        icon = com.bookfriends.R.drawable.ic_book_cover,
        title = "NearbyBookstores",
        route = "NearbyBookstores",
    )

    object Plan : Destinations(
            icon = com.bookfriends.R.drawable.ic_list_icon,
            title = "Plan",
            route = "Plan",
    )

    object AddFriends : Destinations(
        icon = com.bookfriends.R.drawable.add_user_black,
        title = "AddFriends",
        route = "AddFriends",
    )
}
