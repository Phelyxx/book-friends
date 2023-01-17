package com.bookfriends.filter

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bookfriends.models.Book
import com.google.firebase.Timestamp
import com.bookfriends.network.Resources
import com.bookfriends.ui.theme.BooksTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun FilterScreen(
    filterViewModel: FilterViewModel?,
    navToLoginPage: () -> Unit,
) {
    val filterUiState = filterViewModel?.filterUiState ?: FilterUiState()

    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(key1 = Unit){
        filterViewModel?.loadBooks()
    }

    Scaffold(
        scaffoldState = scaffoldState,
    ) {
            padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (filterUiState.BookList) {
                is Resources.Loading<*> -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success<*> -> {

                    Text(
                        text = "Order by",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)

                    )
                    // Get shared preferences
                    val prefs = LocalContext.current.getSharedPreferences("com.bookfriends.prefs", Context.MODE_PRIVATE);

                    // Get the value of the key "sort_by"
                    val sortBy = prefs.getString("sort_by", "Title")

                    // Get the value of the key "sort_order"
                    val sortOrder = prefs.getString("sort_order", "Ascending")

                    if (sortBy != null) {
                        filterViewModel?.onOptionSelected(sortBy)
                    }

                    val radioOptions = filterUiState.radioOptions

                    Column {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            radioOptions.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = filterUiState.selectedRadioOption == item,
                                        onClick = {
                                            filterViewModel?.onOptionSelected(item)
                                            // Save the value of the key "sort_by"
                                            prefs.edit().putString("sort_by", item).apply()
                                        },
                                        enabled = true,
                                    )
                                    Text(text = item, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }

                    val radioOptionsOrder = filterUiState.radioOptionsOrder

                    if (sortOrder != null) {
                        filterViewModel?.onOptionOrderSelected(sortOrder)
                    }

                    Column {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            radioOptionsOrder.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = filterUiState.selectedRadioOptionOrder == item,
                                        onClick = {
                                            filterViewModel?.onOptionOrderSelected(item)
                                            // Save the value of the key "sort_order"
                                            prefs.edit().putString("sort_order", item).apply()
                                        },
                                        enabled = true,
                                    )
                                    Text(text = item, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }

                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(300.dp),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 16.dp,
                            end = 12.dp,
                            bottom = 16.dp
                        )
                    ) {
                        if (filterUiState.BookList.data?.size == 0) {
                            item {
                                Text(
                                    text = "No books yet",
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
                                filterUiState.BookList.data ?: listOf()
                        ) { Book ->
                            BookItem (
                                Book = Book
                            )
                        }
                        }



                    }
                }
                else -> {
                    Text(
                        text = filterUiState
                            .BookList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }


            }


        }


    }
    LaunchedEffect(key1 = filterViewModel?.hasUser){
        if (filterViewModel?.hasUser == false){
            navToLoginPage.invoke()
        }
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookItem(
    Book: Book,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 8.dp
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Book.name,
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
                Image(
                    painter = rememberAsyncImagePainter(Book.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(256.dp)
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = "Author: " + Book.author,
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
                    text = "Genre: " + Book.genre,
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
                    text = "Number of pages: " + Book.numPages.toString(),
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
                    text = "ISBN: " + Book.isbn,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4
                )

            }










        }


    }


}




@Preview
@Composable
fun PrevFilterScren () {
    BooksTheme {
        FilterScreen(
            filterViewModel = null,
            navToLoginPage = { /*TODO*/ },
        )
    }
}


