

package com.bookfriends

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bookfriends.addFriends.AddFriendsScreen
import com.bookfriends.addFriends.AddFriendsViewModel
import com.bookfriends.book.BookScreen
import com.bookfriends.book.BookViewModel
import com.bookfriends.discussionboard.DiscussionBoardScreen
import com.bookfriends.discussionboard.DiscussionBoardViewModel
import com.bookfriends.filter.FilterScreen
import com.bookfriends.filter.FilterViewModel
import com.bookfriends.home.Home
import com.bookfriends.home.HomeViewModel
import com.bookfriends.login.LoginScreen
import com.bookfriends.login.LoginViewModel
import com.bookfriends.login.SignUpScreen
import com.bookfriends.nearbybookstores.NearbyBookstoresScreen
import com.bookfriends.nearbybookstores.NearbyBookstoresViewModel
import com.bookfriends.notes.NoteDetailScreen
import com.bookfriends.notes.NoteDetailViewModel
import com.bookfriends.notes.NoteScreen
import com.bookfriends.notes.NoteViewModel
import com.bookfriends.plan.PlanScreen
import com.bookfriends.profile.ProfileScreen
import com.bookfriends.profile.ProfileViewModel
import com.bookfriends.plan.PlanViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

enum class LoginRoutes {
    Signup,
    SignIn
}

enum class HomeRoutes {
    Home,
    Detail
}

enum class NoteRoutes {
    Note,
    Detail
}

enum class NavBarRoutes {
    Home,
    Profile,
    NearbyBookstores,
    DiscussionBoard,
    Plan,
    Note,
    Filter,
    AddFriends
}

enum class NestedRoutes {
    Main,
    Login,
    NoteDetail
}


@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    bookViewModel: BookViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    noteViewModel : NoteViewModel,
    noteDetailViewModel: NoteDetailViewModel,
    filterViewModel: FilterViewModel,
    addFriendsViewModel: AddFriendsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Main.name
    ) {
        authGraph(
            navController,
            profileViewModel,
            loginViewModel,
            filterViewModel,
            addFriendsViewModel
        )
        homeGraph(
            navController = navController,
            bookViewModel,
            homeViewModel,
        )
        noteGraph(
            navController = navController,
            noteViewModel = noteViewModel,
            noteDetailViewModel = noteDetailViewModel,
        )
    }


}

@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    loginViewModel: LoginViewModel,
    filterViewModel: FilterViewModel,
    addFriendsViewModel: AddFriendsViewModel
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.Signup.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    popUpTo(LoginRoutes.Signup.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }

        composable(route = NavBarRoutes.Profile.name) {
            ProfileScreen(profileViewModel = profileViewModel,
                userEmail = "")
        }

        composable(route = NavBarRoutes.NearbyBookstores.name) {
            NearbyBookstoresScreen()
        }

        composable(route = NavBarRoutes.DiscussionBoard.name) {
            DiscussionBoardScreen()
        }

        composable(route = NavBarRoutes.Plan.name) {
            PlanScreen()
        }

        composable(route = NavBarRoutes.Filter.name) {
            FilterScreen(
                filterViewModel = filterViewModel
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }

        }

        composable(route = NavBarRoutes.AddFriends.name) {
            AddFriendsScreen(
                addFriendsViewModel = addFriendsViewModel
            )
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    bookViewModel: BookViewModel,
    homeViewModel: HomeViewModel,
){
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name,
    ){
        composable(HomeRoutes.Home.name){
            Home(
                homeViewModel = homeViewModel,
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }

            }
        }

        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){ entry ->

            BookScreen(
                bookViewModel = bookViewModel,
                bookId = entry.arguments?.getString("id") as String,
            ) {
                navController.navigate(HomeRoutes.Home.name)
            }
        }
    }
}

fun NavGraphBuilder.noteGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    noteDetailViewModel: NoteDetailViewModel
){
    navigation(
        startDestination = NoteRoutes.Note.name,
        route = NestedRoutes.NoteDetail.name,
    ){
        composable(NoteRoutes.Note.name){
            NoteScreen(
                noteViewModel = noteViewModel,
                onNoteClick = { noteId ->
                    navController.navigate(
                        NoteRoutes.Detail.name + "?id=$noteId"
                    ){
                        launchSingleTop = true
                    }
                },
                navToDetailPage = {
                    navController.navigate(NoteRoutes.Detail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }

            }
        }

        composable(
            route = NoteRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){ entry ->

            NoteDetailScreen(
                noteDetailViewModel = noteDetailViewModel,
                noteId = entry.arguments?.getString("id") as String,
            ) {
                navController.navigate(NoteRoutes.Note.name)
            }
        }
    }
}