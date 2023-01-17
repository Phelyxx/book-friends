package com.bookfriends.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.bookfriends.models.Note
import com.bookfriends.network.Resources
import com.bookfriends.ui.theme.BooksTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun NoteScreen(
    noteViewModel: NoteViewModel?,
    onNoteClick: (id: String) -> Unit,
    navToDetailPage: () -> Unit,
    navToLoginPage: () -> Unit,
) {
    val noteUiState = noteViewModel?.noteUiState ?: NoteUiState()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedNote: Note? by remember {
        mutableStateOf(null)
    }

    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(key1 = Unit) {
        noteViewModel?.loadNotes()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailPage.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (noteUiState.NoteList) {
                is Resources.Loading<*> -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success<*> -> {
                    LazyVerticalGrid(
                        cells = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        if (noteUiState.NoteList.data?.size == 0) {
                            item {
                                Text(
                                    text = "No notes yet",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(16.dp)
                                )
                            }
                            item {
                                Text(
                                    text = "Click the + button to add a note",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(16.dp)
                                )
                            }
                        } else {
                            items(
                                noteUiState.NoteList.data ?: listOf()
                            ) { Note ->
                                NoteItem(
                                    Note = Note,
                                    onLongClick = {
                                        openDialog = true
                                        selectedNote = Note
                                    },
                                ) {
                                    onNoteClick.invoke(Note.documentId)
                                }
                            }
                        }


                    }
                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete Note") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedNote?.documentId?.let {
                                            noteViewModel?.deleteNote(it)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                ) {
                                    Text(text = "Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            }
                        )


                    }
                }
                else -> {
                    Text(
                        text = noteUiState
                            .NoteList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }


            }


        }


    }
    LaunchedEffect(key1 = noteViewModel?.hasUser) {
        if (noteViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    Note: Note,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth(),
    ) {

        Column {
            Text(
                text = Note.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = Note.description,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4
                )

            }
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = formatDate(Note.timestamp),
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End),
                    maxLines = 4
                )

            }


        }


    }


}


private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Preview
@Composable
fun PrevNoteScreen() {
    BooksTheme {
        NoteScreen(
            noteViewModel = null,
            onNoteClick = {},
            navToDetailPage = { /*TODO*/ },
            navToLoginPage = { /*TODO*/ },
        )
    }
}


