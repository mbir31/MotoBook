package com.example.motobook.presentation.service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*

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

    val selectedItems = remember {
        mutableStateListOf<String>().apply {
            if (existingEntry != null) addAll(existingEntry.itemsServiced)
        }
    }

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
                title = stringResource(id = R.string.add_service_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SERVICE CATEGORY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedToggle(
                    options = categories,
                    selectedIndex = categoryIndex,
                    onOptionSelected = { categoryIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DETAILS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

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
                    label = "Total Service Cost (৳) *",
                    keyboardType = KeyboardType.Decimal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items Serviced Multi-Select Chips
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ITEMS SERVICED (MULTI-SELECT)",
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
                                text = preset,
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
                    text = "SERVICE LOCATION",
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
                        Text(text = "Official", fontSize = 14.sp, color = palette.textPrimary)
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
                        Text(text = "Other Garage", fontSize = 14.sp, color = palette.textPrimary)
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
