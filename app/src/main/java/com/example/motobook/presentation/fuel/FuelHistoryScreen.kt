package com.example.motobook.presentation.fuel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
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
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FuelHistoryScreen(
    entries: List<FuelEntry>,
    totalCost: Float,
    totalQuantity: Float,
    onAddFuelClick: () -> Unit,
    onEditEntryClick: (FuelEntry) -> Unit,
    onDeleteEntryClick: (FuelEntry) -> Unit,
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
                title = "Fuel History",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onAddFuelClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Fuel",
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
                        Text(text = "TOTAL COST", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "৳ ${String.format("%.0f", totalCost)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TOTAL FUEL", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "${String.format("%.1f", totalQuantity)} L",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ENTRIES", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "${entries.size}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
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
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No fuel entries yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to log your first refuel",
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
                    items(entries, key = { it.fuelId }) { entry ->
                        FuelEntryCard(
                            entry = entry,
                            dateFormat = dateFormat,
                            onEditClick = { onEditEntryClick(entry) },
                            onDeleteClick = { onDeleteEntryClick(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FuelEntryCard(
    entry: FuelEntry,
    dateFormat: SimpleDateFormat,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val palette = LocalThemePalette.current
    val isFull = entry.refuelType.equals("FULL", ignoreCase = true)
    val badgeColor = if (isFull) EmeraldPrimary else AmberPrimary

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = MotoBookShapes.small,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = if (isFull) "FULL" else "PARTIAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = dateFormat.format(Date(entry.date)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
            }

            Row {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
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

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${entry.fuelQuantity} L  |  ৳ ${entry.pricePerLiter}/L",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 ${entry.odometer.toInt()} km",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                if (!entry.fuelStation.isNull_or_blank()) {
                    Text(
                        text = "⛽ ${entry.fuelStation}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Text(
                text = "৳ ${String.format("%.0f", entry.totalCost)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.primary
            )
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this.isNullOrBlank()
