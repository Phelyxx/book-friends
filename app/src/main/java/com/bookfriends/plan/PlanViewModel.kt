package com.bookfriends.plan

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.repository.AuthRepository
import kotlinx.coroutines.launch

class PlanViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    val currentUser = authRepository.user()

    val hasUser: Boolean
        get() = authRepository.hasUser()

    var planUiState by mutableStateOf(PlanUiState())
        private set

    fun onUserNameChange(userName: String) {
        planUiState = planUiState.copy(userName = userName)
    }

    fun onPasswordNameChange(password: String) {
        planUiState = planUiState.copy(password = password)
    }

    fun onUserNameChangeSignup(userName: String) {
        planUiState = planUiState.copy(userNameSignUp = userName)
    }

    fun onPasswordChangeSignup(password: String) {
        planUiState = planUiState.copy(passwordSignUp = password)
    }

    fun onConfirmPasswordChange(password: String) {
        planUiState = planUiState.copy(confirmPasswordSignUp = password)
    }

    private fun validatePlanForm() =
        planUiState.userName.isNotBlank() &&
                planUiState.password.isNotBlank()

    private fun validateSignupForm() =
        planUiState.userNameSignUp.isNotBlank() &&
                planUiState.passwordSignUp.isNotBlank() &&
                planUiState.confirmPasswordSignUp.isNotBlank()




    fun planUser(context: Context) = viewModelScope.launch {
        try {
            if (!validatePlanForm()) {
                throw IllegalArgumentException("El email o la contraseña no pueden ser vacíos")
            }
            planUiState = planUiState.copy(isLoading = true)
            planUiState = planUiState.copy(planError = null)



        } catch (e: Exception) {
            planUiState = planUiState.copy(planError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            planUiState = planUiState.copy(isLoading = false)
        }


    }


}

data class PlanUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessPlan: Boolean = false,
    val signUpError: String? = null,
    val planError: String? = null,
)




















