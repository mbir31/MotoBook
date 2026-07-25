package com.example.motobook.presentation.chain

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.domain.model.ChainEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChainScreen(
    entries: List<ChainEntry>,
    onSaveChain: (odometer: String, lubeType: String, notes: String?, dateMillis: Long) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var odometer by remember { mutableStateOf("") }
    var lubricantType by remember { mutableStateOf("Chain Lube Spray") }
    var notes by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val formattedSelectedDate = remember(selectedDateMillis) {
        val dateStr = dateFormat.format(Date(selectedDateMillis))
        val todayStr = dateFormat.format(Date())
        if (dateStr == todayStr) "Today ($dateStr)" else dateStr
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

    val latestChain = sortedEntries.firstOrNull()

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
            MotoTopBar(title = stringResource(id = R.string.chain_screen_title), onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(12.dp))

            // Last Chain Lube Status Banner
            if (latestChain != null) {
                val daysAgo = remember(latestChain.date) {
                    val diff = System.currentTimeMillis() - latestChain.date
                    val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                    when {
                        days <= 0 -> "Today"
                        days == 1 -> "Yesterday"
                        else -> "$days days ago"
                    }
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = AmberPrimary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AmberPrimary.copy(alpha = 0.2f), shape = MotoBookShapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LAST CHAIN LUBE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$daysAgo (${dateFormat.format(Date(latestChain.date))})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Form Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.chain_screen_title).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Date Selector Row
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
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Maintenance Date",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedSelectedDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pick Date",
                                tint = palette.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = stringResource(id = R.string.odometer),
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = lubricantType,
                    onValueChange = { lubricantType = it },
                    label = stringResource(id = R.string.lube_type)
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = stringResource(id = R.string.notes)
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlowButton(
                    text = stringResource(id = R.string.save_chain_log),
                    onClick = {
                        onSaveChain(odometer, lubricantType, notes.ifBlank { null }, selectedDateMillis)
                        odometer = ""
                        notes = ""
                        selectedDateMillis = System.currentTimeMillis()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.recent_activity).uppercase(),
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
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No chain maintenance logs recorded.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(sortedEntries, key = { it.chainId }) { chain ->
                        val entryDaysAgo = remember(chain.date) {
                            val diff = System.currentTimeMillis() - chain.date
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
                                Column {
                                    Text(
                                        text = "${dateFormat.format(Date(chain.date))} ($entryDaysAgo)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = chain.lubricantType ?: "Lubricant",
                                        fontSize = 14.sp,
                                        color = palette.primary
                                    )
                                }
                                if (chain.odometer != null) {
                                    Text(
                                        text = "📍 ${chain.odometer.toInt()} km",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
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
