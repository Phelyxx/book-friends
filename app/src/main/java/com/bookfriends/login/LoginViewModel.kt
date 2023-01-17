package com.bookfriends.login

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookfriends.repository.AuthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    val currentUser = authRepository.user()

    val hasUser: Boolean
        get() = authRepository.hasUser()

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUserNameChange(userName: String) {
        loginUiState = loginUiState.copy(userName = userName)
    }

    fun onPasswordNameChange(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    fun onUserNameChangeSignup(userName: String) {
        loginUiState = loginUiState.copy(userNameSignUp = userName)
    }

    fun onFirstNameChangeSignup(firstName: String) {
        loginUiState = loginUiState.copy(firstNameSignUp = firstName)
    }

    fun onLastNameChangeSignup(lastName: String) {
        loginUiState = loginUiState.copy(lastNameSignUp = lastName)
    }

    fun onPasswordChangeSignup(password: String) {
        loginUiState = loginUiState.copy(passwordSignUp = password)
    }

    fun onBirthDateChangeSignup(birthDate: String) {
        loginUiState = loginUiState.copy(birthDateSignUp = birthDate)
    }

    fun onConfirmPasswordChange(password: String) {
        loginUiState = loginUiState.copy(confirmPasswordSignUp = password)
    }

    private fun validateLoginForm() =
        loginUiState.userName.isNotBlank() &&
                loginUiState.password.isNotBlank()

    private fun validateLoginMaxPasswordLength() =
        loginUiState.password.length <= 20

    private fun validateSignupForm() =
        loginUiState.userNameSignUp.isNotBlank() &&
                loginUiState.firstNameSignUp.isNotBlank() &&
                loginUiState.lastNameSignUp.isNotBlank() &&
                loginUiState.passwordSignUp.isNotBlank() &&
                loginUiState.confirmPasswordSignUp.isNotBlank() &&
                loginUiState.birthDateSignUp.isNotBlank()

    private fun validateMaxEmailLength() =
        loginUiState.userNameSignUp.length <= 320

    private fun validateSignUpMaxPasswordLength() =
        loginUiState.passwordSignUp.length <= 20 &&
                loginUiState.confirmPasswordSignUp.length <= 20

    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateSignupForm()) {
                throw IllegalArgumentException("Fields cannot be empty")
            }

            if (!validateMaxEmailLength()) {
                throw IllegalArgumentException("The email cannot be longer than 320 characters")
            }

            if (!validateSignUpMaxPasswordLength()) {
                throw IllegalArgumentException("Password cannot be longer than 20 characters")
            }

            // Validate if users date of birth is at least 8 years old
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(loginUiState.birthDateSignUp)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val currentDate = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
            if (currentYear - year < 8) {
                throw IllegalArgumentException("You must be at least 8 years old to create an account")
            } else if (currentYear - year == 8) {
                if (currentMonth < month) {
                    throw IllegalArgumentException("You must be at least 8 years old to create an account")
                } else if (currentMonth == month) {
                    if (currentDay < day) {
                        throw IllegalArgumentException("You must be at least 8 years old to create an account")
                    }
                }
            }




            loginUiState = loginUiState.copy(isLoading = true)
            if (loginUiState.passwordSignUp !=
                loginUiState.confirmPasswordSignUp
            ) {
                throw IllegalArgumentException(
                    "Passwords do not match"
                )
            }
            loginUiState = loginUiState.copy(signUpError = null)
            authRepository.createUser(
                loginUiState.userNameSignUp,
                loginUiState.passwordSignUp,
                loginUiState.firstNameSignUp,
                loginUiState.lastNameSignUp,
                loginUiState.birthDateSignUp
            ) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "Successful login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }

            }


        } catch (e: Exception) {
            loginUiState = loginUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }


    }

    fun loginUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateLoginForm()) {
                throw IllegalArgumentException("Email address or password cannot be empty")
            }

            if (!validateLoginMaxPasswordLength()) {
                throw IllegalArgumentException("Password cannot be longer than 20 characters")
            }

            if (!validateMaxEmailLength()) {
                throw IllegalArgumentException("The email cannot be longer than 320 characters")
            }

            loginUiState = loginUiState.copy(isLoading = true)
            loginUiState = loginUiState.copy(loginError = null)
            authRepository.login(
                loginUiState.userName,
                loginUiState.password
            ) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "Successful login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }

            }


        } catch (e: Exception) {
            loginUiState = loginUiState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }


    }

    private var dateFormat = "yyyy-MM-dd"
    fun showDatePickerDialog(context: Context) {
        val calendar = getCalendar()
        DatePickerDialog(
            context, { _, year, month, day ->
                loginUiState = loginUiState.copy(birthDateSignUp = getPickedDateAsString(year, month, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    private fun getCalendar(): Calendar {
        return if (loginUiState.birthDateSignUp.isEmpty())
            Calendar.getInstance()
        else
            getLastPickedDateCalendar()
    }
    private fun getLastPickedDateCalendar(): Calendar {
        val dateFormat = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(loginUiState.birthDateSignUp)
        return calendar
    }
    private fun getPickedDateAsString(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat(dateFormat)
        return dateFormat.format(calendar.time)
    }


}

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val firstNameSignUp: String = "",
    val lastNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val birthDateSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
)

















