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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.R
import com.example.motobook.domain.model.*

import com.example.motobook.presentation.components.*
import com.example.motobook.presentation.maintenance.AddReminderDialog
import com.example.motobook.presentation.navigation.Screen
import com.example.motobook.presentation.theme.*

@Composable
fun DashboardScreen(
    bike: Bike?,
    mileageStats: MileageStats,
    lastFuelEntry: FuelEntry?,
    fuelEntries: List<FuelEntry> = emptyList(),
    serviceEntries: List<ServiceEntry> = emptyList(),
    washEntries: List<WashEntry> = emptyList(),
    reminders: List<MaintenanceReminder> = emptyList(),
    currentOdometer: Float? = null,
    onAddReminder: (MaintenanceReminder) -> Unit = {},
    onCompleteReminder: (MaintenanceReminder) -> Unit = {},
    onDeleteReminder: (MaintenanceReminder) -> Unit = {},
    onAddBikeClick: () -> Unit,
    onNavigateToMileageStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onTabSelected: (String) -> Unit
) {
    val palette = LocalThemePalette.current
    var showAddReminderDialog by remember { mutableStateOf(false) }

    if (showAddReminderDialog && bike != null) {
        AddReminderDialog(
            bikeId = bike.bikeId,
            currentOdometer = currentOdometer,
            onDismiss = { showAddReminderDialog = false },
            onSaveReminder = { reminder ->
                onAddReminder(reminder)
                showAddReminderDialog = false
            }
        )
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
            MotoTopBar(
                title = stringResource(id = R.string.app_name),
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToSettings() },
                        color = palette.surface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f)),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = palette.primary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.settings_title),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }
                    }
                }
            )

            if (bike == null) {
                // Empty state when no motorcycle added
                Box(
                    modifier = Modifier.weight(1f),
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
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
                                            .size(58.dp)
                                            .clip(CircleShape)
                                            .background(palette.primary.copy(alpha = 0.2f), CircleShape)
                                            .border(1.5.dp, palette.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!bike.bikeImagePath.isNullOrBlank()) {
                                            AsyncImage(
                                                model = bike.bikeImagePath,
                                                contentDescription = "Bike Profile Photo",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.TwoWheeler,
                                                contentDescription = null,
                                                tint = palette.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
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
                                text = stringResource(id = R.string.est_fuel_range_header),
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
                                    text = "${stringResource(id = R.string.est_fuel_label)}: ~${String.format("%.1f", estFuel)} L",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = "${stringResource(id = R.string.est_range_label)}: ~${estRange.toInt()} km",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }

                            if (lastFuelEntry != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${stringResource(id = R.string.last_fill_label)}: ${lastFuelEntry.fuelQuantity} L (${lastFuelEntry.odometer.toInt()} km)",
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

                    // Maintenance Reminders & Engine Oil / Filter Distance Tracker
                    item {
                        ReminderTrackerCard(
                            currentOdometer = currentOdometer,
                            reminders = reminders,
                            onAddReminderClick = { showAddReminderDialog = true },
                            onCompleteReminderClick = onCompleteReminder,
                            onDeleteReminderClick = onDeleteReminder
                        )
                    }

                    // Monthly Expenditure Summary
                    item {
                        MonthlyExpenditureCard(
                            fuelEntries = fuelEntries,
                            serviceEntries = serviceEntries,
                            washEntries = washEntries
                        )
                    }


                    item {
                        // Vehicle Health & Overview Quick Navigation Banner
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "OVERALL MOTORCYCLE STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "All Systems Ready",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Use Fuel & Maintenance tabs to manage logs",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = palette.primary.copy(alpha = 0.15f),
                                    shape = MotoBookShapes.small,
                                    modifier = Modifier.clickable { onTabSelected(Screen.Maintenance.route) }
                                ) {
                                    Text(
                                        text = "Maintenance →",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // iOS Floating Navigation Bar
            IosBottomBar(
                currentRoute = Screen.Dashboard.route,
                onTabSelected = onTabSelected
            )
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
