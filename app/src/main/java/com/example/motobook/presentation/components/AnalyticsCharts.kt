package com.example.motobook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.MaintenanceReminder
import com.example.motobook.domain.model.MileageCycle
import com.example.motobook.domain.model.MileageStats
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class ChartPointData(
    val label: String,
    val valKmPerLiter: Float,
    val distanceKm: Float,
    val fuelLiters: Float,
    val costPerKm: Float,
    val dateMs: Long,
    val isSample: Boolean = false
)

@Composable
fun FuelEfficiencyTrendChartCard(
    mileageStats: MileageStats,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    // Prepare chart points from actual completed cycles, or sample data if empty
    val chartPoints = remember(mileageStats) {
        if (mileageStats.completedCycles.size >= 2) {
            mileageStats.completedCycles.map { cycle ->
                val totalCycleCost = (cycle.endEntry.totalCost + cycle.intermediateEntries.sumOf { it.totalCost.toDouble() }).toFloat()
                val computedCostPerKm = if (cycle.distanceKm > 0f) totalCycleCost / cycle.distanceKm else 0f

                ChartPointData(
                    label = "C${cycle.cycleNumber}",
                    valKmPerLiter = cycle.mileageKmPerLiter,
                    distanceKm = cycle.distanceKm,
                    fuelLiters = cycle.fuelUsedLiters,
                    costPerKm = computedCostPerKm,
                    dateMs = cycle.cycleEndDate,
                    isSample = false
                )
            }
        } else {
            // Realistic sample preview so user sees the trend visualization
            listOf(
                ChartPointData("C1", 38.5f, 320f, 8.3f, 3.2f, System.currentTimeMillis() - 864000000L, true),
                ChartPointData("C2", 42.0f, 350f, 8.3f, 2.9f, System.currentTimeMillis() - 691200000L, true),
                ChartPointData("C3", 40.2f, 330f, 8.2f, 3.1f, System.currentTimeMillis() - 518400000L, true),
                ChartPointData("C4", 45.8f, 380f, 8.3f, 2.7f, System.currentTimeMillis() - 345600000L, true),
                ChartPointData("C5", 44.1f, 365f, 8.2f, 2.8f, System.currentTimeMillis() - 172800000L, true)
            )
        }
    }

    val isSampleMode = chartPoints.any { it.isSample }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    val values = chartPoints.map { it.valKmPerLiter }
    val minVal = (values.minOrNull() ?: 30f) * 0.9f
    val maxVal = (values.maxOrNull() ?: 50f) * 1.1f
    val avgVal = if (values.isNotEmpty()) values.average().toFloat() else 40f

    val latestKmPerL = values.lastOrNull() ?: 0f
    val prevKmPerL = if (values.size > 1) values[values.size - 2] else latestKmPerL
    val trendDiff = latestKmPerL - prevKmPerL

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FUEL EFFICIENCY TREND (km/L)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }
                if (isSampleMode) {
                    Text(
                        text = "Preview Mode • Log refuels to track live trends",
                        fontSize = 11.sp,
                        color = AmberPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (trendDiff != 0f) {
                Surface(
                    color = (if (trendDiff >= 0) EmeraldPrimary else RedPrimary).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        (if (trendDiff >= 0) EmeraldPrimary else RedPrimary).copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (trendDiff >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (trendDiff >= 0) EmeraldPrimary else RedPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%+.1f km/L", trendDiff),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trendDiff >= 0) EmeraldPrimary else RedPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart Canvas Area
        val primaryColor = palette.primary
        val gridLineColor = GlassBorder.copy(alpha = 0.4f)
        val linePathColor = palette.primary
        val pointDotColor = palette.primary

        var calculatedOffsets by remember { mutableStateOf<List<Offset>>(emptyList()) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(palette.surface.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(chartPoints) {
                        detectTapGestures { tapOffset ->
                            if (calculatedOffsets.isNotEmpty()) {
                                var closestIndex = -1
                                var minDistance = Float.MAX_VALUE
                                calculatedOffsets.forEachIndexed { index, pointOffset ->
                                    val dist = (pointOffset - tapOffset).getDistance()
                                    if (dist < minDistance && dist < 60f) {
                                        minDistance = dist
                                        closestIndex = index
                                    }
                                }
                                selectedPointIndex = if (closestIndex != -1) closestIndex else null
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingX = 30f
                val paddingY = 30f
                val usableWidth = width - (paddingX * 2)
                val usableHeight = height - (paddingY * 2)

                // 1. Draw horizontal grid lines (min, avg, max)
                val range = (maxVal - minVal).coerceAtLeast(1f)
                val avgY = height - paddingY - ((avgVal - minVal) / range * usableHeight)

                // Dashed line for average
                val dashPath = Path().apply {
                    moveTo(paddingX, avgY)
                    lineTo(width - paddingX, avgY)
                }
                drawPath(
                    path = dashPath,
                    color = primaryColor.copy(alpha = 0.4f),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )

                // 2. Map data points to canvas Offsets
                val offsets = mutableListOf<Offset>()
                val stepX = if (chartPoints.size > 1) usableWidth / (chartPoints.size - 1) else usableWidth / 2

                chartPoints.forEachIndexed { i, point ->
                    val x = paddingX + (i * stepX)
                    val y = height - paddingY - ((point.valKmPerLiter - minVal) / range * usableHeight)
                    offsets.add(Offset(x, y))
                }
                calculatedOffsets = offsets

                if (offsets.size >= 2) {
                    // 3. Build smooth bezier path
                    val path = Path().apply {
                        moveTo(offsets[0].x, offsets[0].y)
                        for (i in 0 until offsets.size - 1) {
                            val p0 = offsets[i]
                            val p1 = offsets[i + 1]
                            val controlX1 = p0.x + (p1.x - p0.x) / 2
                            val controlY1 = p0.y
                            val controlX2 = p0.x + (p1.x - p0.x) / 2
                            val controlY2 = p1.y
                            cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                        }
                    }

                    // 4. Draw area gradient fill under path
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(offsets.last().x, height - paddingY)
                        lineTo(offsets.first().x, height - paddingY)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f),
                                primaryColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = paddingY,
                            endY = height - paddingY
                        )
                    )

                    // 5. Draw main line stroke
                    drawPath(
                        path = path,
                        color = linePathColor,
                        style = Stroke(width = 4f)
                    )
                }

                // 6. Draw point dots and labels
                offsets.forEachIndexed { index, offset ->
                    val isSelected = selectedPointIndex == index
                    val dotRadius = if (isSelected) 8f else 5f

                    // Outer glow for selected or all
                    drawCircle(
                        color = if (isSelected) AmberPrimary else primaryColor.copy(alpha = 0.3f),
                        radius = dotRadius * 1.8f,
                        center = offset
                    )
                    drawCircle(
                        color = if (isSelected) AmberPrimary else pointDotColor,
                        radius = dotRadius,
                        center = offset
                    )
                    drawCircle(
                        color = Color.White,
                        radius = dotRadius * 0.4f,
                        center = offset
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Point Detail Tooltip Overlay
        val selectedIndex = selectedPointIndex
        if (selectedIndex != null && selectedIndex in chartPoints.indices) {
            val point = chartPoints[selectedIndex]
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = palette.surface.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.5f)),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cycle ${point.label} • ${dateFormat.format(Date(point.dateMs))}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                        Text(
                            text = "${String.format("%.1f", point.valKmPerLiter)} km/L",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Distance: ${point.distanceKm.toInt()} km  |  Fuel: ${String.format("%.1f", point.fuelLiters)} L",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (point.costPerKm > 0f) {
                            Text(
                                text = "৳ ${String.format("%.2f", point.costPerKm)}/km",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        } else {
            // Default Legend / Summary bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap any node for details",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Min: ${String.format("%.1f", values.minOrNull() ?: 0f)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = "Avg: ${String.format("%.1f", avgVal)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary
                    )
                    Text(
                        text = "Max: ${String.format("%.1f", values.maxOrNull() ?: 0f)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}

data class ServiceIntervalItem(
    val title: String,
    val icon: String,
    val lastDoneKm: Float,
    val intervalKm: Float,
    val currentKm: Float
)

@Composable
fun ServiceIntervalsChartCard(
    serviceEntries: List<ServiceEntry>,
    reminders: List<MaintenanceReminder>,
    currentOdometer: Float?,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    val currentOdo = currentOdometer ?: 0f

    // Calculate or compile interval health for key motorcycle components
    val intervalItems = remember(serviceEntries, reminders, currentOdo) {
        val oilLast = reminders.find { it.title.contains("Engine Oil", ignoreCase = true) }?.lastDoneOdometer
            ?: serviceEntries.filter { it.category == "ENGINE_OIL" || it.itemsServiced.any { item -> item.contains("Engine Oil", ignoreCase = true) } }
                .maxOfOrNull { it.odometer } ?: 0f

        val filterLast = reminders.find { it.title.contains("Air Filter", ignoreCase = true) }?.lastDoneOdometer
            ?: serviceEntries.filter { it.itemsServiced.any { item -> item.contains("Air Filter", ignoreCase = true) } }
                .maxOfOrNull { it.odometer } ?: 0f

        val chainLast = reminders.find { it.title.contains("Chain", ignoreCase = true) }?.lastDoneOdometer ?: 0f
        val sparkLast = reminders.find { it.title.contains("Spark Plug", ignoreCase = true) }?.lastDoneOdometer ?: 0f

        listOf(
            ServiceIntervalItem("Engine Oil", "🛢️", oilLast, 3000f, currentOdo),
            ServiceIntervalItem("Chain Lube", "⚙️", chainLast, 500f, currentOdo),
            ServiceIntervalItem("Air Filter", "💨", filterLast, 6000f, currentOdo),
            ServiceIntervalItem("Spark Plug", "⚡", sparkLast, 10000f, currentOdo)
        )
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SERVICE INTERVALS & COMPONENT HEALTH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            if (currentOdo > 0f) {
                Text(
                    text = "${currentOdo.toInt()} km",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            intervalItems.forEach { item ->
                val distanceDriven = (item.currentKm - item.lastDoneKm).coerceAtLeast(0f)
                val progress = (distanceDriven / item.intervalKm).coerceIn(0f, 1f)
                val remainingKm = (item.intervalKm - distanceDriven).coerceAtLeast(0f)

                val statusColor = when {
                    progress >= 0.9f -> RedPrimary
                    progress >= 0.7f -> AmberPrimary
                    else -> EmeraldPrimary
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surface.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }

                        Text(
                            text = if (progress >= 1.0f) "Due Now!" else "~${remainingKm.toInt()} km left",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Interval Bar Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(palette.surface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(statusColor.copy(alpha = 0.7f), statusColor)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Last done: ${item.lastDoneKm.toInt()} km",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Interval: ${item.intervalKm.toInt()} km",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
