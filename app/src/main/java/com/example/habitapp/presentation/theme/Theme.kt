
package com.example.habitapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.habitapp.ui.theme.AppBackground
import com.example.habitapp.ui.theme.AppOutline
import com.example.habitapp.ui.theme.AppSurface
import com.example.habitapp.ui.theme.AppSurfaceVariant
import com.example.habitapp.ui.theme.GreenOnPrimary
import com.example.habitapp.ui.theme.GreenOnPrimaryContainer
import com.example.habitapp.ui.theme.GreenPrimary
import com.example.habitapp.ui.theme.GreenPrimaryContainer
import com.example.habitapp.ui.theme.TopBarBackground
import com.example.habitapp.ui.theme.TextPrimary
import com.example.habitapp.ui.theme.TextSecondary

private val HabitLightColorScheme = lightColorScheme(
    // Primary — Green
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    // Secondary — Soft Green
    secondary = GreenPrimary,
    onSecondary = GreenOnPrimary,
    secondaryContainer = GreenPrimaryContainer,
    onSecondaryContainer = GreenOnPrimaryContainer,

    // Tertiary — Soft Blue
    tertiary = androidx.compose.ui.graphics.Color(0xFF7EA6C6),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF18364A),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFDCEAF7),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF254A63),

    // Background
    background = androidx.compose.ui.graphics.Color.White,
    onBackground = TextPrimary,

    // Surface
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    // Outline
    outline = AppOutline,

    // Error
    error = androidx.compose.ui.graphics.Color(0xFFD7655D),
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFBE5E3),
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
