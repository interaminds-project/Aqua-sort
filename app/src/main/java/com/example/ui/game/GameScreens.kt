package com.example.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Path
import kotlin.math.sin
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// --- SPLASH SCREEN ---
@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logo_rotate"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Rotating Alchemical Flask Logo Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(ColorPrimary.copy(alpha = 0.15f))
                    .border(2.dp, ColorPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = ColorSecondary,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(rotateAngle)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "AQUA SORT",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                style = MaterialTheme.typography.titleLarge.copy(
                    shadow = Shadow(
                        color = ColorPrimary.copy(alpha = 0.6f),
                        offset = Offset(0f, 3f),
                        blurRadius = 10f
                    )
                )
            )

            Text(
                text = "LIQUID ALCHEMY PUZZLE",
                color = ColorSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Loading bar
            CircularProgressIndicator(
                color = ColorSecondary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = "v2.5.0-premium",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .graphicsLayer { alpha = 0.7f }
            )
        }
    }
}

// --- WELCOME SCREEN ---
@Composable
fun WelcomeScreen(
    onPlayClick: () -> Unit,
    onShowSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF090E17), Color(0xFF161E2E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Liquids,
                null,
                tint = ColorPrimary,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "AQUA SORT",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    shadow = Shadow(
                        color = ColorPrimary,
                        offset = Offset(0f, 4f),
                        blurRadius = 12f
                    )
                )
            )

            Text(
                text = "The Modern Premium Liquid sorting Game",
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            PremiumButton(
                text = "START PLAYING",
                icon = Icons.Filled.PlayArrow,
                onClick = onPlayClick,
                gradientColors = listOf(ColorPrimary, ColorSecondary),
                modifier = Modifier.fillMaxWidth(),
                glowColor = ColorPrimary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PremiumButton(
                text = "SETTINGS",
                icon = Icons.Filled.Settings,
                onClick = onShowSettings,
                gradientColors = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Terms",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {}
                )
                Text(
                    text = "  •  ",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Privacy Policy",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {}
                )
                Text(
                    text = "  •  ",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "English",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {}
                )
            }
        }
    }
}

// Custom liquid icon placeholder for Welcome screen
private val Icons.Filled.Liquids: ImageVector get() = Icons.Filled.InvertColors

// --- HOME SCREEN ---
@Composable
fun HomeScreen(
    currentLevel: Int,
    selectedAvatarId: Int,
    coins: Int,
    gems: Int,
    lives: Int,
    quests: List<Quest>,
    eventTimeRemaining: String,
    onPlayCurrentLevel: () -> Unit,
    onShowDailyRewards: () -> Unit,
    onShowLuckyWheel: () -> Unit,
    onShowShop: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onShowAchievements: () -> Unit,
    onShowProfile: () -> Unit,
    onShowSettings: () -> Unit,
    onShowLevels: () -> Unit,
    onShowRecipeBook: () -> Unit,
    onShowDailyQuests: () -> Unit,
    onClaimQuest: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "play_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "play_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0E14), Color(0xFF151A24))
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .fillMaxHeight()
                .padding(bottom = 80.dp) // Leave space for bottom nav
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP HEADER STATUS ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(GlassBg)
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                        .clickable { onShowProfile() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val userAvatar = AvatarOptions.find { it.id == selectedAvatarId } ?: AvatarOptions.first()
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(userAvatar.color.copy(alpha = 0.2f))
                            .border(1.dp, userAvatar.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = userAvatar.icon,
                            contentDescription = null,
                            tint = userAvatar.color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text("Lvl $currentLevel", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusPill(
                        icon = Icons.Filled.Diamond,
                        value = "$gems",
                        iconColor = ColorSecondary,
                        onClick = onShowShop
                    )
                    StatusPill(
                        icon = Icons.Filled.MonetizationOn,
                        value = if (coins >= 1000) String.format("%.1fk", coins / 1000f) else "$coins",
                        iconColor = ColorAccent,
                        onClick = onShowShop
                    )
                }
            }

            // 2. DAILY REWARD CARD BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassBg)
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .clickable { onShowDailyRewards() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Reward",
                            color = ColorAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap to claim today's gift!",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColorAccent.copy(alpha = 0.2f))
                            .border(1.dp, ColorAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.CardGiftcard, null, tint = ColorAccent, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // 3. MAIN CENTER PLAY BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Concentric decorative pulsing halos
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer {
                            scaleX = pulseScale * 1.15f
                            scaleY = pulseScale * 1.15f
                            alpha = 0.15f
                        }
                        .background(ColorPrimary.copy(alpha = 0.15f), shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .shadow(32.dp, CircleShape, ambientColor = ColorPrimary, spotColor = ColorSecondary)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ColorPrimary, ColorSecondary)
                            )
                        )
                        .clickable { onPlayCurrentLevel() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(76.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PLAY NOW",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontSize = 22.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offset = Offset(0f, 3f),
                                    blurRadius = 6f
                                )
                            )
                        )
                    }
                }
            }

            // Level badge indicator below the center button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Level $currentLevel",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. ACTION GRID BUTTONS (Shop, Rank, Pass, Goals)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("Shop", Icons.Filled.ShoppingCart, onShowShop),
                    Triple("Rank", Icons.Filled.Leaderboard, onShowLeaderboard),
                    Triple("Recipes", Icons.Filled.MenuBook, onShowRecipeBook),
                    Triple("Goals", Icons.Filled.EmojiEvents, onShowAchievements)
                ).forEach { (name, icon, action) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassBg)
                            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                            .clickable { action() }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = when (name) {
                                    "Shop" -> ColorSecondary
                                    "Rank" -> ColorPrimary
                                    "Recipes" -> ColorAccent
                                    else -> ColorWarning
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4a. DAILY QUESTS BOARD PREVIEW CARD
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassBg)
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .clickable { onShowDailyQuests() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            val activeCount = quests.count { !it.isClaimed }
                            Text(
                                text = "$activeCount Active Quests",
                                color = ColorPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Daily Quests Board",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Complete experiments to claim coins & gems!",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorPrimary.copy(alpha = 0.15f))
                            .border(1.dp, ColorPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ListAlt,
                            contentDescription = "Quests",
                            tint = ColorPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // 4b. POTION RECIPE BOOK CARD
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1E1430), Color(0xFF100B1A))
                            )
                        )
                        .border(1.dp, ColorSecondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .clickable { onShowRecipeBook() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorSecondary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Alchemy Codex", color = ColorSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Potion Recipe Book",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Discover unlocked formulas & mixtures!",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorSecondary.copy(alpha = 0.15f))
                            .border(1.dp, ColorSecondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoStories,
                            contentDescription = "Recipe Book",
                            tint = ColorSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // 5. ACTIVE ALCHEMIST EVENT WIDGET (TICKING COUNTDOWN!)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF2E1C0A), Color(0xFF1C1105))
                            )
                        )
                        .border(1.dp, ColorWarning.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .clickable { onShowDailyQuests() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorWarning.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Active Alchemist Event", color = ColorWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Solar Eclipse", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("Earn DOUBLE gems on all levels!", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = ColorWarning,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Ends in: $eventTimeRemaining",
                                color = ColorWarning,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ColorWarning.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = ColorWarning,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // 6. BOTTOM NAVIGATION BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(12.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xEB0D1117))
                .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(Icons.Filled.Settings, onShowSettings, size = 44.dp, tint = Color.LightGray)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ColorPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Home, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                GlassIconButton(Icons.Filled.GridView, onShowLevels, size = 44.dp, tint = Color.LightGray)
                GlassIconButton(Icons.Filled.People, onShowLeaderboard, size = 44.dp, tint = Color.LightGray)
            }
        }
    }
}

// --- WORLD SELECTION SCREEN ---
@Composable
fun WorldSelectionScreen(
    currentLevel: Int,
    claimedSectionRewards: String = "",
    onClaimSectionReward: (String) -> Unit = {},
    onWorldSelect: (Int) -> Unit,
    onBack: () -> Unit
) {
    val claimedList = remember(claimedSectionRewards) {
        claimedSectionRewards.split(",").filter { it.isNotEmpty() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF090E17), Color(0xFF161E2E))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(Icons.AutoMirrored.Filled.ArrowBack, onBack, size = 40.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Alchemical Chapters", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("500+ Deterministic Levels across 8 Chapters", color = ColorAccent, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LevelSectionsList.forEachIndexed { index, sec ->
                    val isUnlocked = currentLevel >= sec.startLevel
                    val isCompleted = currentLevel > sec.endLevel
                    val progress = when {
                        currentLevel <= sec.startLevel -> 0
                        currentLevel > sec.endLevel -> 100
                        else -> ((currentLevel - sec.startLevel) * 100 / (sec.endLevel - sec.startLevel + 1)).coerceIn(0, 100)
                    }
                    val isRewardClaimed = claimedList.contains(sec.id)
                    val canClaimReward = isCompleted && !isRewardClaimed

                    WorldCard(
                        chapterNum = index + 1,
                        title = sec.name,
                        desc = sec.description,
                        levelRange = sec.subtitle,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        progress = progress,
                        canClaimReward = canClaimReward,
                        isRewardClaimed = isRewardClaimed,
                        rewardText = "+${sec.rewardCoins} Coins | +${sec.rewardGems} Gems",
                        onClaimReward = { onClaimSectionReward(sec.id) },
                        onClick = { onWorldSelect(sec.startLevel) }
                    )
                }
            }
        }
    }
}

@Composable
fun WorldCard(
    chapterNum: Int,
    title: String,
    desc: String,
    levelRange: String,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    progress: Int,
    canClaimReward: Boolean,
    isRewardClaimed: Boolean,
    rewardText: String,
    onClaimReward: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isUnlocked) GlassBg else Color.Black.copy(alpha = 0.5f))
            .border(1.dp, if (isUnlocked) ColorSecondary.copy(alpha = 0.35f) else GlassBorder, RoundedCornerShape(24.dp))
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ch. $chapterNum: $title", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (!isUnlocked) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Lock, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                } else if (isCompleted) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.CheckCircle, null, tint = ColorSecondary, modifier = Modifier.size(18.dp))
                }
            }
            Text(desc, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(levelRange, color = ColorAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (canClaimReward) {
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("🎁 Claim Reward", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isRewardClaimed) {
                    Text("✓ Reward Claimed", color = ColorSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(rewardText, color = Color.Gray, fontSize = 11.sp)
                }
            }
            
            if (isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress / 100f)
                                .fillMaxHeight()
                                .background(ColorSecondary)
                        )
                    }
                    Text("$progress%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- LEVEL SELECTION SCREEN ---
@Composable
fun LevelSelectionScreen(
    currentLevel: Int,
    initialSectionIndex: Int = -1,
    onLevelSelect: (Int) -> Unit,
    onBack: () -> Unit
) {
    val autoSectionIdx = remember(currentLevel) {
        val found = LevelSectionsList.indexOfFirst { currentLevel in it.startLevel..it.endLevel }
        if (found >= 0) found else LevelSectionsList.indexOfLast { currentLevel >= it.startLevel }.coerceAtLeast(0)
    }
    var selectedSecIdx by remember(currentLevel, initialSectionIndex) {
        mutableStateOf(if (initialSectionIndex in 0..LevelSectionsList.lastIndex) initialSectionIndex else autoSectionIdx)
    }
    val currentSec = LevelSectionsList[selectedSecIdx]
    val levels = remember(selectedSecIdx) { (currentSec.startLevel..currentSec.endLevel).toList() }

    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val tabScrollState = rememberScrollState()

    // Scroll level grid to current level if in active section
    LaunchedEffect(selectedSecIdx, currentLevel) {
        val lvlIndex = levels.indexOf(currentLevel)
        if (lvlIndex >= 0) {
            gridState.scrollToItem(lvlIndex)
        } else {
            gridState.scrollToItem(0)
        }
    }

    // Scroll horizontal chapter tabs to active chapter tab
    LaunchedEffect(selectedSecIdx) {
        tabScrollState.animateScrollTo((selectedSecIdx * 110).coerceAtLeast(0))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF090E17), Color(0xFF161E2E))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(Icons.AutoMirrored.Filled.ArrowBack, onBack, size = 40.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Select Level", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Chapter ${selectedSecIdx + 1}: ${currentSec.name} (${currentSec.subtitle})", color = ColorAccent, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Section Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(tabScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LevelSectionsList.forEachIndexed { idx, sec ->
                    val isTabSelected = idx == selectedSecIdx
                    val isUnlocked = currentLevel >= sec.startLevel
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isTabSelected) ColorPrimary else if (isUnlocked) GlassBg else Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, if (isTabSelected) ColorAccent else GlassBorder, RoundedCornerShape(20.dp))
                            .clickable(enabled = isUnlocked) { selectedSecIdx = idx }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = sec.name,
                                color = if (isTabSelected) Color.White else if (isUnlocked) Color.LightGray else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (!isUnlocked) {
                                Icon(Icons.Filled.Lock, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(levels) { lvl ->
                    val isLocked = lvl > currentLevel
                    val isCurrent = lvl == currentLevel
                    val isSolved = lvl < currentLevel

                    val bg = when {
                        isLocked -> Color.Black.copy(alpha = 0.4f)
                        isCurrent -> ColorPrimary
                        else -> ColorSecondary.copy(alpha = 0.25f)
                    }

                    val borderColor = when {
                        isCurrent -> ColorAccent
                        isSolved -> ColorSecondary
                        else -> GlassBorder
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isLocked) { onLevelSelect(lvl) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLocked) {
                            Icon(Icons.Filled.Lock, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$lvl",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                if (isSolved) {
                                    Icon(Icons.Filled.Check, null, tint = ColorSecondary, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- GAMEPLAY SCREEN ---
@Composable
fun GameplayScreen(
    levelNumber: Int,
    tubes: List<List<Int>>,
    selectedTubeIndex: Int?,
    movesCount: Int,
    maxMoves: Int,
    timerSeconds: Int,
    extraTubeAdded: Boolean,
    activePour: GameViewModel.PourAnimationState?,
    onCompletePour: () -> Unit,
    skinType: String,
    onSelectTube: (Int) -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onAddTube: () -> Unit,
    onSkipLevel: () -> Unit,
    onPause: () -> Unit,
    completedTubeAnimationIndex: Int?,
    onClearCompletedTubeAnimation: () -> Unit,
    activePowerUp: PowerUpType = PowerUpType.NONE,
    frozenTubeIndices: Set<Int> = emptySet(),
    volatileTubeIndex: Int? = null,
    volatileMovesLeft: Int = 5,
    portalPairs: Map<Int, Int> = emptyMap(),
    crystalLockTubeIndex: Int? = null,
    onToggleSwapper: () -> Unit = {},
    onToggleCatalyst: () -> Unit = {}
) {
    // Coordinate tracking maps for aligning the pouring stream
    val tubePositions = remember { mutableStateMapOf<Int, Offset>() }
    var boxRootPosition by remember { mutableStateOf(Offset.Zero) }
    val streamPath = remember { Path() }

    // Smooth progress animation for pouring action
    val animatableProgress = remember { Animatable(0f) }
    
    // Wave ripple phase for stream wobble - optimized State to avoid 60fps full-screen recompositions
    val infiniteTransition = rememberInfiniteTransition(label = "stream_wobble")
    val wavePhaseState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stream_wave"
    )

    LaunchedEffect(activePour) {
        if (activePour != null) {
            animatableProgress.snapTo(0f)
            animatableProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
            )
            onCompletePour()
        } else {
            animatableProgress.snapTo(0f)
        }
    }

    val pourProgress = animatableProgress.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0E14), Color(0xFF151A24))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP NAV BAR (Timer, Level, Pause)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Timer, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    val mins = timerSeconds / 60
                    val secs = timerSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Level text
                Text(
                    text = "LEVEL $levelNumber",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                // Pause button (disable during pour to avoid state inconsistencies)
                GlassIconButton(
                    icon = Icons.Filled.Pause,
                    onClick = { if (activePour == null) onPause() },
                    size = 36.dp,
                    tint = if (activePour == null) Color.White else Color.Gray
                )
            }

            val movesLeft = maxMoves - movesCount
            val movesColor = if (movesLeft <= 3) ColorDanger else Color.Gray
            val currentStars = when {
                movesCount <= maxMoves * 0.5 -> 3
                movesCount <= maxMoves * 0.75 -> 2
                else -> 1
            }

            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Moves Count indicator
                Text(
                    text = if (movesLeft <= 3) "⚠️ Moves: $movesCount / $maxMoves" else "Moves: $movesCount / $maxMoves",
                    color = movesColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // Stars meter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { starIdx ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (starIdx < currentStars) ColorAccent else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Active Power-up Banner
            if (activePowerUp != PowerUpType.NONE) {
                val bannerColor = if (activePowerUp == PowerUpType.SWAPPER) ColorAccent else Color(0xFF38BDF8)
                val bannerBg = if (activePowerUp == PowerUpType.SWAPPER) ColorAccent.copy(alpha = 0.15f) else Color(0xFF38BDF8).copy(alpha = 0.15f)
                val bannerBorder = if (activePowerUp == PowerUpType.SWAPPER) ColorAccent.copy(alpha = 0.4f) else Color(0xFF38BDF8).copy(alpha = 0.4f)
                val bannerText = if (activePowerUp == PowerUpType.SWAPPER) {
                    "🧪 TRANSMUTATION ACTIVE: Tap a tube with >= 2 layers to swap its top 2 colors!"
                } else {
                    "❄️ FREEZE POTION ACTIVE: Tap a tube to freeze it (prevents future pouring in)!"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bannerBg)
                        .border(1.dp, bannerBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        bannerText,
                        color = bannerColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tutorial Banner if level 1
            if (levelNumber == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ColorPrimary.copy(alpha = 0.2f))
                        .border(1.dp, ColorPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        "🧪 Tap a tube to select, then tap another tube with matching top color to pour!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. TUBES AREA (With live pouring streams, translations, and tilts)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .onGloballyPositioned { coordinates ->
                        boxRootPosition = coordinates.positionInRoot()
                    },
                contentAlignment = Alignment.Center
            ) {
                // Main render block for tubes
                val onRenderTube = @Composable { idx: Int, tubeColors: List<Int> ->
                    val isSource = activePour?.srcIndex == idx
                    val isDest = activePour?.destIndex == idx
                    
                    val tiltAngle = if (isSource && activePour != null) {
                        val maxAngle = if (activePour.destIndex > activePour.srcIndex) 75f else -75f
                        when {
                            pourProgress < 0.22f -> (pourProgress / 0.22f) * (maxAngle * 0.18f)
                            pourProgress < 0.78f -> {
                                val t = (pourProgress - 0.22f) / 0.56f
                                (maxAngle * 0.18f) + t * (maxAngle * 0.82f)
                            }
                            else -> {
                                val t = (1f - pourProgress) / 0.22f
                                t * maxAngle
                            }
                        }
                    } else 0f
                    
                    val pourFraction = when {
                        pourProgress < 0.22f -> 0f
                        pourProgress > 0.78f -> 1f
                        else -> (pourProgress - 0.22f) / 0.56f
                    }
                    
                    val densityVal = LocalDensity.current
                    val translationOffset = if (isSource && activePour != null) {
                        val srcPos = tubePositions[activePour.srcIndex] ?: Offset.Zero
                        val destPos = tubePositions[activePour.destIndex] ?: Offset.Zero
                        val direction = if (activePour.destIndex > activePour.srcIndex) 1f else -1f
                        val targetOffset = if (srcPos != Offset.Zero && destPos != Offset.Zero) {
                            with(densityVal) {
                                destPos - srcPos + Offset(
                                    x = -direction * 30.dp.toPx(),
                                    y = -84.dp.toPx()
                                )
                            }
                        } else Offset.Zero
                        
                        when {
                            pourProgress < 0.22f -> targetOffset * (pourProgress / 0.22f)
                            pourProgress < 0.78f -> targetOffset
                            else -> targetOffset * ((1f - pourProgress) / 0.22f)
                        }
                    } else Offset.Zero

                    val isVolatile = idx == volatileTubeIndex
                    val isFrozen = frozenTubeIndices.contains(idx)

                    Box(contentAlignment = Alignment.TopCenter) {
                        LiquidTube(
                            colors = tubeColors,
                            isSelected = selectedTubeIndex == idx,
                            onClick = { if (activePour == null) onSelectTube(idx) },
                            skinType = skinType,
                            tiltAngle = tiltAngle,
                            pourFraction = pourFraction,
                            isSource = isSource,
                            isDest = isDest,
                            pouredColorId = activePour?.colorId,
                            pourAmount = activePour?.amount ?: 0,
                            translationOffset = translationOffset,
                            levelNumber = levelNumber,
                            isVolatile = isVolatile,
                            volatileMovesLeft = volatileMovesLeft,
                            isFrozen = isFrozen,
                            portalPairId = portalPairs[idx],
                            isCrystalLocked = (crystalLockTubeIndex == idx),
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                tubePositions[idx] = coordinates.positionInRoot()
                            }
                        )

                        if (completedTubeAnimationIndex == idx) {
                            TubeSortedCelebrationEffect(
                                onFinished = onClearCompletedTubeAnimation,
                                modifier = Modifier
                                    .size(width = 80.dp, height = 180.dp)
                                    .graphicsLayer {
                                        translationY = -35.dp.toPx()
                                    }
                            )
                        }
                    }
                }

                val maxTubesPerRow = 5
                val rowCount = (tubes.size + maxTubesPerRow - 1) / maxTubesPerRow
                val baseSize = tubes.size / rowCount
                val extra = tubes.size % rowCount
                
                val rows = mutableListOf<List<Pair<Int, List<Int>>>>()
                var currentIdx = 0
                for (r in 0 until rowCount) {
                    val size = baseSize + (if (r < extra) 1 else 0)
                    val rowList = mutableListOf<Pair<Int, List<Int>>>()
                    for (i in 0 until size) {
                        rowList.add(currentIdx to tubes[currentIdx])
                        currentIdx++
                    }
                    rows.add(rowList)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rows.forEach { rowTubes ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowTubes.forEach { (idx, tubeColors) ->
                                onRenderTube(idx, tubeColors)
                            }
                        }
                    }
                }

                // 2b. ACTIVE NEON POURING STREAM LAYER
                if (activePour != null && pourProgress in 0.22f..0.78f) {
                    val srcPos = tubePositions[activePour.srcIndex]
                    val destPos = tubePositions[activePour.destIndex]
                    if (srcPos != null && destPos != null && boxRootPosition != Offset.Zero) {
                        val densityVal = LocalDensity.current
                        with(densityVal) {
                            val tubeWidthPx = 60.dp.toPx()
                            val mouthYPx = 4.dp.toPx()
                            
                            val direction = if (activePour.destIndex > activePour.srcIndex) 1f else -1f
                            val targetOffset = destPos - srcPos + Offset(
                                x = -direction * 30.dp.toPx(),
                                y = -84.dp.toPx()
                            )
                            
                            val currentOffset = when {
                                pourProgress < 0.22f -> targetOffset * (pourProgress / 0.22f)
                                pourProgress < 0.78f -> targetOffset
                                else -> targetOffset * ((1f - pourProgress) / 0.22f)
                            }
                            
                            val startPoint = srcPos - boxRootPosition + currentOffset + Offset(tubeWidthPx / 2, mouthYPx)
                            val endPoint = destPos - boxRootPosition + Offset(tubeWidthPx / 2, mouthYPx + 12.dp.toPx())
                            
                            val liquidColor = LiquidColors.find { it.id == activePour.colorId }
                            if (liquidColor != null) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    streamPath.reset()
                                    streamPath.apply {
                                        val widthPx = 8.dp.toPx()
                                        val wobble = sin(wavePhaseState.value * 5f) * 1.5.dp.toPx()
                                        
                                        moveTo(startPoint.x - widthPx / 2, startPoint.y)
                                        quadraticTo(
                                            (startPoint.x + endPoint.x) / 2 + wobble,
                                            (startPoint.y + endPoint.y) / 2,
                                            endPoint.x - widthPx / 2,
                                            endPoint.y
                                        )
                                        lineTo(endPoint.x + widthPx / 2, endPoint.y)
                                        quadraticTo(
                                            (startPoint.x + endPoint.x) / 2 + wobble,
                                            (startPoint.y + endPoint.y) / 2,
                                            startPoint.x + widthPx / 2,
                                            startPoint.y
                                        )
                                        close()
                                    }
                                    
                                    // Neon water column gradient
                                    drawPath(
                                        path = streamPath,
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(liquidColor.startColor, liquidColor.endColor)
                                        )
                                    )
                                    
                                    // Glowing highlight stroke
                                    drawPath(
                                        path = streamPath,
                                        color = Color.White.copy(alpha = 0.45f),
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- POWER-UP SELECTION ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PowerUpButton(
                    name = "Swapper",
                    cost = 150,
                    icon = Icons.Filled.SwapVert,
                    isActive = activePowerUp == PowerUpType.SWAPPER,
                    onClick = { if (activePour == null) onToggleSwapper() }
                )
                PowerUpButton(
                    name = "Catalyst",
                    cost = 100,
                    icon = Icons.Filled.AutoAwesome,
                    isActive = activePowerUp == PowerUpType.CATALYST,
                    onClick = { if (activePour == null) onToggleCatalyst() }
                )
            }

            // 3. BOTTOM BUTTONS ROW (Undo, Restart, Hint, Skip - disabled during animations)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0x9E090D14))
                    .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        icon = Icons.Filled.Undo,
                        onClick = { if (activePour == null) onUndo() },
                        size = 48.dp,
                        tint = if (activePour == null) Color.White else Color.Gray
                    )
                    GlassIconButton(
                        icon = Icons.Filled.Refresh,
                        onClick = { if (activePour == null) onRestart() },
                        size = 48.dp,
                        tint = if (activePour == null) Color.White else Color.Gray
                    )
                    
                    // Center high glow Help button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, CircleShape, ambientColor = if (activePour == null) ColorPrimary else Color.Transparent, spotColor = if (activePour == null) ColorPrimary else Color.Transparent)
                            .clip(CircleShape)
                            .background(if (activePour == null) ColorPrimary else Color.Gray)
                            .clickable(enabled = activePour == null) { onAddTube() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "Add Tube Helper",
                            tint = if (activePour == null) ColorAccent else Color.LightGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    GlassIconButton(
                        icon = Icons.Filled.SkipNext,
                        onClick = { if (activePour == null) onSkipLevel() },
                        size = 48.dp,
                        tint = if (activePour == null) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PowerUpButton(
    name: String,
    cost: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val borderCol = if (isActive) ColorAccent else GlassBorder
    val bgCol = if (isActive) ColorAccent.copy(alpha = 0.25f) else Color(0x66090D14)
    val textCol = if (isActive) ColorAccent else Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bgCol)
                .border(1.2.dp, borderCol, RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isActive) ColorAccent else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = name,
                color = textCol,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Cost badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MonetizationOn,
                contentDescription = "Coins",
                tint = Color(0xFFFFB703),
                modifier = Modifier.size(10.dp)
            )
            Text(
                text = "$cost",
                color = Color(0xFFFFB703),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
