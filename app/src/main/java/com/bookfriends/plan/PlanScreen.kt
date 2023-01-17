package com.bookfriends.plan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bookfriends.R
import com.bookfriends.ui.theme.BooksTheme

@Composable
fun PlanScreen(
        planViewModel: PlanViewModel? = null,
) {
    val planUiState = planViewModel?.planUiState
    val isError = planUiState?.planError != null
    val context = LocalContext.current

    Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
                text = "Participants",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp)
        )

        if (isError){
            Text(
                    text = planUiState?.planError ?: "Error desconocido",
                    color = Color.Red,
            )
        }

        Row(modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(10.dp)
            .clip(CircleShape)
            .requiredHeight(100.dp)
            ,

            horizontalArrangement =  Arrangement.spacedBy(10.dp)
        ) {
            Image(bitmap= ImageBitmap.imageResource(id = R.drawable.pic_not_found), modifier =Modifier.clip(
                CircleShape), contentDescription = "person1")
            Image(bitmap= ImageBitmap.imageResource(id = R.drawable.pic_not_found), modifier =Modifier.clip(
                CircleShape), contentDescription = "person2")
            Image(bitmap= ImageBitmap.imageResource(id = R.drawable.pic_not_found), modifier =Modifier.clip(
                CircleShape), contentDescription = "person3")
            Image(bitmap= ImageBitmap.imageResource(id = R.drawable.pic_not_found), modifier =Modifier.clip(
                CircleShape), contentDescription = "person4")
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {

            AsyncImage(model = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTRQlcYvc6gYzVS8swAdppDbJqW7o7uY8eIInd_5M5QPlZ7u1FV" ,
                modifier =Modifier.requiredHeight(400.dp),
                contentDescription = "book1")

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "user")
                LinearProgressIndicator(progress = 0.7f, modifier = Modifier.padding(vertical = 10.dp)) //user
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "user1")
                LinearProgressIndicator(progress = 0.1f, modifier = Modifier.padding(vertical = 10.dp)) //user
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "user2")
                LinearProgressIndicator(progress = 0.5f, modifier = Modifier.padding(vertical = 10.dp)) //user
            }
        }


        Spacer(modifier = Modifier.size(16.dp))


        Button(
                onClick = { planViewModel?.planUser(context) },
                colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                ),
        ) {
            Text(text = "Ir a plan")
        }
        Spacer(modifier = Modifier.size(16.dp))



        if (planUiState?.isLoading == true){
            CircularProgressIndicator()
        }

        }
    }




@Preview(showSystemUi = true)
@Composable
fun PrevPlanScreen() {
    BooksTheme {
        PlanScreen()
    }
}

