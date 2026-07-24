package com.example.motobook.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalThemePalette = compositionLocalOf { MotoThemes.FrostLight }
val LocalGlassIntensity = compositionLocalOf { 0.85f }
val LocalCardRadius = compositionLocalOf { 20.dp }

val MotoBookShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun MotoBookTheme(
    themeName: String = "FROST_LIGHT",
    glassIntensity: Float = 0.85f,
    cardRadiusDp: Float = 20f,
    content: @Composable () -> Unit
) {
    val palette = MotoThemes.getThemeByName(themeName)

    val lightColorScheme = lightColorScheme(
        primary = palette.primary,
        secondary = palette.secondary,
        background = palette.background,
        surface = palette.surface,
        onPrimary = TextOnAccent,
        onSecondary = TextOnAccent,
        onBackground = palette.textPrimary,
        onSurface = palette.textPrimary,
        error = CoralPrimary
    )

    CompositionLocalProvider(
        LocalThemePalette provides palette,
        LocalGlassIntensity provides glassIntensity,
        LocalCardRadius provides cardRadiusDp.dp
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme,
            shapes = MotoBookShapes,
            content = content
        )
    }
}
