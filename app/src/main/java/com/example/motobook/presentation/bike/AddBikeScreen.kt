package com.example.motobook.presentation.bike

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.motobook.domain.model.Bike
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*

@Composable
fun AddBikeScreen(
    existingBike: Bike? = null,
    onSaveClick: (
        bikeId: Long,
        bikeName: String,
        brand: String,
        model: String,
        year: String,
        registrationNumber: String,
        fuelType: String,
        tankCapacity: String,
        reserveCapacity: String,
        frontPsi: String,
        rearPsi: String
    ) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var bikeName by remember { mutableStateOf(existingBike?.bikeName ?: "") }
    var brand by remember { mutableStateOf(existingBike?.brand ?: "") }
    var model by remember { mutableStateOf(existingBike?.model ?: "") }
    var year by remember { mutableStateOf(existingBike?.year?.toString() ?: "2023") }
    var regNo by remember { mutableStateOf(existingBike?.registrationNumber ?: "") }
    var selectedFuelTypeIndex by remember {
        mutableIntStateOf(
            when (existingBike?.fuelType?.uppercase()) {
                "PETROL" -> 1
                else -> 0
            }
        )
    }
    val fuelTypes = listOf("Octane", "Petrol")

    var tankCapacity by remember { mutableStateOf(existingBike?.tankCapacity?.toString() ?: "12") }
    var reserveCapacity by remember { mutableStateOf(existingBike?.reserveCapacity?.toString() ?: "2") }
    var frontPsi by remember { mutableStateOf(existingBike?.frontTyrePressure?.toString() ?: "28") }
    var rearPsi by remember { mutableStateOf(existingBike?.rearTyrePressure?.toString() ?: "32") }

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
                title = if (existingBike != null) stringResource(id = R.string.edit_bike_title)
                else stringResource(id = R.string.add_bike_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Basic Information Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.basic_info_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                MotoTextField(
                    value = bikeName,
                    onValueChange = { bikeName = it },
                    label = stringResource(id = R.string.bike_nickname)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = stringResource(id = R.string.brand)
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = stringResource(id = R.string.model)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = stringResource(id = R.string.year),
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = regNo,
                            onValueChange = { regNo = it },
                            label = stringResource(id = R.string.registration_no)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fuel Configuration Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.fuel_config_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(id = R.string.fuel_type),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                SegmentedToggle(
                    options = fuelTypes,
                    selectedIndex = selectedFuelTypeIndex,
                    onOptionSelected = { selectedFuelTypeIndex = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = tankCapacity,
                            onValueChange = { tankCapacity = it },
                            label = stringResource(id = R.string.tank_capacity),
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = reserveCapacity,
                            onValueChange = { reserveCapacity = it },
                            label = stringResource(id = R.string.reserve_capacity),
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tyre Pressure Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.tyre_setup_header),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = frontPsi,
                            onValueChange = { frontPsi = it },
                            label = stringResource(id = R.string.front_psi),
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = rearPsi,
                            onValueChange = { rearPsi = it },
                            label = stringResource(id = R.string.rear_psi),
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            GlowButton(
                text = stringResource(id = R.string.btn_save_bike),
                onClick = {
                    onSaveClick(
                        existingBike?.bikeId ?: 0L,
                        bikeName,
                        brand,
                        model,
                        year,
                        regNo,
                        fuelTypes[selectedFuelTypeIndex],
                        tankCapacity,
                        reserveCapacity,
                        frontPsi,
                        rearPsi
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MotoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 13.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = palette.surface.copy(alpha = 0.5f),
            unfocusedContainerColor = palette.surface.copy(alpha = 0.3f),
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = GlassBorder,
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary
        ),
        shape = MotoBookShapes.small,
        modifier = modifier.fillMaxWidth()
    )
}
