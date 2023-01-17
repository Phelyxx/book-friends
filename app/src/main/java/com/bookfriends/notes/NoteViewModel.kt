package com.bookfriends.notes


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.models.Note
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.network.Resources
import com.bookfriends.repository.AuthRepository
import com.bookfriends.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val NoteRepository: NoteRepository = NoteRepository(),
    private val networkServiceAdapter: NetworkServiceAdapter = NetworkServiceAdapter,
) : ViewModel() {
    var noteUiState by mutableStateOf(NoteUiState())

    val user = authRepository.user()
    val hasUser: Boolean
        get() = authRepository.hasUser()
    private val userId: String
        get() = authRepository.getUserId()

    fun loadNotes(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserNotes(userId)
            }
        }else{
            noteUiState = noteUiState.copy(NoteList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    private fun getUserNotes(userId:String) = viewModelScope.launch {
        networkServiceAdapter.getUserNotes(userId).collect {
            noteUiState = noteUiState.copy(NoteList = it)
        }
    }

    fun deleteNote(NoteId:String) = NoteRepository.deleteNote(NoteId){
        noteUiState = noteUiState.copy(NoteDeletedStatus = it)
    }

    fun signOut() = authRepository.signOut()

    fun changeTheme() {
        TODO("Not yet implemented")
    }

}

data class NoteUiState(
    val NoteList: Resources<List<Note>> = Resources.Loading(),
    val NoteDeletedStatus: Boolean = false,
)




