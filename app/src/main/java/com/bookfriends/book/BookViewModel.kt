package com.bookfriends.book

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bookfriends.models.Book
import com.google.firebase.auth.FirebaseUser
import com.bookfriends.repository.AuthRepository

class BookViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val hasUser: Boolean
        get() = authRepository.hasUser()

    private val user:FirebaseUser?
        get() = authRepository.user()

    fun onColorChange(colorIndex: Int) {
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onTitleChange(title: String) {
        detailUiState = detailUiState.copy(title = title)
    }

    fun onBookChange(book: String) {
        detailUiState = detailUiState.copy(book = book)
    }

    fun addBook(){
        if (hasUser && detailUiState.title.isNotBlank() && detailUiState.book.isNotBlank()){


        }
        else if (!detailUiState.title.isNotBlank() || !detailUiState.book.isNotBlank() ) {
            detailUiState = detailUiState.copy(bookBlankStatus = true)
        }


    }




    fun resetBookAddedStatus(){
        detailUiState = detailUiState.copy(
            bookAddedStatus = false,
        )
    }



    fun resetBookBlankStatus(){
        detailUiState = detailUiState.copy(
            bookBlankStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }
}

data class DetailUiState(
    val colorIndex: Int = 0,
    val title: String = "",
    val book: String = "",
    val bookAddedStatus: Boolean = false,
    val updateBookStatus: Boolean = false,
    val bookBlankStatus : Boolean = false,
    val selectedBook: Book? = null,
)

















