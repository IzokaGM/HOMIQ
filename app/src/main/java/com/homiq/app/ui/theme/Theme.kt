package com.homiq.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = HomiqGreen,
    onPrimary = HomiqSurface,
    primaryContainer = HomiqGreenSoft,
    onPrimaryContainer = HomiqGreenDark,
    secondary = HomiqGreenDark,
    onSecondary = HomiqSurface,
    secondaryContainer = HomiqSurfaceMuted,
    onSecondaryContainer = HomiqInk,
    background = HomiqCanvas,
    onBackground = HomiqInk,
    surface = HomiqSurface,
    onSurface = HomiqInk,
    surfaceVariant = HomiqSurfaceMuted,
    onSurfaceVariant = HomiqInkSoft,
    outline = HomiqOutline,
)

private val DarkColors = darkColorScheme(
    primary = HomiqMint,
    onPrimary = HomiqGreenDark,
    primaryContainer = HomiqGreenDark,
    onPrimaryContainer = HomiqGreenSoft,
    secondary = HomiqMint,
    onSecondary = HomiqGreenDark,
    secondaryContainer = HomiqDarkSurfaceMuted,
    onSecondaryContainer = HomiqDarkOnSurface,
    background = HomiqDarkCanvas,
    onBackground = HomiqDarkOnSurface,
    surface = HomiqDarkSurface,
    onSurface = HomiqDarkOnSurface,
    surfaceVariant = HomiqDarkSurfaceMuted,
    onSurfaceVariant = HomiqDarkOnSurface,
    outline = HomiqDarkOutline,
)

@Composable
fun HomiqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = HomiqTypography,
        content = content,
    )
}
