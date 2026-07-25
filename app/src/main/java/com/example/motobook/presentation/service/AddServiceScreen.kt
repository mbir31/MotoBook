package com.example.motobook.presentation.service

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddServiceScreen(
    existingEntry: ServiceEntry? = null,
    onSaveClick: (
        serviceId: Long,
        date: Long,
        odometer: String,
        category: String,
        itemsServiced: List<String>,
        isOfficial: Boolean,
        centerName: String?,
        totalCost: String,
        notes: String?
    ) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var categoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("REGULAR_SERVICE", "TYRE", "REPAIR", "WASH", "CHAIN")
    val categoryDisplayNames = listOf(
        stringResource(id = R.string.regular_service),
        stringResource(id = R.string.nav_fuel),
        stringResource(id = R.string.tyre_pressure),
        stringResource(id = R.string.bike_wash),
        stringResource(id = R.string.chain_lube)
    )

    var selectedDateMillis by remember { mutableLongStateOf(existingEntry?.date ?: System.currentTimeMillis()) }
    var odometer by remember { mutableStateOf(existingEntry?.odometer?.toInt()?.toString() ?: "") }
    var isOfficial by remember { mutableStateOf(existingEntry?.isOfficialServiceCenter ?: true) }
    var centerName by remember { mutableStateOf(existingEntry?.serviceCenterName ?: "") }
    var totalCost by remember { mutableStateOf(existingEntry?.totalCost?.toInt()?.toString() ?: "") }
    var notes by remember { mutableStateOf(existingEntry?.notes ?: "") }

    val presetItems = listOf(
        "Engine Oil", "Oil Filter", "Air Filter", "Spark Plug",
        "Front Brake Pad", "Rear Brake Pad", "Brake Fluid", "Coolant",
        "Chain & Sprocket", "Battery", "Front Tyre", "Rear Tyre", "Clutch Plate"
    )

    val presetItemLabels = mapOf(
        "Engine Oil" to stringResource(id = R.string.item_engine_oil),
        "Oil Filter" to stringResource(id = R.string.item_oil_filter),
        "Air Filter" to stringResource(id = R.string.item_air_filter),
        "Spark Plug" to stringResource(id = R.string.item_spark_plug),
        "Front Brake Pad" to stringResource(id = R.string.item_front_brake_pad),
        "Rear Brake Pad" to stringResource(id = R.string.item_rear_brake_pad),
        "Brake Fluid" to stringResource(id = R.string.item_brake_fluid),
        "Coolant" to stringResource(id = R.string.item_coolant),
        "Chain & Sprocket" to stringResource(id = R.string.item_chain_sprocket),
        "Battery" to stringResource(id = R.string.item_battery),
        "Front Tyre" to stringResource(id = R.string.item_front_tyre),
        "Rear Tyre" to stringResource(id = R.string.item_rear_tyre),
        "Clutch Plate" to stringResource(id = R.string.item_clutch_plate)
    )

    val selectedItems = remember {
        mutableStateListOf<String>().apply {
            if (existingEntry != null) addAll(existingEntry.itemsServiced)
        }
    }

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
                title = stringResource(id = R.string.add_service_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.service_category_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedToggle(
                    options = categoryDisplayNames,
                    selectedIndex = categoryIndex,
                    onOptionSelected = { categoryIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.details_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

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

                MotoTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = stringResource(id = R.string.odometer),
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = totalCost,
                    onValueChange = { totalCost = it },
                    label = stringResource(id = R.string.total_service_cost),
                    keyboardType = KeyboardType.Decimal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items Serviced Multi-Select Chips
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.preset_items_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetItems.forEach { preset ->
                        val isSelected = selectedItems.contains(preset)
                        val chipBg = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f)
                        val contentColor = if (isSelected) TextOnAccent else TextSecondary

                        Surface(
                            color = chipBg,
                            shape = MotoBookShapes.small,
                            modifier = Modifier.clickable {
                                if (isSelected) selectedItems.remove(preset)
                                else selectedItems.add(preset)
                            }
                        ) {
                            Text(
                                text = presetItemLabels[preset] ?: preset,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = contentColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location & Center Name Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.service_center),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isOfficial = true }
                    ) {
                        RadioButton(
                            selected = isOfficial,
                            onClick = { isOfficial = true },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                        )
                        Text(
                            text = stringResource(id = R.string.official_center),
                            fontSize = 14.sp,
                            color = palette.textPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isOfficial = false }
                    ) {
                        RadioButton(
                            selected = !isOfficial,
                            onClick = { isOfficial = false },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                        )
                        Text(
                            text = stringResource(id = R.string.other_garage),
                            fontSize = 14.sp,
                            color = palette.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = centerName,
                    onValueChange = { centerName = it },
                    label = stringResource(id = R.string.service_center)
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
                text = stringResource(id = R.string.btn_save_service),
                onClick = {
                    onSaveClick(
                        existingEntry?.serviceId ?: 0L,
                        selectedDateMillis,
                        odometer,
                        categories[categoryIndex],
                        selectedItems.toList(),
                        isOfficial,
                        centerName,
                        totalCost,
                        notes
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
