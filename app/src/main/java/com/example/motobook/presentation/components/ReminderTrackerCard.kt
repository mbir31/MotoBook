package com.example.motobook.presentation.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.MaintenanceReminder
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderTrackerCard(
    currentOdometer: Float?,
    reminders: List<MaintenanceReminder>,
    onAddReminderClick: () -> Unit,
    onCompleteReminderClick: (MaintenanceReminder) -> Unit,
    onDeleteReminderClick: (MaintenanceReminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val activeReminders = remember(reminders) {
        reminders.filter { !it.isCompleted }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(palette.primary.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SERVICE & OIL DISTANCE TRACKER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = palette.primary.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.clickable { onAddReminderClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add Reminder",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentOdometer != null && currentOdometer > 0f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Current Odometer: ${currentOdometer.toInt()} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (activeReminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No active service or oil change reminders.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = onAddReminderClick) {
                            Text(
                                text = "+ Setup Engine Oil or Service Reminder",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    activeReminders.forEach { reminder ->
                        ReminderItemRow(
                            reminder = reminder,
                            currentOdometer = currentOdometer ?: 0f,
                            onComplete = { onCompleteReminderClick(reminder) },
                            onDelete = { onDeleteReminderClick(reminder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderItemRow(
    reminder: MaintenanceReminder,
    currentOdometer: Float,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Calculate status and distance / date remaining
    val kmRemaining = if (reminder.dueOdometer != null) {
        reminder.dueOdometer - currentOdometer
    } else null

    val daysRemaining = if (reminder.dueDate != null) {
        val diff = reminder.dueDate - System.currentTimeMillis()
        (diff / (1000 * 60 * 60 * 24)).toInt()
    } else null

    val isOverdue = (kmRemaining != null && kmRemaining <= 0) || (daysRemaining != null && daysRemaining <= 0)
    val isDueSoon = (kmRemaining != null && kmRemaining in 1f..250f) || (daysRemaining != null && daysRemaining in 1..7)

    val badgeColor = when {
        isOverdue -> RedPrimary
        isDueSoon -> AmberPrimary
        else -> EmeraldPrimary
    }

    val badgeText = when {
        isOverdue -> "OVERDUE"
        isDueSoon -> "DUE SOON"
        else -> "OK"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surface.copy(alpha = 0.6f),
        shape = MotoBookShapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(badgeColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                reminder.title.contains("Oil", ignoreCase = true) -> Icons.Default.OilBarrel
                                reminder.title.contains("Filter", ignoreCase = true) -> Icons.Default.FilterAlt
                                else -> Icons.Default.Build
                            },
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = reminder.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Metrics display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (kmRemaining != null && reminder.dueOdometer != null) {
                        if (kmRemaining <= 0) {
                            Text(
                                text = "Overdue by ${(-kmRemaining).toInt()} km",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedPrimary
                            )
                        } else {
                            Text(
                                text = "${kmRemaining.toInt()} km remaining",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                        Text(
                            text = "Target Odo: ${reminder.dueOdometer.toInt()} km",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    val dueDate = reminder.dueDate
                    if (daysRemaining != null && dueDate != null) {
                        if (daysRemaining <= 0) {
                            Text(
                                text = "Overdue date (${dateFormat.format(Date(dueDate))})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedPrimary
                            )
                        } else {
                            Text(
                                text = "$daysRemaining days left (${dateFormat.format(Date(dueDate))})",
                                fontSize = 12.sp,
                                color = badgeColor
                            )
                        }
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onComplete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mark Complete",
                            tint = EmeraldPrimary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Reminder",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}
