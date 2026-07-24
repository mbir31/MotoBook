package com.example.motobook.presentation.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.domain.model.Bike
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.domain.model.MileageStats
import com.example.motobook.presentation.components.AnimatedCounter
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*

@Composable
fun DashboardScreen(
    bike: Bike?,
    mileageStats: MileageStats,
    lastFuelEntry: FuelEntry?,
    onAddBikeClick: () -> Unit,
    onNavigateToAddFuel: () -> Unit,
    onNavigateToFuelHistory: () -> Unit,
    onNavigateToMileageStats: () -> Unit,
    onNavigateToAddService: () -> Unit,
    onNavigateToServiceHistory: () -> Unit,
    onNavigateToTyrePressure: () -> Unit,
    onNavigateToWash: () -> Unit,
    onNavigateToChain: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val palette = LocalThemePalette.current

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
                title = stringResource(id = R.string.app_name),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = palette.textPrimary
                        )
                    }
                }
            )

            if (bike == null) {
                // Empty state when no motorcycle added
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_motobook_logo),
                                contentDescription = "MotoBook Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = stringResource(id = R.string.no_bike_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GlowButton(
                            text = stringResource(id = R.string.btn_add_bike),
                            onClick = onAddBikeClick
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        // Motorcycle Hero Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = palette.primary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(palette.primary.copy(alpha = 0.2f), CircleShape)
                                            .border(1.dp, palette.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TwoWheeler,
                                            contentDescription = null,
                                            tint = palette.primary,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = bike.bikeName,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${bike.brand} ${bike.model} (${bike.year})",
                                            fontSize = 13.sp,
                                            color = TextSecondary
                                        )
                                        if (bike.registrationNumber.isNotBlank()) {
                                            Text(
                                                text = bike.registrationNumber,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.primary
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = palette.surface.copy(alpha = 0.6f),
                                    shape = MotoBookShapes.small,
                                    modifier = Modifier.clickable { onAddBikeClick() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Bike",
                                        tint = TextSecondary,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Fuel Level & Range Gauge Card
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "ESTIMATED FUEL & RANGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val estFuel = bike.tankCapacity * 0.7f // Estimated remaining
                            val estRange = estFuel * (mileageStats.averageMileage ?: 40f)

                            // Animated Liquid Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(palette.surface)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(7.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.7f)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(palette.primary, CyanLight)
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Est. Fuel: ~${String.format("%.1f", estFuel)} L",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = "Est. Range: ~${estRange.toInt()} km",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }

                            if (lastFuelEntry != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Last fill: ${lastFuelEntry.fuelQuantity} L (${lastFuelEntry.odometer.toInt()} km)",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    item {
                        // Performance Cards Grid (2x2)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToMileageStats() }
                                ) {
                                    DashMetricCard(
                                        title = stringResource(id = R.string.current_mileage),
                                        value = mileageStats.currentMileage,
                                        unit = "km/L",
                                        accent = palette.primary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToMileageStats() }
                                ) {
                                    DashMetricCard(
                                        title = stringResource(id = R.string.avg_mileage),
                                        value = mileageStats.averageMileage,
                                        unit = "km/L",
                                        accent = PurplePrimary
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToMileageStats() }
                                ) {
                                    DashMetricCard(
                                        title = stringResource(id = R.string.best_mileage),
                                        value = mileageStats.bestMileage,
                                        unit = "km/L",
                                        accent = EmeraldPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToMileageStats() }
                                ) {
                                    DashMetricCard(
                                        title = stringResource(id = R.string.cost_per_km),
                                        value = mileageStats.costPerKm,
                                        unit = "৳/km",
                                        accent = AmberPrimary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(id = R.string.quick_actions),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    item {
                        // Quick Actions Grid (3x2)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                QuickActionButton(
                                    title = stringResource(id = R.string.add_fuel),
                                    icon = Icons.Default.LocalGasStation,
                                    onClick = onNavigateToAddFuel,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = stringResource(id = R.string.tyre_pressure),
                                    icon = Icons.Default.Speed,
                                    onClick = onNavigateToTyrePressure,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = stringResource(id = R.string.bike_wash),
                                    icon = Icons.Default.WaterDrop,
                                    onClick = onNavigateToWash,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                QuickActionButton(
                                    title = "Service",
                                    icon = Icons.Default.Build,
                                    onClick = onNavigateToAddService,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = stringResource(id = R.string.chain_lube),
                                    icon = Icons.Default.Link,
                                    onClick = onNavigateToChain,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    title = "History",
                                    icon = Icons.Default.History,
                                    onClick = onNavigateToHistory,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashMetricCard(
    title: String,
    value: Float?,
    unit: String,
    accent: Color
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (value != null && value > 0f) {
            AnimatedCounter(
                value = value,
                unit = unit,
                fontSize = 20,
                color = accent
            )
        } else {
            Text(
                text = "-- $unit",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current

    GlassCard(
        modifier = modifier.clickable { onClick() },
        cornerRadius = 14.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
