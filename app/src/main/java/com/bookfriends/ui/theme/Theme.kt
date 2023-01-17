package com.bookfriends.ui.theme

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.material.internal.ThemeEnforcement

private val DarkColorPalette = darkColors(
    primary = SocialPink,
    primaryVariant = Grey,
    secondary = LightGrey
)

private val LightColorPalette = lightColors(
    primary = SocialPink,
    primaryVariant = Grey,
    secondary = LightGrey

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

object ThemeState { 
    var darkModeState : MutableState<Boolean> = mutableStateOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
}

@Composable
fun BooksTheme(
    darkTheme: Boolean = ThemeState.darkModeState.value,
    content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}