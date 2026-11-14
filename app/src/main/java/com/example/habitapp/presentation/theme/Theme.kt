package com.example.habitapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.habitapp.ui.theme.*

private val HabitLightColorScheme = lightColorScheme(

    // Primary - Fresh Green
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    // Secondary - Sky Blue
    secondary = BlueSecondary,
    onSecondary = BlueOnSecondary,
    secondaryContainer = BlueSecondaryContainer,
    onSecondaryContainer = BlueOnSecondaryContainer,

    // Tertiary - Sunshine Yellow
    tertiary = YellowTertiary,
    onTertiary = YellowOnTertiary,
    tertiaryContainer = YellowTertiaryContainer,
    onTertiaryContainer = YellowOnTertiaryContainer,

    // Background
    background = AppBackground,
    onBackground = TextPrimary,

    // Surface
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    // Outline
    outline = AppOutline,

    // Error
    error = AppError,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = StatusFailedContainer,
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF6F2520)
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