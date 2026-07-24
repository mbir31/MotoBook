package com.example.motobook.presentation.chain

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
import com.example.motobook.domain.model.ChainEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChainScreen(
    entries: List<ChainEntry>,
    onSaveChain: (odometer: String, lubeType: String, notes: String?) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var odometer by remember { mutableStateOf("") }
    var lubricantType by remember { mutableStateOf("Chain Lube Spray") }
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
            MotoTopBar(title = "Chain Maintenance", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "LOG CHAIN LUBRICATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = odometer,
                    onValueChange = { odometer = it },
                    label = "Odometer (km)",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = lubricantType,
                    onValueChange = { lubricantType = it },
                    label = "Lubricant Type"
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotoTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes (optional)"
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlowButton(
                    text = "Save Chain Record",
                    onClick = {
                        onSaveChain(odometer, lubricantType, notes.ifBlank { null })
                        odometer = ""
                        notes = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CHAIN HISTORY",
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
                items(entries, key = { it.chainId }) { chain ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormat.format(Date(chain.date)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = chain.lubricantType ?: "Lubricant",
                                    fontSize = 14.sp,
                                    color = palette.primary
                                )
                            }
                            if (chain.odometer != null) {
                                Text(
                                    text = "📍 ${chain.odometer.toInt()} km",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
