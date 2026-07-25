package com.example.motobook.presentation.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.MaintenanceReminder
import com.example.motobook.presentation.theme.*

data class PresetReminder(
    val title: String,
    val defaultIntervalKm: Float?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val PRESET_REMINDERS = listOf(
    PresetReminder("Engine Oil Change", 2500f, Icons.Default.OilBarrel),
    PresetReminder("Oil Filter Replacement", 5000f, Icons.Default.FilterAlt),
    PresetReminder("Air Filter Cleaning", 3000f, Icons.Default.Air),
    PresetReminder("General Servicing", 4000f, Icons.Default.Build),
    PresetReminder("Spark Plug Check", 10000f, Icons.Default.FlashOn),
    PresetReminder("Chain Lube & Tension", 500f, Icons.Default.Link)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    bikeId: Long,
    currentOdometer: Float?,
    onDismiss: () -> Unit,
    onSaveReminder: (MaintenanceReminder) -> Unit
) {
    val palette = LocalThemePalette.current

    var selectedTitle by remember { mutableStateOf("Engine Oil Change") }
    var targetOdometerText by remember {
        mutableStateOf(
            if (currentOdometer != null && currentOdometer > 0) {
                (currentOdometer + 2500f).toInt().toString()
            } else "2500"
        )
    }
    var intervalKmText by remember { mutableStateOf("2500") }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Maintenance & Service Reminder",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quick Presets:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                // Presets horizontal flow / grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PRESET_REMINDERS.chunked(2).forEach { rowPresets ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPresets.forEach { preset ->
                                val isSelected = selectedTitle == preset.title
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedTitle = preset.title
                                            preset.defaultIntervalKm?.let { interval ->
                                                intervalKmText = interval.toInt().toString()
                                                val baseOdo = currentOdometer ?: 0f
                                                targetOdometerText = (baseOdo + interval).toInt().toString()
                                            }
                                        },
                                    color = if (isSelected) palette.primary.copy(alpha = 0.2f) else palette.surface,
                                    shape = MotoBookShapes.small,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) palette.primary else TextMuted.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) palette.primary else TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = preset.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) palette.primary else palette.textPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = selectedTitle,
                    onValueChange = { selectedTitle = it },
                    label = { Text("Reminder Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = targetOdometerText,
                    onValueChange = { targetOdometerText = it },
                    label = { Text("Target Odometer (km)") },
                    supportingText = {
                        if (currentOdometer != null) {
                            Text("Current Odometer: ${currentOdometer.toInt()} km")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = intervalKmText,
                    onValueChange = { intervalKmText = it },
                    label = { Text("Repeat Interval (km) [Optional]") },
                    supportingText = { Text("Auto schedules next reminder when completed") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes / Oil Brand (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetOdo = targetOdometerText.toFloatOrNull()
                    val intervalKm = intervalKmText.toFloatOrNull()
                    if (selectedTitle.isNotBlank()) {
                        val reminder = MaintenanceReminder(
                            bikeId = bikeId,
                            title = selectedTitle,
                            dueOdometer = targetOdo,
                            intervalKm = intervalKm,
                            notes = notesText.ifBlank { null }
                        )
                        onSaveReminder(reminder)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
            ) {
                Text("Save Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
