package com.bookfriends.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.bookfriends.ui.theme.BooksTheme

//States from choose options
var actuallOption: String by mutableStateOf("1")
var optionInput: String by mutableStateOf("")

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel?,
    userEmail: String
) {
    val profileUiState = profileViewModel?.profileUiState ?: ProfileUiState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    //Notification edit
    val notification = rememberSaveable { mutableStateOf("") }
    if (notification.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_LONG).show()
        notification.value = ""
    }

    //Dialog state
    var showNameInputDialog by rememberSaveable { mutableStateOf(false) }

    //States from edit user name
    var actualUserName: String ="${profileUiState.firstName} ${profileUiState.lastName}"
    var actualFirstName: String = profileUiState.firstName
    var actualLastName: String = profileUiState.lastName
    var firstNameInput: String = ""
    var lastNameInput: String = ""

    LaunchedEffect(key1 = Unit) {
        profileViewModel?.loadUserProfile(userEmail)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { profileViewModel?.updateProfile(userEmail); notification.value = "Updated"},
                backgroundColor = com.bookfriends.ui.theme.SocialPink
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                )
            }
        },
    ) {
        Column {

            Box() {
                CoverImage()
                ProfilePicture(profileViewModel)
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                //UserInformation
                Column(
                    modifier = Modifier
                        .offset(x = 60.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //UserName("Mario Velasco")
                    TextButton(onClick = { showNameInputDialog = true }) {
                        Text(maxLines = 1, text = actualUserName)
                    }

                    //User Social medias
                    SocialMedias()
                }

                //User description
                Text("Your birthdate: ${profileUiState.birthdate.toString()}")
            }

            //Dialogs
            NameInputDialog(
                showNameInputDialog,
                profileViewModel,
                actualFirstName,
                actualLastName,
                firstNameInput,
                lastNameInput,
                { showNameInputDialog = false },
                {
                    actualUserName = "$firstNameInput $lastNameInput"
                    showNameInputDialog = false
                }
            )

            //Options
            OptionsButtons(
                actuallOption,
                profileViewModel,
                {
                    optionInput = "1"
                    actuallOption = optionInput
                },
                {
                    optionInput = "2"
                    actuallOption = optionInput
                },
                {
                    optionInput = "3"
                    actuallOption = optionInput
                }
            )

        }
    }

    // Image functions



}

//Profile picture
@Composable
fun ProfilePicture(profileViewModel: ProfileViewModel?) {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(imageUri.value.ifEmpty { com.bookfriends.R.drawable.ic_user }
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri.value = it.toString() }
    }

    Card(
        shape = CircleShape,
        modifier = Modifier
            .offset(20.dp, 150.dp)
            .size(100.dp)
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

//Profile cover image
@Composable
fun CoverImage() {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(imageUri.value.ifEmpty { com.bookfriends.R.drawable.profile_cover }
    )

    Card(
        shape = RectangleShape,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painter,
            "Profile cover image",
            modifier = Modifier
                .wrapContentSize()
                .border(color = Color.LightGray, width = 1.dp),
            contentScale = ContentScale.Crop
        )
    }
}

//Profile social medias
@Composable
fun SocialMedias() {
    val onClick: () -> Unit = {}

    Row(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Image(
            painterResource(com.bookfriends.R.drawable.facebook),
            "Facebook",
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClickLabel = "Clickable image",
                    onClick = onClick
                )
                .size(40.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Image(
            painterResource(com.bookfriends.R.drawable.instagram),
            "Instagram",
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClickLabel = "Clickable image",
                    onClick = onClick
                )
                .size(40.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Image(
            painterResource(com.bookfriends.R.drawable.twitter),
            "Twitter",
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClickLabel = "Clickable image",
                    onClick = onClick
                )
                .size(40.dp)
        )
    }
}

//Buttons
@Composable
fun OptionsButtons(
    option: String,
    profileViewModel: ProfileViewModel?,
    onFriends: () -> Unit,
    onReadigPlans: () -> Unit,
    onStatistics: () -> Unit
) {
    Column() {
        Row() {
            Button(
                onClick = { onFriends() },
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = "FRIENDS",
                    color = Color.White
                )
            }

            Button(
                onClick = { onReadigPlans() },
            ) {
                Text(
                    text = "READING PLANS",
                    color = Color.White
                )
            }

            Button(
                onClick = { onStatistics() },
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = "STATISTICS",
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            if (option == "1") {
                FriendsList(profileViewModel = profileViewModel)
            }
            if (option == "2") {
                ReadingPlans()
            }
            if (option == "3") {
                Text(text = "Statistics")
            }
        }


    }
}

//Input dialog
@Composable
fun NameInputDialog(
    show: Boolean,
    profileViewModel: ProfileViewModel?,
    actualFirstName: String,
    actualLastName: String,
    firstNameInput: String,
    lastNameInput: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = "Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = "Change name",
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Row() {
                    Column() {
                        OutlinedTextField(
                            modifier = Modifier.padding(top = 8.dp),
                            value = firstNameInput.ifEmpty { actualFirstName },
                            onValueChange = {
                                profileViewModel?.onFirstNameChange(it)
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            modifier = Modifier.padding(top = 8.dp),
                            value = lastNameInput.ifEmpty { actualLastName },
                            onValueChange = {
                                profileViewModel?.onLastNameChange(it)
                            },
                            singleLine = true
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun FriendsList(profileViewModel: ProfileViewModel?) {
    val onClick: () -> Unit = {}
    val profileUiState = profileViewModel?.profileUiState ?: ProfileUiState()

    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        items(
            profileUiState.friendsList?: listOf()
        ) { friend ->

            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(friend.imageUrl.toString()),
                            "Profile picture",
                            modifier = Modifier
                                .wrapContentSize()
                                .border(color = Color.LightGray, width = 1.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = friend.firstName + " " + friend.lastName,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
fun ReadingPlans() {
    val onClick: () -> Unit = {}

    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        items(20) { index ->

            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Image(
                            painter = painterResource(com.bookfriends.R.drawable.ic_book_cover),
                            "Reading plan book cover",
                            modifier = Modifier
                                .wrapContentSize()
                                .border(color = Color.LightGray, width = 1.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Reading plan $index",
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
        }
    }
}

fun Statistics() {

}

@Preview(showSystemUi = true)
@Composable
fun PrevProfileScreen() {
    BooksTheme {
        ProfileScreen(null, "")
    }
}