package com.example.motobook.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.motobook.presentation.navigation.Screen
import com.example.motobook.presentation.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = LocalCardRadius.current,
    backgroundColor: Color = LocalThemePalette.current.surface.copy(alpha = LocalGlassIntensity.current),
    borderColor: Color = GlassBorder,
    glowColor: Color = Color.Transparent,
    glowRadius: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    var cardModifier = modifier.clip(shape)

    if (onClick != null) {
        cardModifier = cardModifier.clickable { onClick() }
    }

    Box(
        modifier = cardModifier
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .padding(18.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    glowColor: Color = LocalThemePalette.current.primary,
    containerColor: Color = LocalThemePalette.current.primary
) {
    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = TextOnAccent,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = TextOnAccent.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.4f),
                spotColor = glowColor.copy(alpha = 0.6f)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MotoBookHeaderLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_motobook_logo),
            contentDescription = "MotoBook Logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotoTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    showLogo: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showLogo) {
                    MotoBookHeaderLogo(size = 36.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalThemePalette.current.textPrimary
                )
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = LocalThemePalette.current.textPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun AnimatedCounter(
    value: Float,
    unit: String = "",
    decimalPlaces: Int = 1,
    fontSize: Int = 28,
    color: Color = LocalThemePalette.current.primary
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(value) {
        animate(
            initialValue = 0f,
            targetValue = value,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        ) { valCurrent, _ ->
            animatedValue = valCurrent
        }
    }

    val formatStr = "%.${decimalPlaces}f"
    val displayText = String.format(formatStr, animatedValue)

    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = displayText,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        if (unit.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                fontSize = (fontSize * 0.5).sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val surfaceColor = LocalThemePalette.current.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor.copy(alpha = 0.5f), shape)
            .border(1.dp, GlassBorder, shape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            val activeColor = LocalThemePalette.current.primary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(
                        if (isSelected) activeColor else Color.Transparent,
                        shape
                    )
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextOnAccent else TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = LocalThemePalette.current.primary,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

data class NavItemData(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
)

@Composable
fun IosBottomBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalThemePalette.current
    val items = listOf(
        NavItemData(Screen.Dashboard.route, com.example.R.string.nav_home, Icons.Default.Home),
        NavItemData(Screen.FuelHistory.route, com.example.R.string.nav_fuel, Icons.Default.LocalGasStation),
        NavItemData(Screen.Maintenance.route, com.example.R.string.nav_maintenance, Icons.Default.Build),
        NavItemData(Screen.History.route, com.example.R.string.nav_history, Icons.Default.History)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = palette.surface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(26.dp),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) palette.primary else TextSecondary,
                    animationSpec = tween(300), label = "tabColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) palette.primary.copy(alpha = 0.16f) else Color.Transparent
                        )
                        .clickable {
                            if (!isSelected) {
                                onTabSelected(item.route)
                            }
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(id = item.titleResId),
                            tint = animatedColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stringResource(id = item.titleResId),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = animatedColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
