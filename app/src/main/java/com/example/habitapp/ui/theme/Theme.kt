package com.example.habitapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HabitLightColorScheme = lightColorScheme(

    // Primary — Green
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,

    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    // Secondary — Soft Green
    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,

    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,

    // Tertiary — Soft Blue
    tertiary = BlueAccent,
    onTertiary = BlueOnAccent,

    tertiaryContainer = BlueContainer,
    onTertiaryContainer = BlueOnContainer,

    // Background
    background = AppBackground,
    onBackground = AppOnBackground,

    // Surface
    surface = AppSurface,
    onSurface = AppOnSurface,

    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppOnSurfaceVariant,

    // Outline
    outline = AppOutline,

    // Error
    error = AppError,
    onError = AppOnError,

    errorContainer = AppErrorContainer,
    onErrorContainer = AppOnErrorContainer
)

@Composable
fun HabitAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HabitLightColorScheme,
        typography = HabitTypography,
        content = content
    )
}