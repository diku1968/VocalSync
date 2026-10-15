package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VocalSyncDarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0F3647),
    onPrimaryContainer = CyanAccent,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1E54),
    onSecondaryContainer = MagentaAccent,
    tertiary = MagentaAccent,
    background = StudioDarkBg,
    onBackground = TextPrimary,
    surface = StudioCardBg,
    onSurface = TextPrimary,
    surfaceVariant = StudioCardBgElevated,
    onSurfaceVariant = TextSecondary,
    outline = StudioCardBorder,
    error = CoralRed,
    onError = Color.White
)

@Composable
fun VocalSyncTheme(
    darkTheme: Boolean = true, // VocalSync is designed as a dark studio console
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VocalSyncDarkColorScheme,
        typography = Typography,
        content = content
    )
}
