package com.example.motobook.presentation.mileage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.motobook.domain.model.MileageStats
import com.example.motobook.presentation.components.AnimatedCounter
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MileageStatsScreen(
    stats: MileageStats,
    onBackClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

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
                title = stringResource(id = R.string.mileage_stats_title),
                onBackClick = onBackClick
            )

            if (stats.totalCycles == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Insufficient Fuel Data",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To calculate accurate mileage using the Full Tank Cycle method, log at least 2 FULL TANK refuels.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        // Current Mileage Hero Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = palette.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "CURRENT MILEAGE (LAST CYCLE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedCounter(
                                value = stats.currentMileage ?: 0f,
                                unit = "km/L",
                                decimalPlaces = 1,
                                fontSize = 36,
                                color = palette.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val performanceBadge = when {
                                (stats.currentMileage ?: 0f) >= 50f -> "🌟 Excellent Performance"
                                (stats.currentMileage ?: 0f) >= 40f -> "🟢 Good Performance"
                                (stats.currentMileage ?: 0f) >= 30f -> "🟡 Average Performance"
                                else -> "🔴 Low Mileage"
                            }
                            Text(
                                text = performanceBadge,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                        }
                    }

                    item {
                        // 2x2 Grid Stats
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                SmallStatGlassCard(
                                    title = "AVERAGE",
                                    value = String.format("%.1f", stats.averageMileage ?: 0f),
                                    unit = "km/L"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SmallStatGlassCard(
                                    title = "BEST",
                                    value = String.format("%.1f", stats.bestMileage ?: 0f),
                                    unit = "km/L"
                                )
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                SmallStatGlassCard(
                                    title = "WORST",
                                    value = String.format("%.1f", stats.worstMileage ?: 0f),
                                    unit = "km/L"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SmallStatGlassCard(
                                    title = "LAST 5 AVG",
                                    value = String.format("%.1f", stats.lastFiveCycleAverage ?: 0f),
                                    unit = "km/L"
                                )
                            }
                        }
                    }

                    item {
                        // Aggregate Totals
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "TOTAL STATS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total Distance:", fontSize = 14.sp, color = TextSecondary)
                                Text(
                                    text = "${stats.totalDistanceCovered.toInt()} km",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total Fuel Used:", fontSize = 14.sp, color = TextSecondary)
                                Text(
                                    text = "${String.format("%.1f", stats.totalFuelConsumed)} L",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Cost per km:", fontSize = 14.sp, color = TextSecondary)
                                Text(
                                    text = "৳ ${String.format("%.2f", stats.costPerKm ?: 0f)} / km",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "CYCLE HISTORY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    items(stats.completedCycles, key = { it.cycleNumber }) { cycle ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cycle ${cycle.cycleNumber} (${dateFormat.format(Date(cycle.cycleStartDate))} - ${dateFormat.format(Date(cycle.cycleEndDate))})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = "${String.format("%.1f", cycle.mileageKmPerLiter)} km/L",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Distance: ${cycle.distanceKm.toInt()} km  |  Fuel: ${cycle.fuelUsedLiters} L",
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

@Composable
private fun SmallStatGlassCard(
    title: String,
    value: String,
    unit: String
) {
    val palette = LocalThemePalette.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}
