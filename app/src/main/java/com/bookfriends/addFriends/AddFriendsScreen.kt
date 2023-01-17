package com.bookfriends.addFriends

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.bookfriends.models.User
import com.bookfriends.network.Resources
import com.bookfriends.ui.theme.BooksTheme

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun AddFriendsScreen(
    addFriendsViewModel: AddFriendsViewModel?,
) {
    val addFriendsUiState = addFriendsViewModel?.addFriendsUiState ?: AddFriendsUiState()

    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(key1 = Unit) {
        addFriendsViewModel?.loadUsers()
    }

    Scaffold(
        scaffoldState = scaffoldState,
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (addFriendsUiState.UserList) {
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
                    val prefs = LocalContext.current.getSharedPreferences(
                        "com.bookfriends.prefs",
                        Context.MODE_PRIVATE
                    );

                    // Get the value of the key "sort_by"
                    val sortBy = prefs.getString("sort_by", "First name")

                    // Get the value of the key "sort_order"
                    val sortOrder = prefs.getString("sort_order", "Ascending")

                    if (sortBy != null) {
                        addFriendsViewModel?.onOptionSelected(sortBy)
                    }

                    val radioOptions = addFriendsUiState.radioOptions

                    Column {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            radioOptions.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = addFriendsUiState.selectedRadioOption == item,
                                        onClick = {
                                            addFriendsViewModel?.onOptionSelected(item)
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

                    val radioOptionsOrder = addFriendsUiState.radioOptionsOrder

                    if (sortOrder != null) {
                        addFriendsViewModel?.onOptionOrderSelected(sortOrder)
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
                                        selected = addFriendsUiState.selectedRadioOptionOrder == item,
                                        onClick = {
                                            addFriendsViewModel?.onOptionOrderSelected(item)
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
                        if (addFriendsUiState.UserList.data?.size == 0) {
                            item {
                                Text(
                                    text = "No users yet",
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
                                addFriendsUiState.UserList.data ?: listOf()
                            ) { User ->
                                UserItem(
                                    User = User
                                )
                            }
                        }


                    }
                }
                else -> {
                    Text(
                        text = addFriendsUiState
                            .UserList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }


            }


        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    User: User,
) {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(imageUri.value.ifEmpty { com.bookfriends.R.drawable.ic_user }
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri.value = it.toString() }
    }

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
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.disabled
                    ) {
                        Image(
                            painter = painter,
                            "Profile picture",
                            modifier = Modifier
                                .wrapContentSize()
                                .border(color = Color.LightGray, width = 1.dp)
                                .clickable { launcher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${User.firstName} ${User.lastName}",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PrevAddFriendsScreen() {
    BooksTheme {
        AddFriendsScreen(
            addFriendsViewModel = null
        )
    }
}


