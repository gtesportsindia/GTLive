package com.gtesports.gtlive.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GTRedPrimary,
    onPrimary = GTTextPrimary,
    primaryContainer = GTRedDark,
    onPrimaryContainer = GTTextPrimary,
    secondary = GTRedLight,
    onSecondary = GTTextPrimary,
    background = GTBackgroundBlack,
    onBackground = GTTextPrimary,
    surface = GTSurfaceDark,
    onSurface = GTTextPrimary,
    surfaceVariant = GTSurfaceCard,
    onSurfaceVariant = GTTextSecondary,
    outline = GTBorderDark
)

@Composable
fun GTLiveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
