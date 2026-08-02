package com.example.motobook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun MaintenanceAlertsBanner(
    currentOdometer: Float?,
    reminders: List<MaintenanceReminder>,
    onCompleteReminder: (MaintenanceReminder) -> Unit,
    onNavigateToService: () -> Unit,
    onAddReminderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    val currentOdo = currentOdometer ?: 0f

    val alertItems = remember(reminders, currentOdo) {
        reminders.filter { !it.isCompleted }.mapNotNull { reminder ->
            val dueOdo = reminder.dueOdometer
            val kmRemaining = if (dueOdo != null) dueOdo - currentOdo else null
            val daysRemaining = if (reminder.dueDate != null) {
                ((reminder.dueDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
            } else null

            val isOverdue = (kmRemaining != null && kmRemaining <= 0) || (daysRemaining != null && daysRemaining <= 0)
            val isDueSoon = (kmRemaining != null && kmRemaining in 1f..200f) || (daysRemaining != null && daysRemaining in 1..7)

            if (isOverdue || isDueSoon) {
                Triple(reminder, isOverdue, kmRemaining)
            } else null
        }
    }

    if (alertItems.isEmpty()) return

    var isExpanded by remember { mutableStateOf(true) }
    val overdueCount = alertItems.count { it.second }
    val urgentColor = if (overdueCount > 0) RedPrimary else AmberPrimary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = urgentColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, urgentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(urgentColor.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerts",
                            tint = urgentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (overdueCount > 0) "🚨 URGENT MAINTENANCE ALERTS ($overdueCount)" else "⚠️ UPCOMING SERVICE ALERTS (${alertItems.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = urgentColor,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap to view tasks requiring attention",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = urgentColor
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    alertItems.forEach { (reminder, isOverdue, kmRemaining) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = palette.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, (if (isOverdue) RedPrimary else AmberPrimary).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when {
                                                reminder.title.contains("Oil", ignoreCase = true) -> "🛢️ "
                                                reminder.title.contains("Chain", ignoreCase = true) -> "⚙️ "
                                                reminder.title.contains("Filter", ignoreCase = true) -> "💨 "
                                                reminder.title.contains("Spark", ignoreCase = true) -> "⚡ "
                                                else -> "🛠️ "
                                            },
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = reminder.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isOverdue) {
                                            if (kmRemaining != null) "Overdue by ${(-kmRemaining).toInt()} km (Target: ${reminder.dueOdometer?.toInt()} km)"
                                            else "Overdue date!"
                                        } else {
                                            if (kmRemaining != null) "${kmRemaining.toInt()} km remaining (Target: ${reminder.dueOdometer?.toInt()} km)"
                                            else "Due soon!"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOverdue) RedPrimary else AmberPrimary
                                    )
                                }

                                Button(
                                    onClick = { onCompleteReminder(reminder) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                        contentColor = EmeraldPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Done", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onNavigateToService) {
                            Text(
                                text = "Log Maintenance Service →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
