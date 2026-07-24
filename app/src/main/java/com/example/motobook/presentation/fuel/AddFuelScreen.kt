package com.example.motobook.presentation.fuel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    var refuelTypeIndex by remember {
        mutableIntStateOf(if (existingEntry?.refuelType == "PARTIAL") 1 else 0)
    }
    val refuelTypes = listOf("FULL TANK", "PARTIAL")

    var selectedDateMillis by remember { mutableLongStateOf(existingEntry?.date ?: System.currentTimeMillis()) }
    var odometer by remember { mutableStateOf(existingEntry?.odometer?.toInt()?.toString() ?: "") }
    var quantity by remember { mutableStateOf(existingEntry?.fuelQuantity?.toString() ?: "") }
    var pricePerLiter by remember { mutableStateOf(existingEntry?.pricePerLiter?.toString() ?: "105") }
    var station by remember { mutableStateOf(existingEntry?.fuelStation ?: "") }
    var notes by remember { mutableStateOf(existingEntry?.notes ?: "") }

    val qtyVal = quantity.toFloatOrNull() ?: 0f
    val priceVal = pricePerLiter.toFloatOrNull() ?: 0f
    val calculatedTotalCost = qtyVal * priceVal

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDateMillis))

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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            MotoTopBar(
                title = if (existingEntry != null) stringResource(id = R.string.edit_fuel_title)
                else stringResource(id = R.string.add_fuel_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fill Type Segmented Toggle
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FILL TYPE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedToggle(
                    options = refuelTypes,
                    selectedIndex = refuelTypeIndex,
                    onOptionSelected = { refuelTypeIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fuel Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ENTRY DETAILS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date: $formattedDate",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    IconButton(onClick = { /* Date picker triggered */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pick Date",
                            tint = palette.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = stringResource(id = R.string.odometer),
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = stringResource(id = R.string.fuel_quantity),
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = pricePerLiter,
                            onValueChange = { pricePerLiter = it },
                            label = stringResource(id = R.string.price_per_liter),
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculated Cost Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL COST",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "৳ ${String.format("%.2f", calculatedTotalCost)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optional Card
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
                    onSaveClick(
                        existingEntry?.fuelId ?: 0L,
                        selectedDateMillis,
                        odometer,
                        quantity,
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
