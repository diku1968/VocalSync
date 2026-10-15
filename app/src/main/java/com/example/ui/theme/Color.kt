package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val StudioDarkBg = Color(0xFF0D1117)
val StudioDarkBgVariant = Color(0xFF121824)
val StudioCardBg = Color(0xFF1A2232)
val StudioCardBgElevated = Color(0xFF1E293B)
val StudioCardBorder = Color(0xFF26334D)

val CyanAccent = Color(0xFF00E5FF)
val TealAccent = Color(0xFF00B4D8)
val PurpleAccent = Color(0xFF8A2BE2)
val MagentaAccent = Color(0xFFD946EF)
val CoralRed = Color(0xFFFF3B5C)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val MeterGreen = Color(0xFF10B981)
val MeterYellow = Color(0xFFF59E0B)
val MeterRed = Color(0xFFEF4444)
val PresetTagBg = Color(0xFF0C2B3A)
val PresetTagText = Color(0xFF00E5FF)

val CyanPurpleGradient = Brush.horizontalGradient(
    colors = listOf(CyanAccent, PurpleAccent)
)

val CyanTealGradient = Brush.horizontalGradient(
    colors = listOf(CyanAccent, TealAccent)
)

val PurpleMagentaGradient = Brush.horizontalGradient(
    colors = listOf(PurpleAccent, MagentaAccent)
)

val DarkButtonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF222B3D), Color(0xFF1A2232))
)
