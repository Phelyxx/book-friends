package com.bookfriends.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bookfriends.repository.AuthRepository
import com.bookfriends.repository.NoteRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.bookfriends.models.Note


class NoteDetailViewModel(
    private val noteRepository: NoteRepository = NoteRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set

    private val hasUser: Boolean
        get() = authRepository.hasUser()

    private val user:FirebaseUser?
        get() = authRepository.user()


    fun onTitleChange(title: String) {
        detailUiState = detailUiState.copy(title = title)
    }

    fun onNoteChange(note: String) {
        detailUiState = detailUiState.copy(note = note)
    }

    fun addNote(){
        if (hasUser && detailUiState.title.isNotBlank() && detailUiState.note.isNotBlank()){
            noteRepository.addNote(
                userId = user!!.uid,
                title = detailUiState.title,
                description = detailUiState.note,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now()
            ){
                detailUiState = detailUiState.copy(noteAddedStatus = it)
            }

        }
        else if (!detailUiState.title.isNotBlank() || !detailUiState.note.isNotBlank() ) {
            detailUiState = detailUiState.copy(noteBlankStatus = true)
        }

        else{
            detailUiState = detailUiState.copy(noInternetStatus = true)
        }



    }

    fun setEditFields(note: Note){
        detailUiState = detailUiState.copy(
            title = note.title,
            note = note.description
        )
    }

    suspend fun getNote(noteId:String){
        noteRepository.getNote(
            noteId = noteId,
            onError = {},
        ){
            detailUiState = detailUiState.copy(selectedNote = it)
            detailUiState.selectedNote?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateNote(
        noteId: String
    ){
        if (!detailUiState.title.isNotBlank() || !detailUiState.note.isNotBlank() ) {
            detailUiState = detailUiState.copy(noteBlankStatus = true)
        }
        else {
            noteRepository.updateNote(
                title = detailUiState.title,
                note = detailUiState.note,
                noteId = noteId,
                color = detailUiState.colorIndex
            ) {
                detailUiState = detailUiState.copy(updateNoteStatus = it)
            }
        }

    }

    fun resetNoteAddedStatus(){
        detailUiState = detailUiState.copy(
            noteAddedStatus = false,
        )
    }

    fun resetNoteUpdatedStatus(){
        detailUiState = detailUiState.copy(
            updateNoteStatus = false,
        )
    }

    fun resetNoteBlankStatus(){
        detailUiState = detailUiState.copy(
            noteBlankStatus = false,
        )
    }

    fun resetState(){
        detailUiState = DetailUiState()
    }

    fun resetNoInternetStatus(){
        detailUiState = detailUiState.copy(
            noInternetStatus = false,
        )
    }
}

data class DetailUiState(
    val colorIndex: Int = 0,
    val title: String = "",
    val note: String = "",
    val noteAddedStatus: Boolean = false,
    val updateNoteStatus: Boolean = false,
    val noteBlankStatus : Boolean = false,
    val noInternetStatus : Boolean = false,
    val selectedNote: Note? = null,
)

















