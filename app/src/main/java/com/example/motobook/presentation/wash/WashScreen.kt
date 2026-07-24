package com.example.motobook.presentation.wash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.WashEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.components.SegmentedToggle
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WashScreen(
    entries: List<WashEntry>,
    onSaveWash: (washType: String, cost: String, notes: String?) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var washTypeIndex by remember { mutableIntStateOf(0) }
    val washTypes = listOf("SELF_WASH", "PROFESSIONAL")

    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val palette = LocalThemePalette.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

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
            MotoTopBar(title = "Bike Wash Records", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "LOG NEW WASH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                SegmentedToggle(
                    options = washTypes,
                    selectedIndex = washTypeIndex,
                    onOptionSelected = { washTypeIndex = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (washTypeIndex == 1) {
                    MotoTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = "Cost (৳)",
                        keyboardType = KeyboardType.Decimal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                MotoTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes (optional)"
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlowButton(
                    text = "Save Wash Record",
                    onClick = {
                        onSaveWash(washTypes[washTypeIndex], cost, notes.ifBlank { null })
                        cost = ""
                        notes = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WASH HISTORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(entries, key = { it.washId }) { wash ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormat.format(Date(wash.date)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = wash.washType,
                                    fontSize = 14.sp,
                                    color = palette.primary
                                )
                            }
                            if (wash.cost != null) {
                                Text(
                                    text = "৳ ${wash.cost.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
