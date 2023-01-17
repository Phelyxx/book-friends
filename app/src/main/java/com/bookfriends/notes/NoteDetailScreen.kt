package com.bookfriends.notes

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bookfriends.ui.theme.BooksTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NoteDetailScreen(
    noteDetailViewModel: NoteDetailViewModel?,
    noteId: String,
    onNavigate: () -> Unit,
) {
    val detailUiState = noteDetailViewModel?.detailUiState ?: DetailUiState()
    val isNoteIdNotBlank = noteId.isNotBlank()
    val icon = if (isNoteIdNotBlank) Icons.Default.Refresh
    else Icons.Default.Check
    LaunchedEffect(key1 = Unit) {
        if (isNoteIdNotBlank) {
            noteDetailViewModel?.getNote(noteId)
        } else {
            noteDetailViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isNoteIdNotBlank) {
                            noteDetailViewModel?.updateNote(noteId)
                        } else {
                            noteDetailViewModel?.addNote()
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (detailUiState.noteAddedStatus) {
                scope.launch {
                    noteDetailViewModel?.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }

           if (detailUiState.noInternetStatus) {
               scope.launch {
                   scaffoldState.snackbarHostState
                       .showSnackbar("The note will be added when you recover internet connection.")
                   noteDetailViewModel?.resetNoInternetStatus()
               }
            }

            if (detailUiState.updateNoteStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("The note note has been successfully updated.")
                    noteDetailViewModel?.resetNoteAddedStatus()
                }
            }

            if (detailUiState.noteBlankStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("The note fields cannot be empty")
                }
                noteDetailViewModel?.resetNoteBlankStatus()
            }

            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = {
                    noteDetailViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.note,
                onValueChange = { noteDetailViewModel?.onNoteChange(it) },
                label = { Text(text = "Note content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )


        }


    }


}


@Preview(showSystemUi = true)
@Composable
fun PrevNoteDetailScreen() {
    BooksTheme {
        NoteDetailScreen(noteDetailViewModel = null, noteId = "") {

        }
    }

}
















