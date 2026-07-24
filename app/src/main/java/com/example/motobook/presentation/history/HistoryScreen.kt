package com.example.motobook.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    items: List<UnifiedHistoryItem>,
    selectedCategory: HistoryCategory,
    onCategorySelect: (HistoryCategory) -> Unit,
    onDeleteItem: (UnifiedHistoryItem) -> Unit,
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
                title = "Timeline History",
                onBackClick = onBackClick
            )

            // Category Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(HistoryCategory.values()) { cat ->
                    val isSelected = cat == selectedCategory
                    val chipBg = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f)
                    val contentColor = if (isSelected) TextOnAccent else TextSecondary

                    Surface(
                        color = chipBg,
                        shape = MotoBookShapes.small,
                        modifier = Modifier.clickable { onCategorySelect(cat) }
                    ) {
                        Text(
                            text = cat.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No history records found",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(items, key = { itemKey(it) }) { item ->
                        HistoryCardItem(
                            item = item,
                            dateFormat = dateFormat,
                            onDelete = { onDeleteItem(item) }
                        )
                    }
                }
            }
        }
    }
}

private fun itemKey(item: UnifiedHistoryItem): String = when (item) {
    is UnifiedHistoryItem.Fuel -> "fuel_${item.entry.fuelId}"
    is UnifiedHistoryItem.Service -> "service_${item.entry.serviceId}"
    is UnifiedHistoryItem.Tyre -> "tyre_${item.entry.tyrePressureId}"
    is UnifiedHistoryItem.Wash -> "wash_${item.entry.washId}"
    is UnifiedHistoryItem.Chain -> "chain_${item.entry.chainId}"
}

@Composable
private fun HistoryCardItem(
    item: UnifiedHistoryItem,
    dateFormat: SimpleDateFormat,
    onDelete: () -> Unit
) {
    val palette = LocalThemePalette.current

    val (icon, title, subtitle, costText, accentColor) = when (item) {
        is UnifiedHistoryItem.Fuel -> Tuple5(
            Icons.Default.LocalGasStation,
            "Fuel Refuel (${item.entry.refuelType})",
            "${item.entry.fuelQuantity} L  |  📍 ${item.entry.odometer.toInt()} km",
            "৳ ${String.format("%.0f", item.entry.totalCost)}",
            palette.primary
        )
        is UnifiedHistoryItem.Service -> Tuple5(
            Icons.Default.Build,
            "Service (${item.entry.category})",
            "📍 ${item.entry.odometer.toInt()} km  |  ${item.entry.itemsServiced.joinToString()}",
            "৳ ${String.format("%.0f", item.entry.totalCost)}",
            PurplePrimary
        )
        is UnifiedHistoryItem.Tyre -> Tuple5(
            Icons.Default.Speed,
            "Tyre Pressure Log",
            "Front: ${item.entry.frontPsi} PSI  |  Rear: ${item.entry.rearPsi} PSI",
            "",
            CyanPrimary
        )
        is UnifiedHistoryItem.Wash -> Tuple5(
            Icons.Default.WaterDrop,
            "Bike Wash (${item.entry.washType})",
            item.entry.notes ?: "Wash record",
            if (item.entry.cost != null) "৳ ${String.format("%.0f", item.entry.cost)}" else "",
            EmeraldPrimary
        )
        is UnifiedHistoryItem.Chain -> Tuple5(
            Icons.Default.Link,
            "Chain Lubrication",
            "${item.entry.lubricantType ?: "Lube"} ${if (item.entry.odometer != null) "| 📍 ${item.entry.odometer?.toInt()} km" else ""}",
            "",
            AmberPrimary
        )
    }

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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = dateFormat.format(Date(item.dateMillis)),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (costText.isNotEmpty()) {
                    Text(
                        text = costText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = CoralPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
)
