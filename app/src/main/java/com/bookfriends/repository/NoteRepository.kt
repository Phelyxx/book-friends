package com.bookfriends.repository

import com.bookfriends.models.Note
import com.bookfriends.network.NetworkServiceAdapter
import com.google.firebase.Timestamp

class NoteRepository {

    suspend fun getNote(
        noteId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (Note?) -> Unit
    ) {
        NetworkServiceAdapter.getNote(noteId, onError, onSuccess)
    }

    fun addNote(
        userId: String,
        title: String,
        description: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit,
    ) {
        NetworkServiceAdapter.addNote(userId, title, description, timestamp, color, onComplete)
    }

    fun updateNote(
        title: String,
        note:String,
        color: Int,
        noteId: String,
        onResult:(Boolean) -> Unit
    ) {
        NetworkServiceAdapter.updateNote(title, note, color, noteId, onResult)
    }

    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit) {
        NetworkServiceAdapter.deleteNote(noteId, onComplete)
    }





















}