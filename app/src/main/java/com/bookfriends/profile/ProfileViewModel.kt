package com.bookfriends.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.bookfriends.domain.model.UploadImageResponse
//import com.bookfriends.domain.repositories.ProfileImageRepository
import com.bookfriends.models.User
import com.bookfriends.repository.AuthRepository
import com.bookfriends.repository.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    //private val repo: ProfileImageRepository
) : ViewModel() {
    /*
    // Image
    var addImageToStorageResponse by mutableStateOf<UploadImageResponse<Uri>>(
        UploadImageResponse.Success(
            null
        )
    )
        private set
    var addImageToDatabaseResponse by mutableStateOf<UploadImageResponse<Boolean>>(
        UploadImageResponse.Success(
            null
        ))
        private set
    var getImageFromDatabaseResponse by mutableStateOf<UploadImageResponse<String>>(
        UploadImageResponse.Success(
            null
        ))
        private set

    fun addImageToStorage(imageUri: Uri) = viewModelScope.launch {
        addImageToStorageResponse = UploadImageResponse.Loading
        addImageToStorageResponse = repo.addImageToFirebaseStorage(imageUri)
    }

    fun addImageToDatabase(downloadUrl: Uri) = viewModelScope.launch {
        addImageToDatabaseResponse = UploadImageResponse.Loading
        addImageToDatabaseResponse = repo.addImageUrlToFirestore(downloadUrl)
    }

    fun getImageFromDatabase() = viewModelScope.launch {
        getImageFromDatabaseResponse = UploadImageResponse.Loading
        getImageFromDatabaseResponse = repo.getImageUrlFromFirestore()
    }
*/
    //

    var profileUiState by mutableStateOf(ProfileUiState())

    private val hasUser: Boolean
        get() = authRepository.hasUser()

    private val userEmail: String
        get() = authRepository.getUserEmail()

    fun onFirstNameChange(firstName: String) {
        profileUiState = profileUiState.copy(firstName = firstName)
    }

    fun onLastNameChange(lastName: String) {
        profileUiState = profileUiState.copy(lastName = lastName)
    }

    fun onImageChange(imageUrl: String) {
        profileUiState = profileUiState.copy(imageUrl = imageUrl)
    }

    suspend fun loadUserProfile(pUserEmail: String) = viewModelScope.launch {
        if (hasUser) {
            if (pUserEmail != "") {
                userRepository.getProfile(
                    userEmail = pUserEmail,
                    onError = {},
                ) {
                    profileUiState = profileUiState.copy(selectedUser = it)
                    profileUiState.selectedUser?.let { it1 -> setEditFields(it1) }
                }
            } else {
                userRepository.getProfile(
                    userEmail = userEmail,
                    onError = {},
                ) {
                    profileUiState = profileUiState.copy(selectedUser = it)
                    profileUiState.selectedUser?.let { it1 ->
                        setEditFields(it1)
                    }
                }
            }
        }
    }

    private fun setEditFields(user: User) {
        profileUiState = profileUiState.copy(
            firstName = user.firstName.toString(),
            lastName = user.lastName.toString(),
            imageUrl = user.imageUrl.toString(),
            birthdate = user.birthDate?.let { formatDate(it) }
        )
    }

    fun updateProfile(
        pUserEmail: String
    ) {
        if (pUserEmail != "") {
            if (profileUiState.firstName.isBlank() || profileUiState.lastName.isBlank() || profileUiState.imageUrl.isBlank()) {
                profileUiState = profileUiState.copy(profileBlankStatus = true)
            } else {
                userRepository.updateUser(
                    firstName = profileUiState.firstName,
                    lastName = profileUiState.lastName,
                    userEmail = pUserEmail,
                    imageUrl = profileUiState.imageUrl,
                ) {
                    profileUiState = profileUiState.copy(updateUserStatus = it)
                }
            }
        } else {
            if (profileUiState.firstName.isBlank() || profileUiState.lastName.isBlank() || profileUiState.imageUrl.isBlank()) {
                profileUiState = profileUiState.copy(profileBlankStatus = true)
            } else {
                userRepository.updateUser(
                    firstName = profileUiState.firstName,
                    lastName = profileUiState.lastName,
                    userEmail = userEmail,
                    imageUrl = profileUiState.imageUrl,
                ) {
                    profileUiState = profileUiState.copy(updateUserStatus = it)
                }
            }
        }



    }

    fun resetProfileBlankStatus() {
        profileUiState = profileUiState.copy(
            profileBlankStatus = false,
        )
    }

    fun resetState() {
        profileUiState = ProfileUiState()
    }
}

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val imageUrl: String = "",
    val birthdate: String? = null,
    val friendsList: MutableList<User>? = null,
    val selectedUser: User? = null,
    val updateUserStatus: Boolean = false,
    val profileBlankStatus: Boolean = false
)

private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}