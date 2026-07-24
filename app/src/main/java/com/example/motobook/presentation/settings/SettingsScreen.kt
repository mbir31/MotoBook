package com.example.motobook.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*

@Composable
fun SettingsScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    glassIntensity: Float,
    onGlassIntensityChange: (Float) -> Unit,
    cardRadius: Float,
    onCardRadiusChange: (Float) -> Unit,
    onNavigateToBackup: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current

    val themes = listOf(
        "FROST_LIGHT",
        "PEARL_GLASS"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(palette.background, palette.surface)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            MotoTopBar(title = stringResource(id = R.string.settings_title), onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            // Language Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = palette.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(id = R.string.language).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SegmentedToggle(
                    options = listOf("English 🇬🇧", "বাংলা 🇧🇩"),
                    selectedIndex = if (currentLanguage == "bn") 1 else 0,
                    onOptionSelected = { index ->
                        onLanguageChange(if (index == 1) "bn" else "en")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appearance & Themes
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = palette.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(id = R.string.appearance).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Theme Variant",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                themes.forEach { themeName ->
                    val isSelected = themeName == currentTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeChange(themeName) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = themeName.replace("_", " "),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) palette.primary else palette.textPrimary
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { onThemeChange(themeName) },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Glass Intensity: ${(glassIntensity * 100).toInt()}%",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Slider(
                    value = glassIntensity,
                    onValueChange = onGlassIntensityChange,
                    valueRange = 0.3f..0.95f,
                    colors = SliderDefaults.colors(thumbColor = palette.primary, activeTrackColor = palette.primary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Card Corner Radius: ${cardRadius.toInt()}dp",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Slider(
                    value = cardRadius,
                    onValueChange = onCardRadiusChange,
                    valueRange = 8f..32f,
                    colors = SliderDefaults.colors(thumbColor = palette.primary, activeTrackColor = palette.primary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Backup & Data Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBackup() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = palette.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.backup_title),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    }
                    Text(
                        text = "Manage →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.about).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.about_text),
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
