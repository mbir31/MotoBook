package com.example.motobook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.domain.model.WashEntry
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthlyExpenditureCard(
    fuelEntries: List<FuelEntry>,
    serviceEntries: List<ServiceEntry>,
    washEntries: List<WashEntry>,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    var monthOffset by remember { mutableIntStateOf(0) } // 0 is current month, -1 is last month, etc.

    val targetCalendar = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
        }
    }

    val displayMonthName = remember(targetCalendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(targetCalendar.time)
    }

    val targetYear = targetCalendar.get(Calendar.YEAR)
    val targetMonth = targetCalendar.get(Calendar.MONTH)

    val monthCal = Calendar.getInstance()

    val fuelCost = remember(fuelEntries, monthOffset) {
        fuelEntries.filter {
            monthCal.timeInMillis = it.date
            monthCal.get(Calendar.YEAR) == targetYear && monthCal.get(Calendar.MONTH) == targetMonth
        }.sumOf { it.totalCost.toDouble() }.toFloat()
    }

    val serviceCost = remember(serviceEntries, monthOffset) {
        serviceEntries.filter {
            monthCal.timeInMillis = it.date
            monthCal.get(Calendar.YEAR) == targetYear && monthCal.get(Calendar.MONTH) == targetMonth
        }.sumOf { it.totalCost.toDouble() }.toFloat()
    }

    val washCost = remember(washEntries, monthOffset) {
        washEntries.filter {
            monthCal.timeInMillis = it.date
            monthCal.get(Calendar.YEAR) == targetYear && monthCal.get(Calendar.MONTH) == targetMonth
        }.sumOf { (it.cost ?: 0f).toDouble() }.toFloat()
    }

    val totalSpent = fuelCost + serviceCost + washCost

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Month Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(palette.primary.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MONTHLY EXPENDITURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { monthOffset-- },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = palette.textPrimary
                        )
                    }
                    Text(
                        text = displayMonthName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { monthOffset++ },
                        enabled = monthOffset < 0,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = if (monthOffset < 0) palette.textPrimary else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total Spend Hero Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = palette.surface.copy(alpha = 0.5f),
                shape = MotoBookShapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Spent in $displayMonthName",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "৳ ${totalSpent.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    }

                    if (totalSpent > 0f) {
                        Surface(
                            color = palette.primary.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Recorded",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Itemized Breakdown
            if (totalSpent == 0f) {
                Text(
                    text = "No expenses recorded for $displayMonthName.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpenseProgressRow(
                        label = "Fuel (Octane/Petrol)",
                        amount = fuelCost,
                        total = totalSpent,
                        color = AmberPrimary,
                        icon = Icons.Default.LocalGasStation
                    )

                    ExpenseProgressRow(
                        label = "Services & Maintenance",
                        amount = serviceCost,
                        total = totalSpent,
                        color = palette.primary,
                        icon = Icons.Default.Build
                    )

                    ExpenseProgressRow(
                        label = "Wash & Detailing",
                        amount = washCost,
                        total = totalSpent,
                        color = PurplePrimary,
                        icon = Icons.Default.WaterDrop
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseProgressRow(
    label: String,
    amount: Float,
    total: Float,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val palette = LocalThemePalette.current
    val fraction = if (total > 0f) (amount / total).coerceIn(0f, 1f) else 0f
    val percentage = (fraction * 100).toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
            }
            Text(
                text = "৳ ${amount.toInt()} ($percentage%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
