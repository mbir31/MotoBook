package com.example.motobook.presentation.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.motobook.data.remote.BikeAiDiagnosticResult
import com.example.motobook.data.remote.BikeSpecFetcher
import com.example.motobook.domain.model.*
import com.example.motobook.presentation.components.*
import com.example.motobook.presentation.maintenance.AddReminderDialog
import com.example.motobook.presentation.navigation.Screen
import com.example.motobook.presentation.theme.*
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showSpecDetailsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reminders, currentOdometer) {
        com.example.motobook.utils.MaintenanceNotificationHelper.checkAndSendMaintenanceNotifications(
            context = context,
            reminders = reminders,
            currentOdometer = currentOdometer
        )
    }

    if (showSpecDetailsDialog && bike != null) {
        BikeSpecDialog(
            bike = bike,
            onDismiss = { showSpecDetailsDialog = false }
        )
    }

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
                                modifier = Modifier.size(22.dp)
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
                                            .size(62.dp)
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
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = palette.primary.copy(alpha = 0.18f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "${bike.color} • ${bike.engineCc.toInt()} cc (${bike.countryOfOrigin})",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = palette.primary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = palette.surface.copy(alpha = 0.6f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                                            ) {
                                                Text(
                                                    text = "⛽ ${bike.tankCapacity}L Tank",
                                                    fontSize = 11.sp,
                                                    color = palette.textPrimary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
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

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = MotoBookShapes.small,
                                color = palette.primary.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showSpecDetailsDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = palette.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📋 View Full Specs, Manual & Passport",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // AI Mechanic "Ask Anything" Assistant Card
                        AskAiMechanicCard(bike = bike)
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

                            val estFuel = bike.tankCapacity * 0.7f
                            val estRange = estFuel * (mileageStats.averageMileage ?: 40f)

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
                        // High Priority Urgent Maintenance Notification Banner
                        MaintenanceAlertsBanner(
                            currentOdometer = currentOdometer,
                            reminders = reminders,
                            onCompleteReminder = onCompleteReminder,
                            onNavigateToService = { onTabSelected(Screen.Maintenance.route) },
                            onAddReminderClick = { showAddReminderDialog = true }
                        )
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
                                        title = stringResource(id = R.string.fuel_cost_km),
                                        value = mileageStats.costPerKm,
                                        unit = "₹/km",
                                        accent = AmberPrimary
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

@Composable
fun AskAiMechanicCard(bike: Bike) {
    val palette = LocalThemePalette.current
    val coroutineScope = rememberCoroutineScope()

    var userQuery by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var diagResult by remember { mutableStateOf<BikeAiDiagnosticResult?>(null) }

    val quickQuestions = remember {
        listOf(
            "Engine overheating after 20km",
            "Drive chain rattling & slack",
            "Spongy front brake lever",
            "Cold start battery issue",
            "White smoke from exhaust"
        )
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = palette.primary.copy(alpha = 0.08f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🤖 ASK ANYTHING: AI MECHANIC & MANUAL ASSISTANT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Ask any issue or question regarding your ${bike.brand} ${bike.model}. AI analyzes your motorcycle's owner manual & online data sources:",
            fontSize = 11.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickQuestions.forEach { q ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.clickable {
                        userQuery = q
                        coroutineScope.launch {
                            isAnalyzing = true
                            val res = BikeSpecFetcher.askBikeAiAssistant(bike, q).getOrNull()
                            diagResult = res
                            isAnalyzing = false
                        }
                    }
                ) {
                    Text(
                        text = "💡 $q",
                        fontSize = 10.sp,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = userQuery,
            onValueChange = { userQuery = it },
            placeholder = { Text("Describe noise, symptom, or question...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = MotoBookShapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.primary,
                unfocusedBorderColor = GlassBorder,
                focusedContainerColor = palette.surface,
                unfocusedContainerColor = palette.surface.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (userQuery.isNotBlank()) {
                    coroutineScope.launch {
                        isAnalyzing = true
                        val res = BikeSpecFetcher.askBikeAiAssistant(bike, userQuery).getOrNull()
                        diagResult = res
                        isAnalyzing = false
                    }
                }
            },
            enabled = userQuery.isNotBlank() && !isAnalyzing,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primary,
                contentColor = Color.Black
            ),
            shape = MotoBookShapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing ${bike.brand} ${bike.model} Manual...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Analyze Issue with AI Manual Assistant", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Render AI Diagnostic Result
        diagResult?.let { res ->
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = MotoBookShapes.medium,
                color = palette.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSTIC REPORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )

                        // Safety Tag
                        val (bgColor, textColor, label) = when (res.safetyLevel) {
                            "CRITICAL" -> Triple(Color(0xFFD32F2F), Color.White, "CRITICAL HAZARD 🔴")
                            "CAUTION" -> Triple(Color(0xFFF57C00), Color.White, "CAUTION 🟡")
                            else -> Triple(Color(0xFF388E3C), Color.White, "SAFE OK 🟢")
                        }
                        Surface(
                            shape = CircleShape,
                            color = bgColor
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = res.summaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PROBABLE CAUSES:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    res.probableCauses.forEach { cause ->
                        Text(
                            text = "• $cause",
                            fontSize = 11.sp,
                            color = palette.textPrimary,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "OWNER MANUAL RECOMMENDED STEPS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary
                    )
                    res.manualFixSteps.forEach { step ->
                        Text(
                            text = step,
                            fontSize = 11.sp,
                            color = palette.textPrimary,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }

                    if (res.toolsNeeded.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "RECOMMENDED TOOLS / PARTS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            res.toolsNeeded.forEach { tool ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.primary.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "🔧 $tool",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

@Composable
fun BikeSpecDialog(
    bike: Bike,
    onDismiss: () -> Unit
) {
    val palette = LocalThemePalette.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = Color.Black)
            ) {
                Text("Close Passport", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = null,
                    tint = palette.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${bike.brand} ${bike.model}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = MotoBookShapes.small,
                    color = palette.primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("COLOR VARIANT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(bike.color.ifBlank { "N/A" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("MARKET ORIGIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(bike.countryOfOrigin.ifBlank { "Global" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        }
                    }
                }

                if (bike.manualUrl.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bike.manualUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MotoBookShapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📖 Open Official User Manual Online", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (bike.manualSummary.isNotBlank()) {
                    Surface(
                        shape = MotoBookShapes.small,
                        color = palette.surface.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "ANALYZED USER MANUAL GUIDE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bike.manualSummary,
                                fontSize = 11.sp,
                                color = palette.textPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                if (bike.maxPower.isNotBlank()) {
                    SpecRow(label = "Max Power / Performance", value = bike.maxPower)
                }

                SpecRow(
                    label = "Fuel Tank Capacity",
                    value = "${bike.tankCapacity} Liters (Reserve: ${bike.reserveCapacity} Liters)"
                )

                SpecRow(
                    label = "Recommended Tyre Pressure",
                    value = "Front: ${bike.frontTyrePressure.toInt()} PSI  |  Rear: ${bike.rearTyrePressure.toInt()} PSI"
                )

                if (bike.recommendedOilGrade.isNotBlank()) {
                    SpecRow(label = "Recommended Engine Oil", value = bike.recommendedOilGrade)
                }

                if (bike.maintenanceScheduleNote.isNotBlank()) {
                    Surface(
                        shape = MotoBookShapes.small,
                        color = palette.surface.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "MANUFACTURER SERVICE NOTES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bike.maintenanceScheduleNote,
                                fontSize = 12.sp,
                                color = palette.textPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = palette.surface,
        shape = MotoBookShapes.medium
    )
}

@Composable
private fun SpecRow(label: String, value: String) {
    val palette = LocalThemePalette.current
    Surface(
        shape = MotoBookShapes.small,
        color = palette.surface.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
        }
    }
}
