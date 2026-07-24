package com.example.motobook.presentation.tyre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motobook.domain.model.TyrePressureEntry
import com.example.motobook.presentation.bike.MotoTextField
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.GlowButton
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TyrePressureScreen(
    recommendedFrontPsi: Float = 28f,
    recommendedRearPsi: Float = 32f,
    entries: List<TyrePressureEntry>,
    onSaveLog: (front: String, rear: String, notes: String?) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var frontPsi by remember { mutableStateOf("") }
    var rearPsi by remember { mutableStateOf("") }
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
            MotoTopBar(title = "Tyre Pressure Logs", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(12.dp))

            // Recommended Pressure Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = palette.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "RECOMMENDED PRESSURE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "FRONT", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${recommendedFrontPsi.toInt()} PSI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "REAR", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${recommendedRearPsi.toInt()} PSI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Log Input
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "LOG NEW PRESSURE",
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
                            label = "Front PSI *",
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MotoTextField(
                            value = rearPsi,
                            onValueChange = { rearPsi = it },
                            label = "Rear PSI *",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlowButton(
                    text = "Save Pressure Log",
                    onClick = {
                        if (frontPsi.isNotBlank() && rearPsi.isNotBlank()) {
                            onSaveLog(frontPsi, rearPsi, notes.ifBlank { null })
                            frontPsi = ""
                            rearPsi = ""
                            notes = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PRESSURE HISTORY",
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
                items(entries, key = { it.tyrePressureId }) { log ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormat.format(Date(log.date)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Front: ${log.frontPsi.toInt()} PSI  |  Rear: ${log.rearPsi.toInt()} PSI",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.primary
                                )
                            }

                            // Status Indicator
                            val isOk = kotlin.math.abs(log.frontPsi - recommendedFrontPsi) <= 2f &&
                                    kotlin.math.abs(log.rearPsi - recommendedRearPsi) <= 2f
                            val statusText = if (isOk) "✅ OK" else "⚠️ Check"
                            val statusColor = if (isOk) EmeraldPrimary else AmberPrimary

                            Surface(
                                color = statusColor.copy(alpha = 0.2f),
                                shape = MotoBookShapes.small
                            ) {
                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
