package com.example.motobook.presentation.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
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
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ServiceHistoryScreen(
    entries: List<ServiceEntry>,
    totalCost: Float,
    onAddServiceClick: () -> Unit,
    onDeleteEntryClick: (ServiceEntry) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

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
            MotoTopBar(
                title = stringResource(id = R.string.service_history_title),
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onAddServiceClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Service",
                            tint = palette.primary
                        )
                    }
                }
            )

            // Summary Bar
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.total_spent).uppercase(),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "৳ ${String.format("%.0f", totalCost)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "RECORDS", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "${entries.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No service records yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to log maintenance or repairs",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(entries, key = { it.serviceId }) { entry ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = dateFormat.format(Date(entry.date)),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "📍 ${entry.odometer.toInt()} km  |  ${entry.category}",
                                        fontSize = 13.sp,
                                        color = palette.primary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "৳ ${String.format("%.0f", entry.totalCost)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PurplePrimary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    IconButton(
                                        onClick = { onDeleteEntryClick(entry) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = CoralPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            if (entry.itemsServiced.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Serviced: ${entry.itemsServiced.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (!entry.serviceCenterName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Center: ${entry.serviceCenterName}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
