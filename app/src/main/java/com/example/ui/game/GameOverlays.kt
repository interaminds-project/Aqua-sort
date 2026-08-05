package com.example.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// --- WRAPPER OVERLAY DIALOG ---
@Composable
fun OverlayDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() }, // Click outside to close
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .clickable(enabled = false) {} // Prevent click-through
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 28.dp,
                    borderWidth = 1.dp,
                    glowColor = ColorPrimary
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge.copy(
                                shadow = Shadow(
                                    color = ColorPrimary.copy(alpha = 0.5f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 6f
                                )
                            )
                        )
                        GlassIconButton(
                            icon = Icons.Filled.Close,
                            onClick = onDismiss,
                            size = 36.dp,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

private val Modifier.weight_custom: Modifier get() = Modifier

// --- PAUSE MENU ---
@Composable
fun PauseMenuOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    isSoundEnabled: Boolean,
    isMusicEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.82f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                glowColor = ColorPrimary
            ) {
                Text(
                    text = "GAME PAUSED",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                PremiumButton(
                    text = "RESUME GAME",
                    icon = Icons.Filled.PlayArrow,
                    onClick = onResume,
                    gradientColors = listOf(ColorSecondary, ColorSecondary.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "RESTART LEVEL",
                    icon = Icons.Filled.Refresh,
                    onClick = onRestart,
                    gradientColors = listOf(ColorPrimary, ColorPrimary.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "MAIN MENU",
                    icon = Icons.Filled.Home,
                    onClick = onHome,
                    gradientColors = listOf(Color(0xFF4B5563), Color(0xFF374151)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mini sound setting bar
                Divider(color = GlassBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassIconButton(
                        icon = if (isSoundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        onClick = onToggleSound,
                        size = 48.dp,
                        tint = if (isSoundEnabled) ColorAccent else Color.Gray
                    )
                    GlassIconButton(
                        icon = if (isMusicEnabled) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                        onClick = onToggleMusic,
                        size = 48.dp,
                        tint = if (isMusicEnabled) ColorAccent else Color.Gray
                    )
                }
            }
        }
    }
}

// --- VICTORY SCREEN ---
@Composable
fun VictoryOverlay(
    levelJustCleared: Int,
    coinsEarned: Int,
    gemsEarned: Int,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    stars: Int = 3,
    isVibrationEnabled: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val star1Scale = remember { androidx.compose.animation.core.Animatable(0f) }
    val star2Scale = remember { androidx.compose.animation.core.Animatable(0f) }
    val star3Scale = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(stars) {
        if (stars >= 1) {
            star1Scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
            HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
        }
        if (stars >= 2) {
            kotlinx.coroutines.delay(160)
            star2Scale.animateTo(1.25f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
            HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
        }
        if (stars >= 3) {
            kotlinx.coroutines.delay(160)
            star3Scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
            HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Stars Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (stars >= 1) ColorAccent else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier
                        .scale(if (stars >= 1) star1Scale.value else 1f)
                        .size(48.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (stars >= 2) ColorAccent else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier
                        .scale(if (stars >= 2) star2Scale.value else 1f)
                        .size(72.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (stars >= 3) ColorAccent else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier
                        .scale(if (stars >= 3) star3Scale.value else 1f)
                        .size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                glowColor = ColorSuccess
            ) {
                Text(
                    text = "LEVEL SOLVED!",
                    color = ColorSuccess,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = Shadow(
                            color = ColorSuccess.copy(alpha = 0.4f),
                            offset = Offset(0f, 3f),
                            blurRadius = 8f
                        )
                    )
                )

                Text(
                    text = "Level $levelJustCleared Complete",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Rewards breakdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MonetizationOn, null, tint = ColorAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$coinsEarned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Text("Coins Reward", color = Color.Gray, fontSize = 12.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Diamond, null, tint = ColorSecondary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$gemsEarned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Text("Gems Bonus", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                PremiumButton(
                    text = "NEXT LEVEL",
                    icon = Icons.Filled.PlayArrow,
                    onClick = {
                        HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
                        onNextLevel()
                    },
                    gradientColors = listOf(ColorSecondary, ColorSecondary.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = ColorSecondary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "REPLAY",
                    icon = Icons.Filled.Refresh,
                    onClick = {
                        HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
                        onReplay()
                    },
                    gradientColors = listOf(Color(0xFF4B5563), Color(0xFF374151)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- DAILY REWARDS PANEL ---
@Composable
fun DailyRewardsOverlay(
    currentDayIndex: Int,
    lastClaimedTime: Long,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Daily Rewards", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Log in daily to claim epic boosters & currencies!",
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 7 Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(7) { index ->
                    val day = index + 1
                    val isClaimed = day <= currentDayIndex
                    val isToday = day == currentDayIndex + 1
                    
                    val cardBg = when {
                        isClaimed -> Color(0xFF1E293B)
                        isToday -> Color(0xFF3B416B)
                        else -> Color(0x1AFFFFFF)
                    }

                    val borderColor = when {
                        isToday -> ColorPrimary
                        isClaimed -> ColorSuccess.copy(alpha = 0.4f)
                        else -> GlassBorder
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DAY $day",
                            color = if (isToday) ColorAccent else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = when (day) {
                                3, 5 -> Icons.Filled.Diamond
                                7 -> Icons.Filled.Star
                                else -> Icons.Filled.MonetizationOn
                            },
                            contentDescription = null,
                            tint = when (day) {
                                3, 5 -> ColorSecondary
                                7 -> ColorAccent
                                else -> ColorAccent
                            },
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (day) {
                                1 -> "100"
                                2 -> "250"
                                3 -> "50 Gems"
                                4 -> "500"
                                5 -> "100 Gems"
                                6 -> "1,000"
                                else -> "EPIC SKIN"
                            },
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (isClaimed) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(Icons.Filled.CheckCircle, null, tint = ColorSuccess, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val coolingPeriod = 12 * 60 * 60 * 1000
            val isReady = System.currentTimeMillis() - lastClaimedTime >= coolingPeriod

            PremiumButton(
                text = if (isReady) "CLAIM TODAY'S REWARD" else "CLAIMED - COME BACK LATER",
                isEnabled = isReady,
                onClick = onClaim,
                gradientColors = listOf(ColorAccent, Color(0xFFD97706)),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- LUCKY WHEEL PANEL ---
@Composable
fun LuckyWheelOverlay(
    isSpinning: Boolean,
    rotationAngle: Float,
    onSpin: () -> Unit,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Lucky Wheel", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spin to win exclusive Jackpots! Costs 25 Gems.",
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wheel Container Canvas
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .rotate(rotationAngle)
                    .drawBehind {
                        val diskRadius = this.size.width / 2
                        val centerOffset = Offset(this.size.width / 2, this.size.height / 2)

                        // Background disk
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF2E1C61), Color(0xFF0F0B26))
                            ),
                            radius = diskRadius
                        )
                        // Outer neon stroke
                        drawCircle(
                            color = ColorPrimary,
                            radius = diskRadius,
                            style = Stroke(width = 4.dp.toPx())
                        )
                        // Sector lines
                        for (i in 0 until 8) {
                            val angle = (i * 45).toDouble() * Math.PI / 180.0
                            val endX = centerOffset.x + diskRadius * cos(angle).toFloat()
                            val endY = centerOffset.y + diskRadius * sin(angle).toFloat()
                            drawLine(
                                color = GlassBorder,
                                start = centerOffset,
                                end = Offset(endX, endY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Circular icons inside sectors (drawn statically or text labels)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(32.dp)
                    )
                    Text("SPIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Arrow Indicator pointing to the top sector
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = ColorAccent,
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = (-20).dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PremiumButton(
                text = if (isSpinning) "SPINNING..." else "SPIN FOR 25 GEMS",
                isEnabled = !isSpinning,
                onClick = onSpin,
                gradientColors = listOf(ColorPrimary, ColorSecondary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- SHOP & SKIN INVENTORY PANEL ---
@Composable
fun ShopOverlay(
    coins: Int,
    gems: Int,
    unlockedThemes: String,
    equippedTheme: String,
    unlockedTubes: String,
    equippedTube: String,
    onBuyTheme: (String) -> Unit,
    onEquipTheme: (String) -> Unit,
    onBuyTube: (String) -> Unit,
    onEquipTube: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Themes, 1 = Tube Skins

    OverlayDialog(title = "Alchemist Shop", onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeTab == 0) ColorPrimary else Color.Transparent)
                        .clickable { activeTab = 0 }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("THEMES", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeTab == 1) ColorPrimary else Color.Transparent)
                        .clickable { activeTab = 1 }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GLASS TUBES", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeTab == 0) {
                    // Render AvailableThemes
                    items(AvailableThemes) { theme ->
                        val isOwned = unlockedThemes.split(",").contains(theme.id)
                        val isEquipped = equippedTheme == theme.id

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(GlassBg)
                                .border(1.dp, if (isEquipped) ColorPrimary else GlassBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(theme.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(theme.label, color = Color.Gray, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (isEquipped) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false
                                ) {
                                    Text("EQUIPPED", color = Color.White)
                                }
                            } else if (isOwned) {
                                PremiumButton(
                                    text = "EQUIP",
                                    onClick = { onEquipTheme(theme.id) },
                                    gradientColors = listOf(ColorPrimary, ColorPrimary)
                                )
                            } else {
                                PremiumButton(
                                    text = "${theme.cost}🪙",
                                    onClick = { onBuyTheme(theme.id) },
                                    gradientColors = listOf(ColorAccent, ColorAccent)
                                )
                            }
                        }
                    }
                } else {
                    // Render AvailableTubes
                    items(AvailableTubes) { tube ->
                        val isOwned = unlockedTubes.split(",").contains(tube.id)
                        val isEquipped = equippedTube == tube.id

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(GlassBg)
                                .border(1.dp, if (isEquipped) ColorPrimary else GlassBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tube.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(tube.shapeDescription, color = Color.Gray, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (isEquipped) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false
                                ) {
                                    Text("EQUIPPED", color = Color.White)
                                }
                            } else if (isOwned) {
                                PremiumButton(
                                    text = "EQUIP",
                                    onClick = { onEquipTube(tube.id) },
                                    gradientColors = listOf(ColorPrimary, ColorPrimary)
                                )
                            } else {
                                PremiumButton(
                                    text = "${tube.cost}🪙",
                                    onClick = { onBuyTube(tube.id) },
                                    gradientColors = listOf(ColorAccent, ColorAccent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- LEADERBOARD PANEL ---
@Composable
fun LeaderboardOverlay(
    entries: List<GameViewModel.LeaderboardEntry>,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Weekly Alchemists", onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Compete globally! Rankings reset in 2d 14h.",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(entries) { index, entry ->
                    val isUser = entry.isUser
                    val rank = index + 1
                    
                    val bg = if (isUser) Color(0xFF1E293B) else GlassBg
                    val border = if (isUser) ColorPrimary else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Rank Number
                            Text(
                                text = "#$rank",
                                color = when (rank) {
                                    1 -> ColorAccent
                                    2 -> ColorSecondary
                                    3 -> ColorPrimary
                                    else -> Color.White
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.width(36.dp)
                            )

                            // Profile Icon (Dynamic)
                            val avatarOption = AvatarOptions.find { it.id == entry.avatarId } ?: AvatarOptions.first()
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(avatarOption.color.copy(alpha = 0.2f))
                                    .border(1.dp, avatarOption.color.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = avatarOption.icon,
                                    contentDescription = null,
                                    tint = avatarOption.color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = entry.name,
                                color = if (isUser) ColorAccent else Color.White,
                                fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }

                        // Level tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Lvl ${entry.level}", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- ACHIEVEMENTS PANEL ---
@Composable
fun AchievementsOverlay(
    completedIds: String,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Goals & Badges", onDismiss = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(AchievementsList) { ach ->
                val isCompleted = completedIds.split(",").contains(ach.id)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .border(1.dp, if (isCompleted) ColorSuccess.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ach.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(ach.description, color = Color.Gray, fontSize = 12.sp)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Small aesthetic progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (isCompleted) 1f else 0.4f) // Simulating progress
                                    .fillMaxHeight()
                                    .background(if (isCompleted) ColorSuccess else ColorPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Rewards indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MonetizationOn, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${ach.rewardCoins}", color = ColorAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isCompleted) {
                            Icon(Icons.Filled.CheckCircle, null, tint = ColorSuccess, modifier = Modifier.size(20.dp))
                        } else {
                            Text("LOCKED", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class AvatarOption(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val AvatarOptions = listOf(
    AvatarOption(0, "Novice Alchemist", Icons.Filled.Person, Color(0xFF6C63FF)),
    AvatarOption(1, "Aqua Sorcerer", Icons.Filled.WaterDrop, Color(0xFF00C2A8)),
    AvatarOption(2, "Pyromancer", Icons.Filled.Whatshot, Color(0xFFEF4444)),
    AvatarOption(3, "Electro Sage", Icons.Filled.Bolt, Color(0xFFFFB703)),
    AvatarOption(4, "Biochemist", Icons.Filled.Science, Color(0xFF8B5CF6)),
    AvatarOption(5, "Alchemist Elite", Icons.Filled.AutoAwesome, Color(0xFFEC4899)),
    AvatarOption(6, "Botanist", Icons.Filled.Eco, Color(0xFF10B981)),
    AvatarOption(7, "Celestial Sage", Icons.Filled.Star, Color(0xFFFBBF24))
)

data class AlchemistTitle(
    val title: String,
    val requiredLevel: Int,
    val isHidden: Boolean,
    val description: String
)

val AlchemistTitles = listOf(
    AlchemistTitle("Novice Alchemist", 1, false, "You are just learning to handle basic reagents."),
    AlchemistTitle("Apprentice Alchemist", 5, false, "You understand basic density and color separation."),
    AlchemistTitle("Adept Alchemist", 10, false, "You can sort multiple complex elements comfortably."),
    AlchemistTitle("Elementalist", 15, false, "You begin to command specialized elemental fluids."),
    AlchemistTitle("Grand Alchemist", 20, false, "Your speed and accuracy are recognized by the guild."),
    AlchemistTitle("Elixir Sage", 30, true, "Revealed only after you successfully complete Level 30."),
    AlchemistTitle("Master Alchemist", 40, true, "The ultimate rank, representing complete mastery of potion arts.")
)

fun getAlchemistTitle(level: Int): String {
    return AlchemistTitles.lastOrNull { level >= it.requiredLevel }?.title ?: "Novice Alchemist"
}

// --- PLAYER PROFILE PANEL ---
@Composable
fun PlayerProfileOverlay(
    gamesPlayed: Int,
    gamesWon: Int,
    bestTime: Int,
    currentLevel: Int,
    selectedAvatarId: Int,
    onSelectAvatar: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTitle = getAlchemistTitle(currentLevel)
    val currentAvatar = AvatarOptions.find { it.id == selectedAvatarId } ?: AvatarOptions.first()

    OverlayDialog(title = "Player Profile", onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar circle with glow
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(currentAvatar.color.copy(alpha = 0.4f), Color.Transparent)
                        )
                    )
                    .border(2.dp, currentAvatar.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = currentAvatar.icon,
                    contentDescription = null,
                    tint = currentAvatar.color,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(currentTitle, color = ColorAccent, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Text("Lvl $currentLevel Alchemist Progress", color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(18.dp))

            // Choose Avatar Title Header
            Text(
                text = "TAP AN AVATAR TO EQUIP",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal row or flow of selectable avatars (No uploads, client choices)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                AvatarOptions.take(4).forEach { avatar ->
                    val isSelected = avatar.id == selectedAvatarId
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) avatar.color.copy(alpha = 0.3f) else GlassBg)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) avatar.color else GlassBorder,
                                shape = CircleShape
                            )
                            .clickable { onSelectAvatar(avatar.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = avatar.icon,
                            contentDescription = avatar.name,
                            tint = if (isSelected) avatar.color else Color.LightGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                AvatarOptions.drop(4).forEach { avatar ->
                    val isSelected = avatar.id == selectedAvatarId
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) avatar.color.copy(alpha = 0.3f) else GlassBg)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) avatar.color else GlassBorder,
                                shape = CircleShape
                            )
                            .clickable { onSelectAvatar(avatar.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = avatar.icon,
                            contentDescription = avatar.name,
                            tint = if (isSelected) avatar.color else Color.LightGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ranks progression list with clear locks and level notices
            Text(
                text = "ALCHEMIST RANK PROGRESSION",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AlchemistTitles.forEachIndexed { index, titleInfo ->
                        val isUnlocked = currentLevel >= titleInfo.requiredLevel
                        val displayName = if (isUnlocked || !titleInfo.isHidden) {
                            titleInfo.title
                        } else {
                            "??? (Hidden Title)"
                        }
                        
                        val displayDescription = if (isUnlocked) {
                            titleInfo.description
                        } else if (titleInfo.isHidden) {
                            "Revealed only after you successfully complete Level ${titleInfo.requiredLevel}."
                        } else {
                            titleInfo.description
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isUnlocked) Color.White.copy(alpha = 0.03f) else Color.Transparent)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                        contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                                        tint = if (isUnlocked) ColorSuccess else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = displayName,
                                        color = if (isUnlocked) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isUnlocked) ColorSuccess.copy(alpha = 0.15f)
                                            else ColorSecondary.copy(alpha = 0.1f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isUnlocked) "UNLOCKED" else "Lvl ${titleInfo.requiredLevel}",
                                        color = if (isUnlocked) ColorSuccess else ColorSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = displayDescription,
                                color = if (isUnlocked) Color.LightGray else Color.Gray.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(start = 26.dp)
                            )
                        }

                        if (index < AlchemistTitles.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.White.copy(alpha = 0.05f))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Statistics table
            Text(
                text = "ALCHEMICAL STATISTICS",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Puzzles Played:", color = Color.LightGray, fontSize = 14.sp)
                    Text("$gamesPlayed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Solved:", color = Color.LightGray, fontSize = 14.sp)
                    Text("$gamesWon", color = ColorSuccess, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Win Success Rate:", color = Color.LightGray, fontSize = 14.sp)
                    val rate = if (gamesPlayed == 0) 100 else (gamesWon * 100 / gamesPlayed)
                    Text("$rate%", color = ColorSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Personal Best Time:", color = Color.LightGray, fontSize = 14.sp)
                    val mins = bestTime / 60
                    val secs = bestTime % 60
                    Text(String.format("%02d:%02d", mins, secs), color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// --- GAME OVER OVERLAY ---
@Composable
fun GameOverOverlay(
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onSkip: () -> Unit,
    onHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.SentimentVeryDissatisfied, null, tint = ColorDanger, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                glowColor = ColorDanger
            ) {
                Text(
                    text = "OUT OF MOVES?",
                    color = ColorDanger,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Don't let the liquids spill! Choose a helper below.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                PremiumButton(
                    text = "UNDO LAST MOVE",
                    icon = Icons.Filled.Undo,
                    onClick = onUndo,
                    gradientColors = listOf(ColorSecondary, ColorSecondary.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "RESTART LEVEL",
                    icon = Icons.Filled.Refresh,
                    onClick = onRestart,
                    gradientColors = listOf(ColorPrimary, ColorPrimary.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "SKIP THIS LEVEL",
                    icon = Icons.Filled.SkipNext,
                    onClick = onSkip,
                    gradientColors = listOf(ColorAccent, ColorAccent.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "BACK TO HOME",
                    icon = Icons.Filled.Home,
                    onClick = onHome,
                    gradientColors = listOf(Color(0xFF4B5563), Color(0xFF374151)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- FULLSCREEN INTERACTIVE AD OVERLAY ---
@Composable
fun AdOverlay(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var countdown by remember { mutableStateOf(5) }
    var isSkipEnabled by remember { mutableStateOf(false) }
    var showAppStoreDialog by remember { mutableStateOf(false) }
    
    // Simulate Video Play Progress
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        isSkipEnabled = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Main Ad Container
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Sponsored Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADVERTISEMENT",
                        color = Color.LightGray.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Sponsored by Aqua Sort Pro",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Info icon to balance the header now that Skip is at the bottom
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Ad Info",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Simulated Video Progress Bar
            LinearProgressIndicator(
                progress = progressAnim.value,
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = ColorSecondary,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            // 2. Beautiful Mock Video Player / Playable Ad Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF131A2B))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background game sort graphic / video animation
                val infiniteTransition = rememberInfiniteTransition(label = "ad_anim")
                val tilt by infiniteTransition.animateFloat(
                    initialValue = -12f,
                    targetValue = 12f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ad_tilt"
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Aqua Sort Premium 3D",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "The #1 puzzle of the year is here!",
                        color = ColorSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Mock interactive tubes showing super cool designs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tube 1
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(150.dp)
                                .graphicsLayer { rotationZ = tilt }
                                .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(bottom = 6.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(35.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color(0xFFFFB703), Color(0xFFFB8500))
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        // Tube 2
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(150.dp)
                                .graphicsLayer { rotationZ = -tilt }
                                .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(bottom = 6.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                repeat(2) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(35.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color(0xFF22C55E), Color(0xFF15803D))
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "🔥 TAP TO PLAY FREE 🔥",
                        color = ColorAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .graphicsLayer {
                                val s = 1f + kotlin.math.sin(progressAnim.value * 20f) * 0.04f
                                scaleX = s
                                scaleY = s
                            }
                    )
                }
            }

            // 3. Premium Call-To-Action App Bar
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                    .clickable { showAppStoreDialog = true },
                cornerRadius = 20.dp,
                glowColor = ColorSecondary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Game icon thumbnail
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Aqua Sort Pro 3D",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "★ 4.9",
                                    color = ColorAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• 10M+ Installs • Free",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Install CTA
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(ColorSecondary, ColorSecondary.copy(alpha = 0.8f))
                                )
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "INSTALL",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Floating Responsive Skip Ad Button at bottom center, above the bottom CTA card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 116.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSkipEnabled) ColorPrimary else Color.White.copy(alpha = 0.1f)
                    )
                    .border(
                        1.dp,
                        if (isSkipEnabled) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(24.dp)
                    )
                    .clickable(enabled = isSkipEnabled) {
                        onClose()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSkipEnabled) "Skip Ad ➔" else "Skip in ${countdown}s",
                        color = if (isSkipEnabled) Color.White else Color.LightGray.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    if (isSkipEnabled) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Ad",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Simulated Play Store App Installation bottom dialog
        if (showAppStoreDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { showAppStoreDialog = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .clickable(enabled = false) {}, // Prevent clicks closing it
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Google Play",
                            color = Color(0xFF34A853),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ColorPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WaterDrop,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Aqua Sort Pro: Ultimate Sort Puzzle",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Water Sort & Liquid Puzzles",
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Contains Ads • In-app purchases",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        var downloadProgress by remember { mutableStateOf(0f) }
                        var isDownloading by remember { mutableStateOf(false) }
                        var downloadCompleted by remember { mutableStateOf(false) }

                        if (isDownloading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = downloadProgress,
                                    modifier = Modifier.weight(1f).height(6.dp),
                                    color = Color(0xFF00C2A8),
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                                Text(
                                    text = "${(downloadProgress * 100).toInt()}%",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    isDownloading = true
                                    downloadProgress = 0f
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C2A8)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (downloadCompleted) "OPEN APP" else "INSTALL NOW",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LaunchedEffect(isDownloading) {
                            if (isDownloading) {
                                while (downloadProgress < 1f) {
                                    delay(150)
                                    downloadProgress += 0.08f
                                }
                                downloadProgress = 1f
                                isDownloading = false
                                downloadCompleted = true
                                delay(800)
                                showAppStoreDialog = false
                                onClose() // Automatically returns to the game screen after successful mock download!
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TextButton(
                            onClick = { showAppStoreDialog = false }
                        ) {
                            Text("Cancel", color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

// --- TUBE COMPLETION CELEBRATION SPARKLES EFFECT ---
@Composable
fun TubeSortedCelebrationEffect(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    
    // Create random particles once
    val particles = remember {
        List(15) {
            val angle = (-Math.PI / 2 + (Math.random() * 1.2 - 0.6)).toFloat() // Pointing mostly upwards
            val speed = 120f + Math.random().toFloat() * 180f
            val gravity = 350f + Math.random().toFloat() * 200f
            val size = 4.dp + (Math.random() * 5).dp
            val color = when (Math.random() * 4) {
                in 0.0..1.0 -> Color(0xFF6C63FF) // ColorPrimary
                in 1.0..2.0 -> Color(0xFFFFB703) // ColorAccent
                in 2.0..3.0 -> Color(0xFF00C2A8) // ColorSecondary
                else -> Color(0xFF22C55E) // ColorSuccess
            }
            SparkleParticle(angle, speed, gravity, size, color)
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
        onFinished()
    }

    val currentProgress = progress.value
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val pProgress = currentProgress
            // Kinematic physics calculations
            val xOffset = cos(p.angle) * p.speed * pProgress
            val yOffset = sin(p.angle) * p.speed * pProgress + 0.5f * p.gravity * pProgress * pProgress
            
            val alpha = (1f - pProgress).coerceIn(0f, 1f)
            
            // Draw diamond-shaped sparkle
            val sizePx = p.size.toPx()
            val center = Offset(size.width / 2 + xOffset, size.height * 0.1f + yOffset)
            
            val path = Path().apply {
                moveTo(center.x, center.y - sizePx)
                lineTo(center.x + sizePx * 0.5f, center.y)
                lineTo(center.x, center.y + sizePx)
                lineTo(center.x - sizePx * 0.5f, center.y)
                close()
            }
            
            drawPath(
                path = path,
                color = p.color.copy(alpha = alpha)
            )
            
            // Add a smaller white glowing center for extra sparkliness
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = sizePx * 0.25f,
                center = center
            )
        }
    }
}

data class SparkleParticle(
    val angle: Float,
    val speed: Float,
    val gravity: Float,
    val size: Dp,
    val color: Color
)

// --- POTION RECIPE BOOK OVERLAY ---
@Composable
fun RecipeBookOverlay(
    currentLevel: Int,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Alchemical Recipe Book", onDismiss = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(AlchemicalRecipes) { recipe ->
                val isUnlocked = currentLevel > recipe.unlockLevel

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isUnlocked) GlassBg else Color(0x660B0E14))
                        .border(
                            1.dp,
                            if (isUnlocked) ColorPrimary.copy(alpha = 0.3f) else GlassBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(14.dp)
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalPharmacy,
                                        contentDescription = null,
                                        tint = ColorAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = recipe.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recipe.description,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Ingredient formula preview
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Formula:",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Ingredient 1 circle
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(recipe.color1.startColor)
                                        )
                                        Text(recipe.color1.name, color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Text("+", color = Color.Gray, fontSize = 10.sp)
                                    // Ingredient 2 circle
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(recipe.color2.startColor)
                                        )
                                        Text(recipe.color2.name, color = Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Mixed potion graphic (3D-like glowing mini flask)
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, ColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(32.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    
                                    // Draw flask body outline
                                    val path = Path().apply {
                                        moveTo(w * 0.4f, 0f)
                                        lineTo(w * 0.6f, 0f)
                                        lineTo(w * 0.6f, h * 0.3f)
                                        lineTo(w * 0.9f, h * 0.8f)
                                        quadraticTo(w * 0.95f, h * 0.95f, w * 0.8f, h)
                                        lineTo(w * 0.2f, h)
                                        quadraticTo(w * 0.05f, h * 0.95f, w * 0.1f, h * 0.8f)
                                        lineTo(w * 0.4f, h * 0.3f)
                                        close()
                                    }

                                    // Render fluid mixture inside flask using vertical gradient
                                    drawPath(
                                        path = path,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(recipe.color1.startColor, recipe.color2.endColor)
                                        )
                                    )

                                    // Flask glass outline
                                    drawPath(
                                        path = path,
                                        color = Color.White.copy(alpha = 0.5f),
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }
                            }
                        }
                    } else {
                        // Locked State
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Discovered Potion #${recipe.unlockLevel}",
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Unlocks by clearing Level ${recipe.unlockLevel}",
                                        color = Color.Gray.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- DAILY QUESTS BOARD OVERLAY ---
@Composable
fun DailyQuestsOverlay(
    quests: List<Quest>,
    onClaimQuest: (String) -> Unit,
    onDismiss: () -> Unit
) {
    OverlayDialog(title = "Daily Quests Board", onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColorPrimary.copy(alpha = 0.1f))
                    .border(1.dp, ColorPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Daily Alchemy Duties",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Complete your daily experiments to earn mystical reagents!",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                if (quests.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All quests complete for today!",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    items(quests) { quest ->
                        val isCompleted = quest.current >= quest.target
                        val isClaimed = quest.isClaimed

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isClaimed) Color.White.copy(alpha = 0.02f) else GlassBg)
                                .border(
                                    1.dp,
                                    when {
                                        isClaimed -> GlassBorder
                                        isCompleted -> ColorSuccess.copy(alpha = 0.5f)
                                        else -> ColorPrimary.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = quest.title,
                                        color = if (isClaimed) Color.Gray else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = quest.description,
                                        color = if (isClaimed) Color.DarkGray else Color.LightGray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                if (isClaimed) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.06f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Claimed ✓",
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                } else if (isCompleted) {
                                    Button(
                                        onClick = { onClaimQuest(quest.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = "CLAIM",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black.copy(alpha = 0.25f))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        if (quest.rewardCoins > 0) {
                                            Text(
                                                text = "${quest.rewardCoins}",
                                                color = ColorAccent,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("🪙", fontSize = 11.sp)
                                        }
                                        if (quest.rewardGems > 0) {
                                            if (quest.rewardCoins > 0) Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${quest.rewardGems}",
                                                color = ColorSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("💎", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            if (!isClaimed) {
                                Spacer(modifier = Modifier.height(12.dp))
                                val pct = (quest.current.toFloat() / quest.target.toFloat()).coerceIn(0f, 1f)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(pct)
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(ColorPrimary, ColorAccent)
                                                    )
                                                )
                                        )
                                    }
                                    Text(
                                        text = "${quest.current}/${quest.target}",
                                        color = if (isCompleted) ColorSuccess else Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
