package com.bookfriends.filter



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.MainActivity
import com.bookfriends.models.Book
import com.bookfriends.models.Note
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.network.Resources
import com.bookfriends.repository.AuthRepository
import com.bookfriends.repository.NoteRepository
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log
import androidx.activity.ComponentActivity
import com.bookfriends.MainApp
import com.bookfriends.ui.theme.ThemeState

import kotlinx.coroutines.launch



class FilterViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val NoteRepository: NoteRepository = NoteRepository(),
    private val networkServiceAdapter: NetworkServiceAdapter = NetworkServiceAdapter
) : ViewModel() {
    var filterUiState by mutableStateOf(FilterUiState())

    val user = authRepository.user()
    val hasUser: Boolean
        get() = authRepository.hasUser()
    private val userId: String
        get() = authRepository.getUserId()


    fun loadBooks(){
        if (hasUser){
            if (userId.isNotBlank()){
                getBooks()
            }
        }else{
            filterUiState = filterUiState.copy(BookList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    private fun getBooks() = viewModelScope.launch {
        networkServiceAdapter.getBooks().collect {
            filterUiState = filterUiState.copy(BookList = it)
            onOptionSelected(filterUiState.selectedRadioOption)


        }
    }

    fun onOptionSelected (radioOption : String) {
        filterUiState = filterUiState.copy(selectedRadioOption = radioOption)
        if (radioOption == "Number of pages" && filterUiState.selectedRadioOptionOrder == "Ascending") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(filterUiState.BookList.data?.sortedBy { it.numPages }))
        } else if (radioOption == "Number of pages" && filterUiState.selectedRadioOptionOrder == "Descending") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(filterUiState.BookList.data?.sortedByDescending { it.numPages }))
        } else if (radioOption == "Title" && filterUiState.selectedRadioOptionOrder == "Ascending") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(filterUiState.BookList.data?.sortedBy { it.name }))
        } else if (radioOption == "Title" && filterUiState.selectedRadioOptionOrder == "Descending") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(filterUiState.BookList.data?.sortedByDescending { it.name }))
        }
        // Save selected radio option in Shared Preferences


    }

    fun onOptionOrderSelected (radioOption : String) {
        filterUiState = filterUiState.copy(selectedRadioOptionOrder = radioOption)
        if (radioOption == "Ascending" && filterUiState.selectedRadioOption == "Number of pages") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(
                data = filterUiState?.BookList?.data?.sortedBy { it.numPages }
            ))
        }
        if (radioOption == "Descending" && filterUiState.selectedRadioOption == "Number of pages") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(
                data = filterUiState?.BookList?.data?.sortedByDescending { it.numPages }
            ))
        }

        if (radioOption == "Ascending" && filterUiState.selectedRadioOption == "Title") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(
                data = filterUiState?.BookList?.data?.sortedBy { it.name }
            ))
        }

        if (radioOption == "Descending" && filterUiState.selectedRadioOption == "Title") {
            filterUiState = filterUiState.copy(BookList = Resources.Success(
                data = filterUiState?.BookList?.data?.sortedByDescending { it.name }
            ))
        }
    }




}

data class FilterUiState(
    val BookList: Resources<List<Book>> = Resources.Loading(),
    val radioOptions : List<String> = listOf("Title", "Number of pages"),
    val selectedRadioOption : String = "Title",
    val radioOptionsOrder : List<String> = listOf("Ascending", "Descending"),
    val selectedRadioOptionOrder : String = "Ascending"
)




