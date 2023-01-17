package com.bookfriends.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.models.*
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.network.Resources
import com.bookfriends.repository.AuthRepository
import com.bookfriends.repository.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val bookRepository: BookRepository = BookRepository(),
    private val networkServiceAdapter: NetworkServiceAdapter = NetworkServiceAdapter
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())

    data class HomeUiState(
        val bookList: Resources<List<Books>> = Resources.Loading(),
        val bookDeletedStatus: Boolean = false,
        val planList: Resources<List<ReadingPlans>> = Resources.Loading(),
        val userList: Resources<List<User>> = Resources.Loading(),
        val discussionList: Resources<List<Discussions>> = Resources.Loading(),
        val commentList: Resources<List<Comments>> = Resources.Loading(),
    ) {
    }


    val user = authRepository.user()
    val hasUser: Boolean
        get() = authRepository.hasUser()
    private val userId: String
        get() = authRepository.getUserId()


    fun signOut() = authRepository.signOut()






    private fun getUserPlans(userId:String) = viewModelScope.launch {
        networkServiceAdapter.getUserPlans(userId).collect {
            homeUiState = homeUiState.copy(planList = it)
        }
    }

    private fun getAllPlans() = viewModelScope.launch {
        networkServiceAdapter.getAllPlans().collect {
            homeUiState = homeUiState.copy(planList = it)
        }
    }

    fun loadPlans2(){
        if (hasUser){
            if (userId.isNotBlank()){
                getAllPlans()
            }
        }else{
            homeUiState = homeUiState.copy(planList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    fun loadPlans(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserPlans(userId)
            }
        }else{
            homeUiState = homeUiState.copy(planList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    fun loadBook() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getBookByPlan(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                bookList = Resources.Error(
                    throwable = Throwable(message = "The user is not logged in")
                )
            )
        }
    }


    private fun getBookByPlan(userId: String) = viewModelScope.launch {
        networkServiceAdapter.getBookByPlan(userId).collect {
            homeUiState = homeUiState


        }


    }


    fun getBooks2(){
        if (hasUser){
            if (userId.isNotBlank()){
                loadBooks()
            }
        }else{
            homeUiState = homeUiState.copy(bookList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }


    fun loadBooks() = viewModelScope.launch {
        networkServiceAdapter.getAllBooks().collect {
            homeUiState = homeUiState.copy(bookList = it)
        }
    }

    fun getDiscussions(){
        if (hasUser){
            if (userId.isNotBlank()){
                loadDiscussions()
            }
        }else{
            homeUiState = homeUiState.copy(userList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    fun loadDiscussions() = viewModelScope.launch {
        networkServiceAdapter.getAllDiscussions().collect {
            homeUiState = homeUiState.copy(discussionList = it)
        }
    }

    fun getUsers(){
        if (hasUser){
            if (userId.isNotBlank()){
                loadUsers()
            }
        }else{
            homeUiState = homeUiState.copy(discussionList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    fun loadAll(){
        if (hasUser){
            if (userId.isNotBlank()){
                getAllPlans()
                loadBooks()
                loadComments()
                loadUsers()
            }
        }else{
            homeUiState = homeUiState.copy(planList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }


    fun loadUsers()  = viewModelScope.launch {
        networkServiceAdapter.getAllUsers().collect {
            homeUiState = homeUiState.copy(userList = it)
        }
    }
    fun getComments(){
        if (hasUser){
            if (userId.isNotBlank()){
                loadComments()
            }
        }else{
            homeUiState = homeUiState.copy(commentList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }


    fun loadComments()  = viewModelScope.launch {
        networkServiceAdapter.getAllComments().collect {
            homeUiState = homeUiState.copy(commentList = it)
        }
    }

   fun getUserbyId(){

   }
}















