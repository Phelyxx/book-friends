package com.bookfriends.book

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
import com.bookfriends.Utils
import com.bookfriends.ui.theme.BooksTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BookScreen(
    bookViewModel: BookViewModel?,
    bookId: String,
    onNavigate: () -> Unit,
) {
    val detailUiState = bookViewModel?.detailUiState ?: DetailUiState()
    val isBookIdNotBlank = bookId.isNotBlank()
    val icon = if (isBookIdNotBlank) Icons.Default.Refresh
    else Icons.Default.Check
    LaunchedEffect(key1 = Unit) {
        if (isBookIdNotBlank) {

        } else {
            bookViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isBookIdNotBlank) {

                        } else {
                            bookViewModel?.addBook()
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
            if (detailUiState.bookAddedStatus) {
                scope.launch {
                    bookViewModel?.resetBookAddedStatus()
                    onNavigate.invoke()
                }
            }

            if (detailUiState.updateBookStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("The book note has been successfully updated.")
                    bookViewModel?.resetBookAddedStatus()
                }
            }

            if (detailUiState.bookBlankStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("The note fields cannot be empty")
                }
                bookViewModel?.resetBookBlankStatus()
            }

            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = {
                    bookViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Book") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.book,
                onValueChange = { bookViewModel?.onBookChange(it) },
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
fun PrevBookScreen() {
    BooksTheme {
        BookScreen(bookViewModel = null, bookId = "") {

        }
    }

}
















