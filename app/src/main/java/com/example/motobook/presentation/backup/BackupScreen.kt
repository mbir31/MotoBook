package com.example.motobook.presentation.backup

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.bike.MotoTextField
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
    onExportFuelCsv: () -> Unit = {},
    onExportServiceCsv: () -> Unit = {},
    onExportRemindersCsv: () -> Unit = {},
    onExportFullJson: () -> Unit = {},
    googleDriveAccount: String? = null,
    isOnlineMode: Boolean = false,
    onConnectGoogleDriveAccount: (String) -> Unit = {},
    onDisconnectGoogleDriveAccount: () -> Unit = {},
    onSyncGoogleDrive: () -> Unit = {},
    onRestoreGoogleDrive: () -> Unit = {},
    isDriveSyncing: Boolean = false,
    driveSyncStatus: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    var showConnectDialog by remember { mutableStateOf(false) }
    var accountEmailInput by remember { mutableStateOf(googleDriveAccount ?: "mbr.uhq@gmail.com") }

    if (showConnectDialog) {
        AlertDialog(
            onDismissRequest = { showConnectDialog = false },
            title = {
                Text(
                    text = "Connect Google Drive Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your Google email address to connect your individual Google Drive for automatic cloud backup:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MotoTextField(
                        value = accountEmailInput,
                        onValueChange = { accountEmailInput = it },
                        label = "Google Account Email"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (accountEmailInput.isNotBlank()) {
                            onConnectGoogleDriveAccount(accountEmailInput.trim())
                            showConnectDialog = false
                            Toast.makeText(context, "Google Drive connected: ${accountEmailInput.trim()}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
                ) {
                    Text("Connect & Authorize", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConnectDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = palette.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

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
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MotoTopBar(title = stringResource(id = R.string.backup_title), onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            // Last Backup & Continuous Local Auto-Backup Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.last_backup).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lastBackupTime > 0) dateFormat.format(Date(lastBackupTime)) else "Continuous Auto-Backup Active",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(EmeraldPrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚡ Continuous local backup runs automatically on every entry addition or edit.",
                    fontSize = 12.sp,
                    color = palette.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Storage Location Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AmberPrimary.copy(alpha = 0.2f), CircleShape),
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
                            text = "OFFLINE LOCAL BACKUP LOCATION",
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

            // Google Drive Cloud Sync & Individual Account Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.surface.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyanLight.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = CyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INDIVIDUAL GOOGLE DRIVE CLOUD SYNC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (!googleDriveAccount.isNullOrBlank()) "Connected to $googleDriveAccount"
                                else "Connect your personal Google Drive account",
                                fontSize = 11.sp,
                                color = if (!googleDriveAccount.isNullOrBlank()) EmeraldPrimary else TextSecondary,
                                fontWeight = if (!googleDriveAccount.isNullOrBlank()) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (isDriveSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = palette.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isOnlineMode) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AmberPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🔒 Offline State Active: Google Drive cloud sync is paused. Turn ON 'Online State' in Settings whenever you want to perform cloud backup.",
                                fontSize = 12.sp,
                                color = palette.textPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else if (googleDriveAccount.isNullOrBlank()) {
                    // Account not connected button
                    OutlinedButton(
                        onClick = { showConnectDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(palette.primary, CyanLight)))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connect My Google Account",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                    }
                } else {
                    // Connected account status banner & Disconnect action
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Account: $googleDriveAccount",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary
                                    )
                                    Text(
                                        text = "Private Drive backup folder active",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            TextButton(
                                onClick = {
                                    onDisconnectGoogleDriveAccount()
                                    Toast.makeText(context, "Google Drive account disconnected", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Disconnect", fontSize = 11.sp, color = RedPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onSyncGoogleDrive,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isDriveSyncing
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload to Drive", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onRestoreGoogleDrive,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isDriveSyncing,
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(palette.primary, CyanLight)))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore from Drive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        }
                    }
                }

                if (!driveSyncStatus.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = palette.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = driveSyncStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Export to Local CSV or JSON Section
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "OFFLINE DATA EXPORT (CSV / JSON)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Export your motorcycle records to CSV or JSON format for Excel or offline archiving.",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            onExportFuelCsv()
                            Toast.makeText(context, "Fuel logs exported to Documents/MotoBook_Backups/motobook_fuel_logs.csv", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Fuel Logs (.CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            onExportServiceCsv()
                            Toast.makeText(context, "Service records exported to Documents/MotoBook_Backups/motobook_service_records.csv", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Service Records (.CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            onExportRemindersCsv()
                            Toast.makeText(context, "Reminders exported to Documents/MotoBook_Backups/motobook_maintenance_reminders.csv", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Maintenance Reminders (.CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            onExportFullJson()
                            Toast.makeText(context, "Full JSON database backup exported to Documents/MotoBook_Backups/", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Full Database (.JSON)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-backup toggle switch card
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

            Spacer(modifier = Modifier.height(16.dp))

            GlowButton(
                text = "Manual Local Backup Now",
                icon = Icons.Default.CloudUpload,
                onClick = onCreateBackup
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onRestoreBackup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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
                    text = "Restore Local Backup from Storage Folder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
