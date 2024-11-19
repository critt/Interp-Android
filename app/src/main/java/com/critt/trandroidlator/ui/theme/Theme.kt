package com.critt.trandroidlator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color.Gray,
    secondary = Color.DarkGray,
    background = Color.Black,
    surface = Color.DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.Green,
    onBackground = Color.DarkGray,
    onSurface = Color.LightGray,
    primaryContainer = Color.DarkGray,
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Gray,
    secondary = Color.LightGray,
    background = Color.White,
    surface = Color.LightGray,
    onPrimary = Color.White,
    onSecondary = Color.Green,
    onBackground = Color.DarkGray,
    onSurface = Color.DarkGray,
    primaryContainer = Color.LightGray
)

@Composable
fun TrandroidlatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}