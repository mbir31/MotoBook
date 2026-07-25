package com.example.motobook.presentation.tyre

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
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
import com.example.motobook.domain.model.TyrePressureEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TyrePressureScreen(
    recommendedFrontPsi: Float = 28f,
    recommendedRearPsi: Float = 32f,
    entries: List<TyrePressureEntry>,
    onSaveLog: (dateMillis: Long, notes: String?) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }

    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val formattedSelectedDate = remember(selectedDateMillis) {
        val dateStr = dateFormat.format(Date(selectedDateMillis))
        val todayStr = dateFormat.format(Date())
        if (dateStr == todayStr) {
            "Today ($dateStr)"
        } else {
            dateStr
        }
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val datePickerDialog = remember(selectedDateMillis, context) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedDateMillis = cal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Sort entries by date descending
    val sortedEntries = remember(entries) {
        entries.sortedByDescending { it.date }
    }

    // Find the latest refill before the current/present day (or latest entry overall)
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val previousRefill = remember(sortedEntries, todayStart) {
        sortedEntries.firstOrNull { it.date < todayStart } ?: sortedEntries.firstOrNull()
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
                .padding(horizontal = 20.dp)
        ) {
            MotoTopBar(title = stringResource(id = R.string.tyre_screen_title), onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(12.dp))

            // Recommended Pressure Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = stringResource(id = R.string.recommended_pressure).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.front_tyre).uppercase(),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${recommendedFrontPsi.toInt()} PSI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.rear_tyre).uppercase(),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${recommendedRearPsi.toInt()} PSI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Previous Refill Status Banner
            if (previousRefill != null) {
                val daysAgo = remember(previousRefill.date) {
                    val diff = System.currentTimeMillis() - previousRefill.date
                    val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                    when {
                        days <= 0 -> "Today"
                        days == 1 -> "Yesterday"
                        else -> "$days days ago"
                    }
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = palette.primary.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.last_refilled_before).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$daysAgo (${dateFormat.format(Date(previousRefill.date))})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Air Refill Date Input Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.air_refill_date).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Date Selector Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    color = palette.surface.copy(alpha = 0.6f),
                    shape = MotoBookShapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.select_air_refill_date),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedSelectedDate,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pick Refill Date",
                                tint = palette.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = stringResource(id = R.string.notes)
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlowButton(
                    text = stringResource(id = R.string.log_air_refill),
                    onClick = {
                        onSaveLog(selectedDateMillis, notes.ifBlank { null })
                        selectedDateMillis = System.currentTimeMillis()
                        notes = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.previous_refill_dates).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (sortedEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_previous_refill),
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(sortedEntries, key = { it.tyrePressureId }) { log ->
                        val entryDateFormatted = dateFormat.format(Date(log.date))
                        val entryDaysAgo = remember(log.date) {
                            val diff = System.currentTimeMillis() - log.date
                            val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                            when {
                                days <= 0 -> "Today"
                                days == 1 -> "Yesterday"
                                else -> "$days days ago"
                            }
                        }

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Air,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = entryDateFormatted,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val subText = if (!log.notes.isNull_or_empty()) "$entryDaysAgo • ${log.notes}" else entryDaysAgo
                                        Text(
                                            text = subText,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.2f),
                                    shape = MotoBookShapes.small
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Refilled",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this.isNull_or_blank()
private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
