package com.example.motobook.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BackupScreen(
    lastBackupTime: Long,
    autoBackupEnabled: Boolean,
    onAutoBackupToggle: (Boolean) -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

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
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                MotoTopBar(title = stringResource(id = R.string.backup_title), onBackClick = onBackClick)

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = palette.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = stringResource(id = R.string.last_backup).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (lastBackupTime > 0) dateFormat.format(Date(lastBackupTime)) else "Auto Backup Active",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "⚡ Automatic backup saves your data instantly to storage on every single entry.",
                        fontSize = 12.sp,
                        color = palette.primary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Folder Info Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AmberPrimary.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BACKUP STORAGE LOCATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Internal Storage > Documents > MotoBook_Backups",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "Directly accessible via phone File Manager.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.auto_backup),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "Instant background save on every input",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = autoBackupEnabled,
                            onCheckedChange = onAutoBackupToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextOnAccent,
                                checkedTrackColor = palette.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlowButton(
                    text = "Backup Now to Storage",
                    icon = Icons.Default.CloudUpload,
                    onClick = onCreateBackup
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onRestoreBackup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MotoBookShapes.medium,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(palette.primary, CyanLight)))
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restore Data from MotoBook Folder",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary
                    )
                }
            }
        }
    }
}

