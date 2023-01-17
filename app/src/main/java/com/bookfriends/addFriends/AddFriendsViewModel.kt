package com.bookfriends.addFriends



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.models.User
import com.bookfriends.network.NetworkServiceAdapter
import com.bookfriends.network.Resources
import com.bookfriends.repository.AuthRepository

import kotlinx.coroutines.launch



class AddFriendsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val networkServiceAdapter: NetworkServiceAdapter = NetworkServiceAdapter
) : ViewModel() {
    var addFriendsUiState by mutableStateOf(AddFriendsUiState())

    val user = authRepository.user()
    val hasUser: Boolean
        get() = authRepository.hasUser()
    private val userId: String
        get() = authRepository.getUserId()


    fun loadUsers(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUsers()
            }
        }else{
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Error(
                throwable = Throwable(message = "The user is not logged in")
            ))
        }
    }

    private fun getUsers() = viewModelScope.launch {
        networkServiceAdapter.getAllUsers().collect {
            addFriendsUiState = addFriendsUiState.copy(UserList = it)
            onOptionSelected(addFriendsUiState.selectedRadioOption)
        }
    }

    fun onOptionSelected (radioOption : String) {
        addFriendsUiState = addFriendsUiState.copy(selectedRadioOption = radioOption)
        if (radioOption == "Last name" && addFriendsUiState.selectedRadioOptionOrder == "Ascending") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(addFriendsUiState.UserList.data?.sortedBy { it.lastName }))
        } else if (radioOption == "Last name" && addFriendsUiState.selectedRadioOptionOrder == "Descending") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(addFriendsUiState.UserList.data?.sortedByDescending { it.lastName }))
        } else if (radioOption == "First name" && addFriendsUiState.selectedRadioOptionOrder == "Ascending") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(addFriendsUiState.UserList.data?.sortedBy { it.firstName }))
        } else if (radioOption == "First name" && addFriendsUiState.selectedRadioOptionOrder == "Descending") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(addFriendsUiState.UserList.data?.sortedByDescending { it.firstName }))
        }
        // Save selected radio option in Shared Preferences
    }

    fun onOptionOrderSelected (radioOption : String) {
        addFriendsUiState = addFriendsUiState.copy(selectedRadioOptionOrder = radioOption)

        if (radioOption == "Ascending" && addFriendsUiState.selectedRadioOption == "Last name") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(
                data = addFriendsUiState?.UserList?.data?.sortedBy { it.lastName }
            ))
        }

        if (radioOption == "Descending" && addFriendsUiState.selectedRadioOption == "Last name") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(
                data = addFriendsUiState?.UserList?.data?.sortedByDescending { it.lastName }
            ))
        }

        if (radioOption == "Ascending" && addFriendsUiState.selectedRadioOption == "First name") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(
                data = addFriendsUiState?.UserList?.data?.sortedBy { it.firstName }
            ))
        }

        if (radioOption == "Descending" && addFriendsUiState.selectedRadioOption == "First name") {
            addFriendsUiState = addFriendsUiState.copy(UserList = Resources.Success(
                data = addFriendsUiState?.UserList?.data?.sortedByDescending { it.firstName }
            ))
        }
    }
}

data class AddFriendsUiState(
    val UserList: Resources<List<User>> = Resources.Loading(),
    val radioOptions : List<String> = listOf("First name, Last name"),
    val selectedRadioOption : String = "First name",
    val radioOptionsOrder : List<String> = listOf("Ascending", "Descending"),
    val selectedRadioOptionOrder : String = "Ascending"
)




