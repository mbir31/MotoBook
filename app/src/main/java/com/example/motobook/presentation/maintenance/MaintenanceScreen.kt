package com.example.motobook.presentation.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.components.GlassCard
import com.example.motobook.presentation.components.IosBottomBar
import com.example.motobook.presentation.components.MotoTopBar
import com.example.motobook.presentation.navigation.Screen
import com.example.motobook.presentation.theme.*

@Composable
fun MaintenanceScreen(
    onNavigateToTyrePressure: () -> Unit,
    onNavigateToWash: () -> Unit,
    onNavigateToServiceHistory: () -> Unit,
    onNavigateToAddService: () -> Unit,
    onNavigateToChain: () -> Unit,
    onTabSelected: (String) -> Unit
) {
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
                .padding(horizontal = 20.dp)
        ) {
            MotoTopBar(
                title = stringResource(id = R.string.nav_maintenance)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // Header Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = palette.primary.copy(alpha = 0.12f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(palette.primary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Motorcycle Care & Maintenance",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Keep your bike running smoothly with logs & alerts",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "MAINTENANCE MODULES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                // 1. Tyre Pressure & Air Refill
                item {
                    MaintenanceOptionCard(
                        title = stringResource(id = R.string.tyre_screen_title),
                        subtitle = "Check and log air refill date & pressure",
                        icon = Icons.Default.Air,
                        accentColor = CyanLight,
                        onClick = onNavigateToTyrePressure
                    )
                }

                // 2. Servicing & Regular Maintenance
                item {
                    MaintenanceOptionCard(
                        title = stringResource(id = R.string.service_history_title),
                        subtitle = "Log full servicing, oil filter, engine oil & tuning",
                        icon = Icons.Default.Handyman,
                        accentColor = palette.primary,
                        onClick = onNavigateToServiceHistory,
                        actionText = "+ Add Service",
                        onActionClick = onNavigateToAddService
                    )
                }

                // 3. Bike Wash Records
                item {
                    MaintenanceOptionCard(
                        title = stringResource(id = R.string.wash_screen_title),
                        subtitle = "Track foam wash, self wash & detailing history",
                        icon = Icons.Default.WaterDrop,
                        accentColor = PurplePrimary,
                        onClick = onNavigateToWash
                    )
                }

                // 4. Chain Lube & Clean
                item {
                    MaintenanceOptionCard(
                        title = stringResource(id = R.string.chain_screen_title),
                        subtitle = "Monitor chain lubrication & cleaning schedule",
                        icon = Icons.Default.Link,
                        accentColor = AmberPrimary,
                        onClick = onNavigateToChain
                    )
                }

                // 5. Repairs & Part Replacement
                item {
                    MaintenanceOptionCard(
                        title = "Repairs & Part Replacement",
                        subtitle = "Record brake pad, battery, cable & major repairs",
                        icon = Icons.Default.HomeRepairService,
                        accentColor = EmeraldPrimary,
                        onClick = onNavigateToAddService
                    )
                }
            }

            // iOS Floating Navigation Bar
            IosBottomBar(
                currentRoute = Screen.Maintenance.route,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun MaintenanceOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val palette = LocalThemePalette.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (actionText != null && onActionClick != null) {
                Surface(
                    color = accentColor.copy(alpha = 0.18f),
                    shape = MotoBookShapes.small,
                    modifier = Modifier.clickable { onActionClick() }
                ) {
                    Text(
                        text = actionText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
