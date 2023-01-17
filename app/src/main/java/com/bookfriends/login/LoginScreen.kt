package com.bookfriends.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bookfriends.ui.theme.BooksTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignUpPage: () -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(8.dp)
        )

        if (isError) {
            Text(
                text = loginUiState?.loginError ?: "Check your internet connection",
                color = Color.Red,
            )
        }

        val maxChar = 321

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.userName ?: "",
            onValueChange = {
                loginViewModel?.onUserNameChange(it)
                if (it.length < maxChar) {
                    loginViewModel?.onUserNameChange(it)
                } else {
                    Toast.makeText(context, "Max character is $maxChar", Toast.LENGTH_SHORT).show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Email")
            },
            isError = isError,

            )

        val maxCharPassword = 20

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.password ?: "",
            onValueChange = {
                if (it.length < maxCharPassword) {
                    loginViewModel?.onPasswordNameChange(it)
                } else {
                    Toast.makeText(context, "Max character is $maxCharPassword", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(
            onClick = { loginViewModel?.loginUser(context) },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            ),
        ) {
            Text(text = "Log In")
        }
        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = "Don't have an account? ")
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { onNavToSignUpPage.invoke() }) {
                Text(text = "Create account")
            }
        }
        if (loginUiState?.isLoading == true) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginViewModel?.hasUser) {
            if (loginViewModel?.hasUser == true) {
                onNavToHomePage.invoke()
            }
        }
    }
}

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToLoginPage: () -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.signUpError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )

        if (isError) {
            Text(
                text = loginUiState?.signUpError ?: "Error desconocido",
                color = Color.Red,
            )
        }

        val maxChar = 20

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.firstNameSignUp ?: "",
            onValueChange = {
                if (it.length <= maxChar) {
                    loginViewModel?.onFirstNameChangeSignup(it)
                } else {
                    Toast.makeText(context, "Max character is $maxChar", Toast.LENGTH_SHORT).show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "First name")
            },
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.lastNameSignUp ?: "",
            onValueChange = {
                if (it.length <= maxChar) {
                    loginViewModel?.onLastNameChangeSignup(it)
                } else {
                    Toast.makeText(context, "Max character is $maxChar", Toast.LENGTH_SHORT).show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Last name")
            },
            isError = isError
        )

        val maxCharEmail = 321

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.userNameSignUp ?: "",
            onValueChange = {
                if (it.length <= maxCharEmail) {
                    loginViewModel?.onUserNameChangeSignup(it)
                } else {
                    Toast.makeText(context, "Max character is $maxCharEmail", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Email")
            },
            isError = isError
        )

        val maxCharPassword = 20

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.passwordSignUp ?: "",
            onValueChange = {
                if (it.length <= maxCharPassword) {
                    loginViewModel?.onPasswordChangeSignup(it)
                } else {
                    Toast.makeText(context, "Max character is $maxCharPassword", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = loginUiState?.confirmPasswordSignUp ?: "",
            onValueChange = {
                if (it.length <= maxCharPassword) {
                    loginViewModel?.onConfirmPasswordChange(it)
                } else {
                    Toast.makeText(context, "Max character is $maxCharPassword", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Confirm password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        val context = LocalContext.current

        val maxDateChar = 10

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    loginViewModel?.showDatePickerDialog(context)
                },
            value = loginUiState?.birthDateSignUp ?: "",
            onValueChange = {
                if (it.length <= maxDateChar) {
                    loginViewModel?.onBirthDateChangeSignup(it)
                } else {
                    Toast.makeText(context, "Max character is $maxDateChar", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Birth date")
            },
            isError = isError
        )




        Button(
            onClick = { loginViewModel?.createUser(context) },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            )
        ) {
            Text(text = "Sign Up")
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = "Already have an account?")
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { onNavToLoginPage.invoke() }) {
                Text(text = "Log In")
            }

        }

        if (loginUiState?.isLoading == true) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginViewModel?.hasUser) {
            if (loginViewModel?.hasUser == true) {
                onNavToHomePage.invoke()
            }
        }


    }


}

@Preview(showSystemUi = true)
@Composable
fun PrevLoginScreen() {
    BooksTheme {
        LoginScreen(onNavToHomePage = { /*TODO*/ }) {

        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PrevSignUpScreen() {
    BooksTheme {
        SignUpScreen(onNavToHomePage = { /*TODO*/ }) {

        }
    }
}
















