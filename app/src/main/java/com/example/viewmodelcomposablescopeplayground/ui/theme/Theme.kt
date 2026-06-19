package com.example.viewmodelcomposablescopeplayground.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = WcGreen40,
    onPrimary        = Color.White,
    primaryContainer = WcGreen95,
    onPrimaryContainer = WcGreen10,

    secondary          = WcGold40,
    onSecondary        = Color.White,
    secondaryContainer = WcGold95,
    onSecondaryContainer = WcGold10,

    error          = WcRed40,
    onError        = Color.White,
    errorContainer = WcRed90,
    onErrorContainer = WcRed40,

    background = WcNeutral99,
    onBackground = WcNeutral10,
    surface    = WcNeutral99,
    onSurface  = WcNeutral10,
)

private val DarkColorScheme = darkColorScheme(
    primary          = WcGreen80,
    onPrimary        = WcGreen10,
    primaryContainer = WcGreen20,
    onPrimaryContainer = WcGreen90,

    secondary          = WcGold80,
    onSecondary        = WcGold10,
    secondaryContainer = WcGold30,
    onSecondaryContainer = WcGold90,

    error          = WcRed80,
    onError        = WcRed40,
    errorContainer = WcRed40,
    onErrorContainer = WcRed90,
)

@Composable
fun ViewModelComposableScopePlaygroundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
