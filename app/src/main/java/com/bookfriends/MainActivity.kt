package com.bookfriends

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bookfriends.addFriends.AddFriendsViewModel
import com.bookfriends.book.BookViewModel
import com.bookfriends.filter.FilterUiState
import com.bookfriends.filter.FilterViewModel
import com.bookfriends.home.HomeViewModel
import com.bookfriends.login.LoginViewModel
import com.bookfriends.profile.ProfileViewModel
import com.bookfriends.ui.theme.BooksTheme
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.notes.NoteDetailViewModel
import com.bookfriends.notes.NoteViewModel
import com.bookfriends.ui.theme.ThemeState
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkServiceAdapter.addPersistence()
        val prefs = this.getSharedPreferences("com.bookfriends.prefs", MODE_PRIVATE);
        val darkModePref = prefs!!.getBoolean("dark_mode", true)
        if (darkModePref != ThemeState.darkModeState.value) {
            ThemeState.darkModeState.value = darkModePref
        }

        setContent {
            MainApp(prefs)
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun MainApp(prefs: SharedPreferences) {
    BooksTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()
        val homeViewModel: HomeViewModel = viewModel()
        val isDark = ThemeState.darkModeState.value
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (currentRoute(navController) !in listOf("Login", "Main", "Signup", "SignIn"))
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Navigation Drawer",
                                )
                            }
                        },
                        actions = {

                            IconButton(onClick = {
                                val editor = prefs.edit()
                                editor.putBoolean("dark_mode", !isDark).apply()
                                ThemeState.darkModeState.value = !isDark
                            }) {
                                Icon(
                                    painter = painterResource(id = if (isDark) R.drawable.ic_baseline_light_mode_24 else R.drawable.ic_baseline_dark_mode_24),
                                    contentDescription = null,
                                )
                            }

                            IconButton(onClick = {
                                homeViewModel?.signOut()
                                navController.navigate("login")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                )
                            }
                        },
                        title = {
                            Text(text = "BookFriends")
                        },
                        backgroundColor = Color(0xFFF62E8E)
                    )
            },
            drawerContent = {
                val menuItems = listOf(
                    Destinations.Home,
                    Destinations.Profile,
                    Destinations.Discussionboard,
                    Destinations.NearbyBookstores,
                    Destinations.Plan,
                    Destinations.Note,
                    Destinations.Filter,
                    Destinations.AddFriends
                )
                Column {
                    Image(
                        painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Opciones",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    menuItems.forEach { item ->
                        DrawerItem(item) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            scope.launch {
                                if (scaffoldState.drawerState.isClosed) {
                                    scaffoldState.drawerState.open()
                                } else {
                                    scaffoldState.drawerState.close()
                                }
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                )
            },
            ) { innerPadding ->
            MainNavHost(navController = navController, modifier = Modifier.padding(innerPadding))

        }
    }
}


@Composable
fun DrawerItem(
    item: Destinations,
    onItemClick: (Destinations) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onItemClick(item) }
    ) {
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


//Scaffold requires innerPadding so remove if you decide not to use scaffold
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val loginViewModel: LoginViewModel = viewModel()
    val bookViewModel: BookViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val noteViewModel : NoteViewModel = viewModel()
    val noteDetailViewModel : NoteDetailViewModel = viewModel()
    val filterViewModel : FilterViewModel = viewModel()
    val addFriendsViewModel : AddFriendsViewModel = viewModel()

    Navigation(
        navController = navController,
        loginViewModel = loginViewModel,
        bookViewModel = bookViewModel,
        homeViewModel = homeViewModel,
        profileViewModel = profileViewModel,
        noteViewModel = noteViewModel,
        noteDetailViewModel = noteDetailViewModel,
        filterViewModel = filterViewModel,
        addFriendsViewModel = addFriendsViewModel
    )
}




