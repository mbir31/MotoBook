package com.example.motobook.presentation.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*

@Composable
fun LanguageSelectionScreen(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    var selectedLang by remember { mutableStateOf(currentLanguage) }
    val palette = LocalThemePalette.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.background, palette.surface)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                MotoTopBar(title = stringResource(id = R.string.select_language_title))

                Spacer(modifier = Modifier.height(32.dp))

                LanguageOptionCard(
                    title = stringResource(id = R.string.lang_english),
                    subtitle = stringResource(id = R.string.lang_english_sub),
                    flagEmoji = "🇬🇧",
                    isSelected = selectedLang == "en",
                    onClick = {
                        selectedLang = "en"
                        onLanguageSelected("en")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LanguageOptionCard(
                    title = stringResource(id = R.string.lang_bengali),
                    subtitle = stringResource(id = R.string.lang_bengali_sub),
                    flagEmoji = "🇧🇩",
                    isSelected = selectedLang == "bn",
                    onClick = {
                        selectedLang = "bn"
                        onLanguageSelected("bn")
                    }
                )
            }

            GlowButton(
                text = stringResource(id = R.string.btn_continue),
                onClick = onContinueClick
            )
        }
    }
}

@Composable
private fun LanguageOptionCard(
    title: String,
    subtitle: String,
    flagEmoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalThemePalette.current
    val borderColor = if (isSelected) palette.primary else GlassBorder

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(if (isSelected) 2.dp else 1.dp, borderColor, MotoBookShapes.large)
            .clickable { onClick() },
        backgroundColor = if (isSelected) palette.primary.copy(alpha = 0.15f) else palette.surface.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = flagEmoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
