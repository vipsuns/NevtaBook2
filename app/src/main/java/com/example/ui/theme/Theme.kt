package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NevtaPrimary,
    onPrimary = Color.White,
    primaryContainer = NevtaPeach,
    onPrimaryContainer = NevtaDeepOrange,
    secondary = NevtaPrimaryGold,
    onSecondary = NevtaPrimaryText,
    secondaryContainer = NevtaSand,
    onSecondaryContainer = NevtaSecondaryText,
    tertiary = NevtaGold,
    onTertiary = Color.White,
    background = NevtaBackground,
    onBackground = NevtaPrimaryText,
    surface = NevtaSurface,
    onSurface = NevtaPrimaryText,
    surfaceVariant = NevtaSand,
    onSurfaceVariant = NevtaSecondaryText,
    outline = NevtaBorder,
    outlineVariant = Color(0xFFE2E4E8),
    error = NevtaDanger,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = NevtaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3F210F),
    onPrimaryContainer = NevtaPrimaryGold,
    secondary = NevtaPrimaryGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2C2216),
    onSecondaryContainer = NevtaPeach,
    tertiary = NevtaGold,
    onTertiary = Color.Black,
    background = NevtaDarkBackground,
    onBackground = NevtaDarkTextPrimary,
    surface = NevtaDarkSurface,
    onSurface = NevtaDarkTextPrimary,
    surfaceVariant = NevtaDarkSurfaceElevated,
    onSurfaceVariant = NevtaDarkTextSecondary,
    outline = NevtaDarkBorder,
    outlineVariant = Color(0xFF3B414E),
    error = NevtaDanger,
    onError = Color.White
)

@Composable
fun NevtaBookTheme(
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

// Keep alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NevtaBookTheme(darkTheme = darkTheme, content = content)
}

