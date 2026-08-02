package com.example.motobook.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.motobook.domain.model.Bike
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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
    currentBike: Bike? = null,
    onUpdateBikePhoto: (String?) -> Unit = {},
    isOnlineMode: Boolean = false,
    onOnlineModeChange: (Boolean) -> Unit = {},
    onNavigateToBackup: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = File(context.filesDir, "bike_photo_${currentBike?.bikeId ?: 0}.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        onUpdateBikePhoto(file.absolutePath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

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

            // Motorcycle Profile Photo Card
            if (currentBike != null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = palette.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MOTORCYCLE PROFILE PHOTO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(palette.primary.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, palette.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!currentBike.bikeImagePath.isNullOrBlank()) {
                                AsyncImage(
                                    model = currentBike.bikeImagePath,
                                    contentDescription = "Bike Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = currentBike.bikeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${currentBike.brand} ${currentBike.model}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = palette.primary.copy(alpha = 0.18f),
                                    shape = MotoBookShapes.small,
                                    modifier = Modifier.clickable { photoPickerLauncher.launch("image/*") }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = palette.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (currentBike.bikeImagePath.isNullOrBlank()) "Upload Photo" else "Change Photo",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.primary
                                        )
                                    }
                                }

                                if (!currentBike.bikeImagePath.isNullOrBlank()) {
                                    Surface(
                                        color = Color.Red.copy(alpha = 0.12f),
                                        shape = MotoBookShapes.small,
                                        modifier = Modifier.clickable { onUpdateBikePhoto(null) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Remove",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

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
                    text = stringResource(id = R.string.theme_variant),
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
                    text = "${stringResource(id = R.string.glass_intensity)}: ${(glassIntensity * 100).toInt()}%",
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
                    text = "${stringResource(id = R.string.card_radius)}: ${cardRadius.toInt()}dp",
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

            // App Online State Toggle Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isOnlineMode) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isOnlineMode) palette.primary else TextMuted
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "APP ONLINE STATE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnlineMode) palette.primary else palette.textPrimary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isOnlineMode) "Online Mode Active 🌐" else "Offline Mode Active 🔒",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOnlineMode) EmeraldPrimary else AmberPrimary
                            )
                        }
                    }

                    Switch(
                        checked = isOnlineMode,
                        onCheckedChange = onOnlineModeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.primary,
                            checkedTrackColor = palette.primary.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = palette.surface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = MotoBookShapes.small,
                    color = if (isOnlineMode) palette.primary.copy(alpha = 0.1f) else palette.surface.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, if (isOnlineMode) palette.primary.copy(alpha = 0.3f) else GlassBorder)
                ) {
                    Text(
                        text = if (isOnlineMode) {
                            "🌐 Online Mode Active: Cloud synchronization and Google Drive backup services are ready when connected."
                        } else {
                            "🔒 Offline Mode Active: The app operates 100% locally without external network calls. Your motorcycle profile, logs, and maintenance data stay strictly on device. Toggle ON whenever you wish to sync cloud backups with Google Drive."
                        },
                        fontSize = 12.sp,
                        color = palette.textPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
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
                        text = stringResource(id = R.string.manage_label),
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
