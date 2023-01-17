package com.bookfriends.discussionboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bookfriends.models.Book
import com.bookfriends.network.Resources
import com.bookfriends.repository.AuthRepository


class DiscussionBoardViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    var discussionBoardUiStateUiState by mutableStateOf(DiscussionBoardUiState())

    val user = authRepository.user()
    val hasUser: Boolean
        get() = authRepository.hasUser()
    private val userId: String
        get() = authRepository.getUserId()
}

data class DiscussionBoardUiState(
    val bookList: Resources<List<Book>> = Resources.Loading(),
    val bookDeletedStatus: Boolean = false,
)