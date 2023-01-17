package com.bookfriends.discussionboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bookfriends.R
import com.bookfriends.discussionboard.DiscussionBoardViewModel
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.profile.ProfileScreen
import com.bookfriends.ui.theme.BooksTheme

@Composable
fun DiscussionBoardScreen(
    discussionBoardViewModel: DiscussionBoardViewModel? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoardCoverImage()
        BookTittle("Harry potter and the deathly hallows")
        Members()
        Discussions()
    }
}

//Discussion board cover image
@Composable
fun BoardCoverImage() {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(com.bookfriends.R.drawable.harry_potter_and_the_deathly_hallows),
            "Discussions board cover image",
            modifier = Modifier
                .wrapContentSize()
                .border(color = Color.LightGray, width = 1.dp),
            contentScale = ContentScale.Crop
        )
    }
}

//Discussion board Book tittle
@Composable
fun BookTittle(text: String) {
    Text(
        text, modifier = Modifier
            .padding(8.dp)
    )
}

//Discussion board members
@Composable
fun Members() {
    val onClick: () -> Unit = {}

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        items(4) { index ->
            Image(
                painterResource(com.bookfriends.R.drawable.ic_user),
                "Profile picture $index",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Clickable image",
                        onClick = onClick
                    )
                    .background(Color.Gray)
                    .size(50.dp)

            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

//Discussions section (Open discussions and new discussions)
@Composable
fun Discussions() {

    val onClick: () -> Unit = {}
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {

        // Head Discussions Composable
        Row() {
            Text(
                "Discussions:",
                modifier = Modifier
                    .weight(2F)
            )
            //Add new discussion
            Button(
                onClick = { },
            ) {
                Text(
                    "New discussion",
                    color = Color.White
                )
            }
        }

        //Open Discussions List
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(8) { index ->
                Text(
                    "What was your favorite quote/passage?",
                    modifier = Modifier
                        .clickable(
                            enabled = true,
                            onClickLabel = "Clickable text",
                            onClick = onClick
                        )
                        .padding(8.dp)
                )
            }
        }

        Text(
            "What was your favorite quote/passage?",
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            //User who comments
            Column() {
                Row() {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Image(
                            painter = painterResource(com.bookfriends.R.drawable.ic_user),
                            "Profile picture",
                            modifier = Modifier
                                .wrapContentSize()
                                .border(color = Color.LightGray, width = 1.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Column() {
                        Text(
                            text = "Valentina Torres",
                        )
                        Text(
                            text = "1 hour ago",
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "The ending was quiet unexpected... I love it!")
            }

            Spacer(modifier = Modifier.width(8.dp))

            //Add comment button
            Button(
                onClick = { /*TODO*/ },
            ) {
                Image(
                    painter = painterResource(id = com.bookfriends.R.drawable.ic_add_button),
                    contentDescription = "Add comment button"
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrevDiscussionBoardScreen() {
    BooksTheme {
        DiscussionBoardScreen()
    }
}