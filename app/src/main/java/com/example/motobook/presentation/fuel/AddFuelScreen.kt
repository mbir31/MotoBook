package com.example.motobook.presentation.fuel

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
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
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddFuelScreen(
    existingEntry: FuelEntry? = null,
    latestOdometer: Float? = null,
    onSaveClick: (
        fuelId: Long,
        date: Long,
        odometer: String,
        quantity: String,
        price: String,
        refuelType: String,
        station: String?,
        notes: String?
    ) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var fuelTypeIndex by remember {
        mutableIntStateOf(if (existingEntry?.pricePerLiter == 140f) 1 else 0)
    }
    val fuelTypes = listOf("Octane (৳145/L)", "Petrol (৳140/L)")

    var refuelTypeIndex by remember {
        mutableIntStateOf(if (existingEntry?.refuelType == "PARTIAL") 1 else 0)
    }
    val refuelTypes = listOf(
        stringResource(id = R.string.full_tank),
        stringResource(id = R.string.partial_tank)
    )

    // Mode 0 = Enter Total Cost (৳), Mode 1 = Enter Fuel Volume (L)
    var calculationModeIndex by remember { mutableIntStateOf(0) }
    val entryModes = listOf("Total Cost (৳)", "Volume (Liters)")

    var selectedDateMillis by remember { mutableLongStateOf(existingEntry?.date ?: System.currentTimeMillis()) }
    var odometer by remember {
        mutableStateOf(
            existingEntry?.odometer?.toInt()?.toString()
                ?: (latestOdometer?.toInt()?.toString() ?: "")
        )
    }

    var totalCostInput by remember {
        mutableStateOf(
            existingEntry?.totalCost?.let {
                if (it % 1f == 0f) it.toInt().toString() else String.format("%.2f", it)
            } ?: "500"
        )
    }

    var quantityInput by remember {
        mutableStateOf(
            existingEntry?.fuelQuantity?.let {
                if (it % 1f == 0f) it.toInt().toString() else it.toString()
            } ?: ""
        )
    }

    var pricePerLiter by remember {
        mutableStateOf(
            existingEntry?.pricePerLiter?.let {
                if (it % 1f == 0f) it.toInt().toString() else it.toString()
            } ?: "145"
        )
    }

    var station by remember { mutableStateOf(existingEntry?.fuelStation ?: "") }
    var notes by remember { mutableStateOf(existingEntry?.notes ?: "") }

    val priceVal = pricePerLiter.toFloatOrNull() ?: 145f
    val costVal = totalCostInput.toFloatOrNull() ?: 0f
    val qtyVal = quantityInput.toFloatOrNull() ?: 0f

    // Calculated values depending on mode
    val computedQuantity = if (priceVal > 0f) costVal / priceVal else 0f
    val computedTotalCost = qtyVal * priceVal

    val context = LocalContext.current
    val palette = LocalThemePalette.current

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val formattedDate = remember(selectedDateMillis) {
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            MotoTopBar(
                title = if (existingEntry != null) stringResource(id = R.string.edit_fuel_title)
                else stringResource(id = R.string.add_fuel_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Latest Odometer Reference Banner
            if (latestOdometer != null && latestOdometer > 0f) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (odometer.isBlank()) {
                                odometer = latestOdometer.toInt().toString()
                            }
                        },
                    color = palette.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LATEST ODOMETER: ${latestOdometer.toInt()} km",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        }
                        Text(
                            text = if (odometer.isNotBlank()) "Checked ✓" else "Tap to Use",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Fuel Preset Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FUEL TYPE & PRICE PRESET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedToggle(
                    options = fuelTypes,
                    selectedIndex = fuelTypeIndex,
                    onOptionSelected = { index ->
                        fuelTypeIndex = index
                        pricePerLiter = if (index == 0) "145" else "140"
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Entry Method Segmented Toggle: By Total Cost vs By Liters
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "HOW DO YOU WANT TO ENTER FUEL?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedToggle(
                    options = entryModes,
                    selectedIndex = calculationModeIndex,
                    onOptionSelected = { calculationModeIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fuel Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.entry_details_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Date Picker Button
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
                                text = stringResource(id = R.string.date),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formattedDate,
                                fontSize = 15.sp,
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

                // Odometer field
                MotoTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = "Odometer Reading (km)",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mode-dependent fields
                if (calculationModeIndex == 0) {
                    // Enter Total Cost & Price Per Liter
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            MotoTextField(
                                value = totalCostInput,
                                onValueChange = { totalCostInput = it },
                                label = "Total Cost (৳)",
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MotoTextField(
                                value = pricePerLiter,
                                onValueChange = { pricePerLiter = it },
                                label = "Price / Liter (৳)",
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }
                } else {
                    // Enter Fuel Quantity (Liters) & Price Per Liter
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            MotoTextField(
                                value = quantityInput,
                                onValueChange = { quantityInput = it },
                                label = "Fuel Quantity (Liters)",
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MotoTextField(
                                value = pricePerLiter,
                                onValueChange = { pricePerLiter = it },
                                label = "Price / Liter (৳)",
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tank Fill Type (Full vs Partial)
                Text(
                    text = "TANK FILL TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                SegmentedToggle(
                    options = refuelTypes,
                    selectedIndex = refuelTypeIndex,
                    onOptionSelected = { refuelTypeIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Result Summary Callout
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.15f)
            ) {
                if (calculationModeIndex == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "COMPUTED FUEL VOLUME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Total ৳${costVal.toInt()} ÷ ৳${priceVal.toInt()}/L",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "${String.format("%.2f", computedQuantity)} Liters",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "COMPUTED TOTAL REFILL COST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "${qtyVal} L × ৳${priceVal.toInt()}/L",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "৳ ${String.format("%.2f", computedTotalCost)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Optional Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "OPTIONAL DETAILS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                MotoTextField(
                    value = station,
                    onValueChange = { station = it },
                    label = stringResource(id = R.string.station_name)
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = stringResource(id = R.string.notes)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            GlowButton(
                text = stringResource(id = R.string.btn_save_fuel),
                onClick = {
                    val finalQtyStr = if (calculationModeIndex == 0) {
                        String.format("%.2f", computedQuantity)
                    } else {
                        quantityInput
                    }

                    onSaveClick(
                        existingEntry?.fuelId ?: 0L,
                        selectedDateMillis,
                        odometer,
                        finalQtyStr,
                        pricePerLiter,
                        if (refuelTypeIndex == 0) "FULL" else "PARTIAL",
                        station,
                        notes
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
