package com.example.motobook.presentation.bike

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.motobook.data.remote.BikeSpecFetcher
import com.example.motobook.domain.model.Bike
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*
import kotlinx.coroutines.launch

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
        rearPsi: String,
        color: String,
        engineCc: String,
        maxPower: String,
        recommendedOilGrade: String,
        maintenanceScheduleNote: String,
        countryOfOrigin: String,
        manualUrl: String,
        manualSummary: String,
        bikeImagePath: String?
    ) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val palette = LocalThemePalette.current
    val context = LocalContext.current

    // Setup Wizard Steps: 1 = Country & Brand, 2 = Model, 3 = Variant & Color, 4 = Review & Manual Profile
    var wizardStep by remember { mutableIntStateOf(if (existingBike != null) 4 else 1) }

    // Form fields
    var bikeName by remember { mutableStateOf(existingBike?.bikeName ?: "") }
    var brand by remember { mutableStateOf(existingBike?.brand ?: "") }
    var model by remember { mutableStateOf(existingBike?.model ?: "") }
    var year by remember { mutableStateOf(existingBike?.year?.toString() ?: "2023") }
    var regNo by remember { mutableStateOf(existingBike?.registrationNumber ?: "") }
    var selectedFuelTypeIndex by remember {
        mutableIntStateOf(if (existingBike?.fuelType?.uppercase() == "PETROL") 1 else 0)
    }
    val fuelTypes = listOf("Octane", "Petrol")

    var tankCapacity by remember { mutableStateOf(existingBike?.tankCapacity?.toString() ?: "12") }
    var reserveCapacity by remember { mutableStateOf(existingBike?.reserveCapacity?.toString() ?: "2") }
    var frontPsi by remember { mutableStateOf(existingBike?.frontTyrePressure?.toString() ?: "28") }
    var rearPsi by remember { mutableStateOf(existingBike?.rearTyrePressure?.toString() ?: "32") }

    var color by remember { mutableStateOf(existingBike?.color ?: "Racing Blue") }
    var selectedVariant by remember { mutableStateOf("Standard ABS") }
    var engineCc by remember { mutableStateOf(existingBike?.engineCc?.toInt()?.toString() ?: "150") }
    var maxPower by remember { mutableStateOf(existingBike?.maxPower ?: "18.4 PS @ 10,000 rpm") }
    var recommendedOilGrade by remember { mutableStateOf(existingBike?.recommendedOilGrade ?: "10W-40 Full Synthetic (1.0 L)") }
    var maintenanceScheduleNote by remember { mutableStateOf(existingBike?.maintenanceScheduleNote ?: "Service every 3,000 km. Chain lube every 500 km.") }

    var countryOfOrigin by remember { mutableStateOf(existingBike?.countryOfOrigin ?: "Bangladesh") }
    var manualUrl by remember { mutableStateOf(existingBike?.manualUrl ?: "") }
    var manualSummary by remember { mutableStateOf(existingBike?.manualSummary ?: "") }
    var bikeImagePath by remember { mutableStateOf<String?>(existingBike?.bikeImagePath) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            bikeImagePath = it.toString()
        }
    }

    // Dynamic Online Fetched Lists
    var modelsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var variantsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var colorsList by remember { mutableStateOf<List<String>>(emptyList()) }

    // Statuses & Loading Flags
    var isLoadingModels by remember { mutableStateOf(false) }
    var isLoadingVariants by remember { mutableStateOf(false) }
    var isLoadingFullSpecs by remember { mutableStateOf(false) }
    var onlineStatusMsg by remember { mutableStateOf<String?>(null) }
    var isSuccessFetch by remember { mutableStateOf(false) }

    val popularCountries = remember {
        listOf("Bangladesh 🇧🇩", "India 🇮🇳", "Japan 🇯🇵", "USA 🇺🇸", "UK 🇬🇧", "Germany 🇩🇪", "Italy 🇮🇹", "Thailand 🇹🇭", "Indonesia 🇮🇩", "Brazil 🇧🇷", "Global 🌍")
    }

    val popularBrands = remember {
        listOf(
            "Yamaha", "Honda", "Suzuki", "KTM", "Royal Enfield", "TVS", "Bajaj",
            "Kawasaki", "BMW", "Ducati", "Triumph", "Hero", "Runner", "Vespa"
        )
    }

    fun selectBrandAndFetchModels(selectedBrand: String) {
        brand = selectedBrand
        wizardStep = 2
        coroutineScope.launch {
            isLoadingModels = true
            onlineStatusMsg = "🔍 Searching official models for $selectedBrand in $countryOfOrigin..."
            modelsList = BikeSpecFetcher.fetchModelsForBrand(selectedBrand, countryOfOrigin)
            isLoadingModels = false
            onlineStatusMsg = "Fetched ${modelsList.size} $selectedBrand models for market: $countryOfOrigin"
        }
    }

    fun selectModelAndFetchVariants(selectedModel: String) {
        model = selectedModel
        wizardStep = 3
        coroutineScope.launch {
            isLoadingVariants = true
            onlineStatusMsg = "🔍 Fetching $countryOfOrigin editions & colors for $brand $selectedModel..."
            val (vList, cList) = BikeSpecFetcher.fetchVariantsAndColors(brand, selectedModel, countryOfOrigin)
            variantsList = vList
            colorsList = cList
            if (cList.isNotEmpty()) { color = cList.first() }
            if (vList.isNotEmpty()) { selectedVariant = vList.first() }
            isLoadingVariants = false
            onlineStatusMsg = "Fetched variants and colors for $brand $selectedModel ($countryOfOrigin edition)"
        }
    }

    fun fetchAndAutoFillFullProfile() {
        coroutineScope.launch {
            isLoadingFullSpecs = true
            onlineStatusMsg = "⚡ Downloading $countryOfOrigin profile, manual & photo for $brand $model..."
            isSuccessFetch = false

            val currentYearInt = year.toIntOrNull() ?: 2023
            val result = BikeSpecFetcher.fetchBikeSpecsOnline(brand, model, currentYearInt, color, countryOfOrigin)

            result.onSuccess { spec ->
                brand = spec.brand
                model = spec.model
                if (bikeName.isBlank() || bikeName == "My Bike") {
                    bikeName = "${spec.brand} ${spec.model}"
                }
                tankCapacity = spec.tankCapacity.toString()
                reserveCapacity = spec.reserveCapacity.toString()
                frontPsi = spec.frontTyrePressure.toInt().toString()
                rearPsi = spec.rearTyrePressure.toInt().toString()
                selectedFuelTypeIndex = if (spec.fuelType.uppercase() == "PETROL") 1 else 0

                color = spec.color
                countryOfOrigin = spec.countryOfOrigin
                engineCc = spec.engineCc.toInt().toString()
                maxPower = spec.maxPower
                recommendedOilGrade = spec.recommendedOilGrade
                maintenanceScheduleNote = spec.maintenanceScheduleNote
                manualUrl = spec.manualUrl
                manualSummary = spec.manualSummary

                // Default online photo assignment if no custom image was set
                if (bikeImagePath.isNullOrBlank() || bikeImagePath?.startsWith("http") == true) {
                    bikeImagePath = spec.imageUrl
                }

                if (spec.availableColors.isNotEmpty()) {
                    colorsList = spec.availableColors
                }

                isLoadingFullSpecs = false
                isSuccessFetch = true
                onlineStatusMsg = "🎉 Auto-filled bike profile, user manual summary & official image for ${spec.brand} ${spec.model}!"
                wizardStep = 4 // Advance to Review & Edit Step
            }.onFailure { err ->
                isLoadingFullSpecs = false
                isSuccessFetch = false
                onlineStatusMsg = "Could not fetch specs: ${err.message}"
                wizardStep = 4
            }
        }
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
                title = if (existingBike != null) stringResource(id = R.string.edit_bike_title)
                else stringResource(id = R.string.add_bike_title),
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Header
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val steps = listOf("1. Market/Brand", "2. Model", "3. Color", "4. Manual Profile")
                    steps.forEachIndexed { idx, stepName ->
                        val stepNumber = idx + 1
                        val isActive = wizardStep == stepNumber
                        val isPassed = wizardStep > stepNumber

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isActive -> palette.primary
                                isPassed -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                else -> palette.surface.copy(alpha = 0.4f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    isActive -> palette.primary
                                    isPassed -> Color(0xFF4CAF50)
                                    else -> GlassBorder
                                }
                            ),
                            modifier = Modifier.clickable {
                                if (stepNumber <= wizardStep || (stepNumber == 2 && brand.isNotBlank()) || (stepNumber == 3 && model.isNotBlank())) {
                                    wizardStep = stepNumber
                                }
                            }
                        ) {
                            Text(
                                text = stepName,
                                fontSize = 10.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isActive -> Color.Black
                                    isPassed -> Color(0xFF81C784)
                                    else -> TextSecondary
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 1: COUNTRY & BRAND SELECTION
            AnimatedVisibility(visible = wizardStep == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Country Selection
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. COUNTRY OF ORIGIN / REGIONAL MARKET",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Select your bike's market region so official catalog specs and model lineups match your country:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            popularCountries.forEach { cItem ->
                                val cleanC = cItem.split(" ").first()
                                val isSelected = countryOfOrigin.contains(cleanC, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) palette.primary else GlassBorder
                                    ),
                                    modifier = Modifier.clickable { countryOfOrigin = cleanC }
                                ) {
                                    Text(
                                        text = cItem,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.Black else palette.textPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Brand Selection
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. SELECT MOTORCYCLE BRAND",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            popularBrands.chunked(3).forEach { rowBrands ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowBrands.forEach { brandItem ->
                                        val isSelected = brand.equals(brandItem, ignoreCase = true)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) palette.primary else GlassBorder
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    selectBrandAndFetchModels(brandItem)
                                                }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = brandItem,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.Black else palette.textPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        MotoTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = "Or Type Custom Brand Name"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (brand.isNotBlank()) {
                                    selectBrandAndFetchModels(brand)
                                }
                            },
                            enabled = brand.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MotoBookShapes.medium
                        ) {
                            Text("Next: Choose Model ($countryOfOrigin spec) ➔", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // STEP 2: MODEL SELECTION
            AnimatedVisibility(visible = wizardStep == 2) {
                Column {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "STEP 2: SELECT $brand MODEL ($countryOfOrigin)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                            TextButton(onClick = { wizardStep = 1 }) {
                                Text("Change Brand/Country", fontSize = 11.sp, color = palette.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoadingModels) {
                            Row(
                                modifier = Modifier.padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = palette.primary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Searching $brand official lineup in $countryOfOrigin...",
                                    fontSize = 12.sp,
                                    color = palette.textPrimary
                                )
                            }
                        } else {
                            Text(
                                text = "Select official model for $brand ($countryOfOrigin edition):",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                modelsList.chunked(2).forEach { rowModels ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowModels.forEach { modelItem ->
                                            val isSelected = model.equals(modelItem, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) palette.primary else GlassBorder
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        selectModelAndFetchVariants(modelItem)
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.TwoWheeler,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color.Black else palette.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = modelItem,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) Color.Black else palette.textPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        MotoTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = "Or Type Custom Model Name"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (model.isNotBlank()) {
                                    selectModelAndFetchVariants(model)
                                }
                            },
                            enabled = model.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MotoBookShapes.medium
                        ) {
                            Text("Next: Choose Variant & Color online ➔", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // STEP 3: VARIANT & COLOR SELECTION
            AnimatedVisibility(visible = wizardStep == 3) {
                Column {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "STEP 3: VARIANT & COLOR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                            TextButton(onClick = { wizardStep = 2 }) {
                                Text("Change Model", fontSize = 11.sp, color = palette.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.primary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Selected: $brand $model ($countryOfOrigin market spec)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isLoadingVariants) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = palette.primary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Loading official colors & edition variants online...",
                                    fontSize = 12.sp,
                                    color = palette.textPrimary
                                )
                            }
                        } else {
                            if (variantsList.isNotEmpty()) {
                                Text(
                                    text = "SELECT VARIANT / EDITION:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    variantsList.forEach { v ->
                                        val isSelected = selectedVariant.equals(v, ignoreCase = true)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) palette.primary else GlassBorder
                                            ),
                                            modifier = Modifier.clickable { selectedVariant = v }
                                        ) {
                                            Text(
                                                text = v,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.Black else palette.textPrimary,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            Text(
                                text = "SELECT COLOR VARIANT:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                colorsList.forEach { cName ->
                                    val isSelected = color.equals(cName, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) palette.primary else palette.surface.copy(alpha = 0.5f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) palette.primary else GlassBorder
                                        ),
                                        modifier = Modifier.clickable { color = cName }
                                    ) {
                                        Text(
                                            text = cName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.Black else palette.textPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        MotoTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = "Or Custom Color Name"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { fetchAndAutoFillFullProfile() },
                            enabled = !isLoadingFullSpecs,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primary,
                                contentColor = Color.Black
                            ),
                            shape = MotoBookShapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoadingFullSpecs) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Downloading Specs, Manual & Stock Image...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⚡ Auto-Fill Profile & Manual ($countryOfOrigin Spec)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // STEP 4: REVIEW, USER MANUAL & EDIT BIKE PROFILE
            AnimatedVisibility(visible = wizardStep == 4) {
                Column {
                    // Status Notification Banner
                    onlineStatusMsg?.let { msg ->
                        Surface(
                            shape = MotoBookShapes.small,
                            color = if (isSuccessFetch) Color(0xFF1B5E20).copy(alpha = 0.35f)
                            else palette.surface.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSuccessFetch) Color(0xFF4CAF50) else palette.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSuccessFetch) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isSuccessFetch) Color(0xFF81C784) else palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Bike Profile Photo Header
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BIKE PROFILE PHOTO (AUTO-FETCHED ONLINE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(palette.surface.copy(alpha = 0.6f))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!bikeImagePath.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(bikeImagePath)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Bike Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "No Photo Loaded",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = MotoBookShapes.small,
                                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Photo", fontSize = 11.sp, color = palette.primary)
                            }

                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val spec = BikeSpecFetcher.fetchBikeSpecsOnline(brand, model, year.toIntOrNull() ?: 2023, color, countryOfOrigin).getOrNull()
                                        if (spec != null && spec.imageUrl.isNotBlank()) {
                                            bikeImagePath = spec.imageUrl
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = MotoBookShapes.small,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = palette.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset Stock Photo", fontSize = 11.sp, color = palette.textPrimary)
                            }
                        }
                    }

                    // Basic Information Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.basic_info_header) + " (EDITABLE)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }
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
                                    value = countryOfOrigin,
                                    onValueChange = { countryOfOrigin = it },
                                    label = "Country of Origin / Market"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MotoTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = stringResource(id = R.string.year),
                                    keyboardType = KeyboardType.Number
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MotoTextField(
                            value = regNo,
                            onValueChange = { regNo = it },
                            label = stringResource(id = R.string.registration_no)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Digital Owner Manual Summary Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DIGITAL OWNER'S MANUAL & MAINTENANCE DATA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        MotoTextField(
                            value = manualSummary,
                            onValueChange = { manualSummary = it },
                            label = "Analyzed Owner Manual Summary",
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MotoTextField(
                            value = manualUrl,
                            onValueChange = { manualUrl = it },
                            label = "Official Manual Link / Search Query"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Color & Engine Technical Specs Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COLOR & ENGINE SPECIFICATIONS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                MotoTextField(
                                    value = color,
                                    onValueChange = { color = it },
                                    label = "Bike Color"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MotoTextField(
                                    value = engineCc,
                                    onValueChange = { engineCc = it },
                                    label = "Engine (cc)",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MotoTextField(
                            value = maxPower,
                            onValueChange = { maxPower = it },
                            label = "Max Power / Performance Specs"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fuel & Pressure Configuration Card
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
                                    label = "${stringResource(id = R.string.tank_capacity)} (L)",
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MotoTextField(
                                    value = reserveCapacity,
                                    onValueChange = { reserveCapacity = it },
                                    label = "${stringResource(id = R.string.reserve_capacity)} (L)",
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = stringResource(id = R.string.tyre_setup_header),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Maintenance & Oil Specifications Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RECOMMENDED OILS & SERVICE SCHEDULE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MotoTextField(
                            value = recommendedOilGrade,
                            onValueChange = { recommendedOilGrade = it },
                            label = "Recommended Engine Oil Grade & Volume"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MotoTextField(
                            value = maintenanceScheduleNote,
                            onValueChange = { maintenanceScheduleNote = it },
                            label = "Maintenance & Service Schedule Notes",
                            singleLine = false
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val finalFuelType = fuelTypes.getOrNull(selectedFuelTypeIndex) ?: "Octane"
                            onSaveClick(
                                existingBike?.bikeId ?: 0L,
                                bikeName.ifBlank { "$brand $model" },
                                brand,
                                model,
                                year,
                                regNo,
                                finalFuelType,
                                tankCapacity,
                                reserveCapacity,
                                frontPsi,
                                rearPsi,
                                color,
                                engineCc,
                                maxPower,
                                recommendedOilGrade,
                                maintenanceScheduleNote,
                                countryOfOrigin,
                                manualUrl,
                                manualSummary,
                                bikeImagePath
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primary,
                            contentColor = Color.Black
                        ),
                        shape = MotoBookShapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (existingBike != null) "Update Motorcycle Profile" else "Save Motorcycle Profile",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun MotoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
    singleLine: Boolean = true
) {
    val palette = LocalThemePalette.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = MotoBookShapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = GlassBorder,
            focusedContainerColor = palette.surface,
            unfocusedContainerColor = palette.surface.copy(alpha = 0.5f),
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = TextSecondary
        )
    )
}
