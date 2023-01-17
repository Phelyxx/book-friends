package com.bookfriends.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bookfriends.Destinations
import com.bookfriends.R
import com.bookfriends.discussionboard.*
import com.bookfriends.models.Books
import com.bookfriends.models.Comments
import com.bookfriends.models.ReadingPlans
import com.bookfriends.network.Resources

import com.google.firebase.Timestamp
import com.bookfriends.ui.theme.BooksTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


val social_pink = Color(0xFFF62E8E)

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun Home(
    homeViewModel: HomeViewModel?=null,
    navToLoginPage: () -> Unit,
) {
    LaunchedEffect(key1 = homeViewModel?.hasUser) {
        if (homeViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }

    }

    val homeUiState = homeViewModel?.homeUiState ?: HomeViewModel.HomeUiState()

    val scaffoldState = rememberScaffoldState()

    //val noteUiState = homeViewModel?.homeUiState ?: NoteUiState()

    LaunchedEffect(key1 = Unit) {
        //homeViewModel?.loadAll()
        //homeViewModel?.getBooks2()
        //homeViewModel?.getDiscussions()
        //homeViewModel?.getUsers()
        //homeViewModel?.getComments()

    }

    val names = getUserNames(homeUiState)

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) {

        Column() {
            BookScrollable(homeViewModel,homeUiState)
            //planBody(homeUiState)
            commentsBody(homeViewModel,homeUiState)
        }


    }
    LaunchedEffect(key1 = homeViewModel?.hasUser) {
        if (homeViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }



}

fun getUserNames(homeUiState: HomeViewModel.HomeUiState) {
}


@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun commentsBody(homeViewModel: HomeViewModel?, homeUiState: HomeViewModel.HomeUiState) {
    homeViewModel?.getComments()
    when (homeUiState.commentList) {
        is Resources.Loading<*> -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }
        is Resources.Success<*> -> {
            LazyVerticalGrid(
                cells = GridCells.Fixed(1),
                contentPadding = PaddingValues(16.dp),
            ) {
                if (homeUiState.commentList.data?.size == 0) {
                    item {
                        Text(
                            text = "No content yet, add a new plan",
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
                            text = "Click the + button to add a plan",
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
                        homeUiState.commentList.data ?: listOf()
                    ) { Comments ->
                        CommentBox(
                            Comments = Comments,
                        )
                    }
                }

            }
        }
        else -> {
            Text(
                text = homeUiState
                    .commentList.throwable?.localizedMessage ?: "Error desconocido",
                color = Color.Red
            )
        }


    }
}

@Composable
fun CommentItem(
    Comments: Comments,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {

        Column {
            Text(
                text = Comments.userId,
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
                    text = Comments.text,
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
                    text = Comments.text,
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


@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun planBody(homeUiState: HomeViewModel.HomeUiState) {
    when (homeUiState.planList) {
        is Resources.Loading<*> -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }
        is Resources.Success<*> -> {
            LazyVerticalGrid(
                cells = GridCells.Fixed(1),
                contentPadding = PaddingValues(16.dp),
            ) {
                if (homeUiState.planList.data?.size == 0) {
                    item {
                        Text(
                            text = "No content yet, add a new plan",
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
                            text = "Click the + button to add a plan",
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
                        homeUiState.planList.data ?: listOf()
                    ) { ReadingPlan ->
                        PlanItem(
                            ReadingPlan = ReadingPlan,
                        )
                    }
                }

            }
        }
        else -> {
            Text(
                text = homeUiState
                    .bookList.throwable?.localizedMessage ?: "Error desconocido",
                color = Color.Red
            )
        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlanItem(
    ReadingPlan: ReadingPlans,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {

        Column {
            Text(
                text = ReadingPlan.title,
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
                    text = "placeholder",
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
                    text = formatDate(ReadingPlan.startDate),
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


@Composable
fun BookItem(
    Books: Books,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {

        Column {
            Text(
                text = Books.name,
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
                    text = Books.genre,
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
                    text = Books.author,
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



@Composable
fun BookScrollable(homeViewModel: HomeViewModel?, homeUiState: HomeViewModel.HomeUiState) {
    homeViewModel?.getBooks2()
    Row(modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .padding(10.dp)
        .requiredHeight(180.dp),

            horizontalArrangement =  Arrangement.spacedBy(20.dp)
    ) {
        AsyncImage(model = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTRQlcYvc6gYzVS8swAdppDbJqW7o7uY8eIInd_5M5QPlZ7u1FV" ,
                modifier =Modifier.clip(RoundedCornerShape(15.dp)),
                contentDescription = "book1")
        AsyncImage(model = "https://www.elejandria.com/covers/El_banquete-Platon-lg.png" ,
            modifier =Modifier.clip(RoundedCornerShape(15.dp)).fillMaxHeight(),
                contentDescription = "book2")
        AsyncImage(model = "https://images.cdn2.buscalibre.com/fit-in/360x360/01/e0/01e04c600c7b7bfaf5a14c17a640e5af.jpg" ,
            modifier =Modifier.clip(RoundedCornerShape(15.dp)).fillMaxHeight(),
                contentDescription = "book3")
        AsyncImage(model = "https://images.cdn3.buscalibre.com/fit-in/360x360/f5/69/f569d107a767385f1ab3b249d6e78285.jpg" ,
            modifier =Modifier.clip(RoundedCornerShape(15.dp)).fillMaxHeight(),
                contentDescription = "book4")
        AsyncImage(model = "https://static.wikia.nocookie.net/esharrypotter/images/9/9a/Harry_Potter_y_la_Piedra_Filosofal_Portada_Espa%C3%B1ol.PNG/revision/latest?cb=20151020165725" ,
            modifier =Modifier.clip(RoundedCornerShape(15.dp)).fillMaxHeight(),
            contentDescription = "book4")
    }
}

@Composable
fun DrawerContent(
    scope : CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    menu_items: List<Destinations>) {
    Column{
        Image(
            painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Opciones",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(16.dp))
        
        menu_items.forEach{item ->
            DrawerItem(item){
                navController.navigate(item.route){
                    launchSingleTop = true
                    }
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    }
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(15.dp))

}

@Composable
fun DrawerItem(
    item: Destinations,
    onItemClick: (Destinations) -> Unit

) {
    Row (
        modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable { onItemClick(item) }
            ){
        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.title,
            modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.h6
        )
    }
}








@Composable
fun TopBar(
    scope : CoroutineScope,
    scaffoldState: ScaffoldState,
    homeViewModel : HomeViewModel?,
    navToLoginPage: () -> Unit,
){
    TopAppBar(
                navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = {
                homeViewModel?.signOut()
                navToLoginPage.invoke()
            }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                )
            }
        },
        title = {
            Text(text = "Book")
        }
    )
}

fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

//home screen template
//--------------------------------------------------------------------------------
//functions for user posts











@Composable
fun UserCard(Comments: Comments) {
    Row(verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp) ){
        Image(bitmap= ImageBitmap.imageResource(id = R.drawable.pic_not_found),
                modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp),
                contentDescription = "person1"
        )
        Column {
            val uName=Comments.userId
            Text(uName,fontSize = 15.sp)
            Text("",fontSize = 10.sp)
        }
    }
}

@Composable
fun BottomCommentBar(){
    Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp) ){
        Image(bitmap= ImageBitmap.imageResource(id = R.drawable.ic_like_white),
                modifier = Modifier
                        .size(15.dp),
                contentDescription = "like bttn"
        )
            Text("7",fontSize = 10.sp)

        Image(bitmap= ImageBitmap.imageResource(id = R.drawable.ic_comment_white),
                modifier = Modifier
                        .size(15.dp),
                contentDescription = "comment bttn"
        )
        Text("2900",fontSize = 10.sp)

        Image(bitmap= ImageBitmap.imageResource(id = R.drawable.ic_upload_white),
                modifier = Modifier
                        .size(15.dp),
                contentDescription = "upload bttn"
        )
        Text("1000",fontSize = 10.sp)

        Button(onClick = {
            //TO Do on click
        },
                shape = CutCornerShape(10),
                colors = ButtonDefaults.buttonColors(backgroundColor = social_pink),
                modifier = Modifier.padding(horizontal = 40.dp)
        )

        {
            Text(text = "Go to Plan",color = Color.White)
        }
    }
}

@Composable
fun CommentBox(Comments: Comments){
    Column (
            verticalArrangement = Arrangement.spacedBy(15.dp)){
        UserCard(Comments)
        Text(Comments.text,fontSize = 10.sp)
        BottomCommentBar()
    }
}


@Composable
fun CommentScrollable(){
    Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
            .requiredHeight(800.dp)
    ,
    verticalArrangement =  Arrangement.spacedBy(15.dp)
    ) {

        }
    }


//--------------------------------------------------------------------------------




//--------------------------------------------------------------------------------
///end template




@Preview(showSystemUi = true)
@Composable
fun PrevHomeScreen() {
    BooksTheme {
        Home(
            homeViewModel = null,
            navToLoginPage = { },
        )
    }
}









