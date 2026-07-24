package com.example.motobook.presentation.theme

import androidx.compose.ui.graphics.Color

// Background & Glass Surface Colors for Light Themes
val FrostBackground = Color(0xFFE0F2FE) // Ice sky blue light background
val PearlBackground = Color(0xFFFBF8F3) // Warm pearl light background

val GlassSurface = Color(0xFFFFFFFF).copy(alpha = 0.85f)
val GlassBorder = Color(0x260284C7)
val GlassShimmer = Color(0x1FFFFFFF)

// Primary Accents
val FrostPrimary = Color(0xFF0284C7) // Sky Blue
val FrostSecondary = Color(0xFF6366F1) // Indigo

val PearlPrimary = Color(0xFF0D9488) // Pearl Teal
val PearlSecondary = Color(0xFFD97706) // Warm Gold

// Legacy Color Aliases
val CyanPrimary = FrostPrimary
val CyanLight = Color(0xFF38BDF8)
val PurplePrimary = FrostSecondary

// Success, Warning, Error
val EmeraldPrimary = Color(0xFF059669)
val AmberPrimary = Color(0xFFD97706)
val CoralPrimary = Color(0xFFDC2626)

// Text Colors (Optimized for High-Contrast Light Layouts)
val TextPrimary = Color(0xFF0F172A) // Dark slate primary text
val TextSecondary = Color(0xFF334155) // Slate secondary text
val TextMuted = Color(0xFF64748B) // Slate muted label text
val TextOnAccent = Color(0xFFFFFFFF) // White text on buttons

// Theme Colors Model
data class ThemePalette(
    val name: String,
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val isLight: Boolean = true
)

object MotoThemes {
    val FrostLight = ThemePalette(
        name = "FROST_LIGHT",
        background = Color(0xFFE0F2FE),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF0284C7),
        secondary = Color(0xFF6366F1),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF334155),
        isLight = true
    )

    val PearlGlass = ThemePalette(
        name = "PEARL_GLASS",
        background = Color(0xFFFBF8F3),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF0D9488),
        secondary = Color(0xFFD97706),
        textPrimary = Color(0xFF1C1917),
        textSecondary = Color(0xFF44403C),
        isLight = true
    )

    fun getThemeByName(name: String): ThemePalette = when (name.uppercase()) {
        "PEARL_GLASS" -> PearlGlass
        else -> FrostLight
    }
}

