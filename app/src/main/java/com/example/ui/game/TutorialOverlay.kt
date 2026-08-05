package com.example.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.sin

// Define Tutorial Info structure
data class TutorialInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val requiredLevel: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val avoidText: String,
    val tackleText: String,
    val detailedDesc: String
)

val TutorialsList = listOf(
    TutorialInfo(
        id = "cyber_city",
        title = "World 1: Cyber City",
        subtitle = "Basic Liquids Sorting",
        requiredLevel = 1,
        primaryColor = Color(0xFF6C63FF),
        secondaryColor = Color(0xFF00C2A8),
        avoidText = "Don't pour mismatched colors on top of each other! Empty tubes are extremely valuable; don't block them early.",
        tackleText = "Focus on clearing one color completely into an empty tube to create buffer space. Plan 2 moves ahead!",
        detailedDesc = "The basic law of alchemy. Select a tube and pour into another only if the top liquid colors match or the target tube is empty."
    ),
    TutorialInfo(
        id = "emerald_forest",
        title = "World 2: Emerald Forest",
        subtitle = "Color Catalyst Pours",
        requiredLevel = 11,
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF34D399),
        avoidText = "Don't pour single blocks slowly when multiple matching blocks can be Catalyst-Poured together!",
        tackleText = "Align matching colors to trigger Catalyst Pours that transfer all connected identical layers in one smooth flow!",
        detailedDesc = "Catalyst reactions pour all contiguous matching liquid blocks instantly in a single rapid flow."
    ),
    TutorialInfo(
        id = "desert_sunset",
        title = "World 3: Desert Sunset",
        subtitle = "Frost-Bound Vials & Thaw Matches",
        requiredLevel = 31,
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFEF4444),
        avoidText = "Never leave frost-locked tubes ignored until the end when space is tight!",
        tackleText = "Pour matching liquid or complete an adjacent tube to shatter the frost, or use the Freeze/Thaw power-up!",
        detailedDesc = "Frost-bound tubes are locked in ice. Melt ice by matching adjacent tubes or using the Freeze/Thaw power-up."
    ),
    TutorialInfo(
        id = "chameleon_realm",
        title = "World 4: Chameleon Realm",
        subtitle = "Dual-Color Chameleon Adapter",
        requiredLevel = 61,
        primaryColor = Color(0xFFD946EF),
        secondaryColor = Color(0xFF06B6D4),
        avoidText = "Don't waste empty tubes when a Chameleon fluid layer is available as a wild-card bridge!",
        tackleText = "Pour any color onto Chameleon fluid—it dynamically transforms to match whatever liquid touches it first!",
        detailedDesc = "Chameleon fluid is a magical wild-card adapter that automatically transforms to match any liquid color."
    ),
    TutorialInfo(
        id = "portal_warp",
        title = "World 5: Elemental Forge",
        subtitle = "Portal Tube Networks",
        requiredLevel = 101,
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFEC4899),
        avoidText = "Don't forget that Portal Alpha and Portal Beta share a connected spatial link across the grid!",
        tackleText = "Pouring liquid into Portal Alpha automatically warps and fills into Portal Beta if space is available!",
        detailedDesc = "Linked Portal Orbs instantly teleport liquids across distant tubes on the board."
    ),
    TutorialInfo(
        id = "crystal_locks",
        title = "World 6: Astral Laboratory",
        subtitle = "Crystal Lock & Key Vials",
        requiredLevel = 181,
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFF10B981),
        avoidText = "Don't try to force liquid into a Crystal-Locked vial before filling its key tube!",
        tackleText = "Complete the designated Key Tube to shatter the Golden Crystal Padlock and earn 50 bonus gems!",
        detailedDesc = "Crystal Padlocks lock tubes completely. Complete the matching Key Tube to shatter the lock and claim rewards."
    ),
    TutorialInfo(
        id = "archmage_fusion",
        title = "World 7: Grand Archmage Sanctum",
        subtitle = "Archmage Fusion Chambers",
        requiredLevel = 301,
        primaryColor = Color(0xFFEAB308),
        secondaryColor = Color(0xFF8B5CF6),
        avoidText = "Don't let your Mana meter sit full without unleashing Archmage Divine Energy!",
        tackleText = "Fill Fusion Tubes to trigger divine rainbow explosions, earning double gems and free hints!",
        detailedDesc = "Master-tier fusion chambers charge your Mana meter, granting divine explosions and instant power-ups."
    )
)

@Composable
fun TutorialOverlay(
    tutorialId: String,
    onDismiss: () -> Unit
) {
    val info = TutorialsList.find { it.id == tutorialId } ?: TutorialsList.first()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .clickable(enabled = false) {} // Prevent click-through
            ) {
                // Outer glow & border card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF0B0F19))
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(info.primaryColor, info.secondaryColor, info.primaryColor)),
                            RoundedCornerShape(32.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = info.title,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = info.subtitle,
                                    color = info.secondaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scrollable Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Animated Demo Canvas Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (info.id) {
                                    "cyber_city" -> CyberCityAnimation()
                                    "emerald_forest" -> EmeraldForestAnimation()
                                    "desert_sunset" -> DesertSunsetAnimation()
                                    "chameleon_realm" -> ChameleonAnimation()
                                    "portal_warp" -> PortalAnimation()
                                    "crystal_locks" -> CrystalLocksAnimation()
                                    "archmage_fusion" -> ArchmageFusionAnimation()
                                    else -> CyberCityAnimation()
                                }
                            }

                            // Description Text
                            Text(
                                text = info.detailedDesc,
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )

                            // What to Avoid Section (Red Alert)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x1AEF4444))
                                    .border(1.dp, Color(0x33EF4444), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Cancel,
                                    contentDescription = "Avoid",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        "WHAT TO AVOID",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = info.avoidText,
                                        color = Color.LightGray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            // How to Tackle Section (Green Match)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x1A10B981))
                                    .border(1.dp, Color(0x3310B981), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Tackle",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        "HOW TO TACKLE",
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = info.tackleText,
                                        color = Color.LightGray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Understood Button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = info.primaryColor
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "UNDERSTOOD",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberCityAnimation() {
    val transition = rememberInfiniteTransition(label = "cyber_city_pour")
    
    // Animate sorting progress cycle
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f
        val tubeWidth = 32.dp.toPx()
        val tubeHeight = 100.dp.toPx()
        
        // Coordinates for Left and Right tubes
        val leftX = center - 60.dp.toPx()
        val rightX = center + 20.dp.toPx()
        val baseLineY = centerY + 30.dp.toPx()
        
        // Pour phases:
        // 0.0 - 0.2: Normal upright tubes
        // 0.2 - 0.3: Left tube lifts and starts tilting
        // 0.3 - 0.7: Liquid stream pours, Left empty grows, Right fills
        // 0.7 - 0.8: Left tube tilts back and lowers
        // 0.8 - 1.0: Normal upright tubes, state resets
        
        val leftTilt: Float
        val leftOffset: Offset
        val leftPurpleLevel: Float = 0.25f // Bottom segment remains purple
        val leftBlueLevel: Float // Top blue segment pours out
        val rightBlueLevel: Float // Right tube fills up
        val streamActive: Boolean
        
        when {
            progress < 0.2f -> {
                leftTilt = 0f
                leftOffset = Offset(leftX, baseLineY)
                leftBlueLevel = 0.25f
                rightBlueLevel = 0.25f
                streamActive = false
            }
            progress < 0.3f -> {
                val p = (progress - 0.2f) / 0.1f
                leftTilt = p * 45f
                leftOffset = Offset(leftX + p * 40.dp.toPx(), baseLineY - p * 30.dp.toPx())
                leftBlueLevel = 0.25f
                rightBlueLevel = 0.25f
                streamActive = false
            }
            progress < 0.7f -> {
                val p = (progress - 0.3f) / 0.4f
                leftTilt = 45f + sin(p * Math.PI.toFloat()) * 5f // Small shaking
                leftOffset = Offset(leftX + 40.dp.toPx(), baseLineY - 30.dp.toPx())
                leftBlueLevel = 0.25f * (1f - p)
                rightBlueLevel = 0.25f + 0.25f * p
                streamActive = p < 0.95f
            }
            progress < 0.8f -> {
                val p = (progress - 0.7f) / 0.1f
                leftTilt = 45f * (1f - p)
                leftOffset = Offset(leftX + 40.dp.toPx() * (1f - p), baseLineY - 30.dp.toPx() * (1f - p))
                leftBlueLevel = 0f
                rightBlueLevel = 0.5f
                streamActive = false
            }
            else -> {
                leftTilt = 0f
                leftOffset = Offset(leftX, baseLineY)
                leftBlueLevel = 0f
                rightBlueLevel = 0.5f
                streamActive = false
            }
        }

        // --- DRAW RIGHT TUBE ---
        // Tube glass outline
        val rightOutlinePath = Path().apply {
            moveTo(rightX, baseLineY - tubeHeight)
            lineTo(rightX, baseLineY - 8.dp.toPx())
            quadraticTo(rightX, baseLineY, rightX + 8.dp.toPx(), baseLineY)
            lineTo(rightX + tubeWidth - 8.dp.toPx(), baseLineY)
            quadraticTo(rightX + tubeWidth, baseLineY, rightX + tubeWidth, baseLineY - 8.dp.toPx())
            lineTo(rightX + tubeWidth, baseLineY - tubeHeight)
        }
        
        // Bottom Purple layer in right tube
        drawRoundRect(
            color = Color(0xFF8B5CF6),
            topLeft = Offset(rightX + 2.dp.toPx(), baseLineY - 25.dp.toPx()),
            size = Size(tubeWidth - 4.dp.toPx(), 24.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        // Blue layers in right tube
        if (rightBlueLevel > 0f) {
            val rightBlueHeight = (tubeHeight - 8.dp.toPx()) * rightBlueLevel
            drawRoundRect(
                color = Color(0xFF3B82F6),
                topLeft = Offset(rightX + 2.dp.toPx(), baseLineY - 25.dp.toPx() - rightBlueHeight),
                size = Size(tubeWidth - 4.dp.toPx(), rightBlueHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
        
        // Draw Glass Outline
        drawPath(rightOutlinePath, Color.White.copy(alpha = 0.3f), style = Stroke(width = 2.dp.toPx()))

        // --- DRAW STREAM ---
        if (streamActive) {
            drawRect(
                color = Color(0xFF3B82F6),
                topLeft = Offset(leftOffset.x + tubeWidth / 2, leftOffset.y - tubeHeight),
                size = Size(4.dp.toPx(), baseLineY - leftOffset.y + tubeHeight - 20.dp.toPx())
            )
        }

        // --- DRAW LEFT TUBE (with rotation and offset) ---
        // Save current layer state to apply translation/rotation
        drawContext.canvas.save()
        drawContext.canvas.translate(leftOffset.x + tubeWidth / 2, leftOffset.y - tubeHeight / 2)
        drawContext.canvas.rotate(leftTilt)
        drawContext.canvas.translate(-(leftOffset.x + tubeWidth / 2), -(leftOffset.y - tubeHeight / 2))

        val leftLocalX = leftOffset.x
        val leftLocalY = leftOffset.y

        // Liquids in left tube
        // Bottom Purple segment
        drawRoundRect(
            color = Color(0xFF8B5CF6),
            topLeft = Offset(leftLocalX + 2.dp.toPx(), leftLocalY - 25.dp.toPx()),
            size = Size(tubeWidth - 4.dp.toPx(), 24.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        // Blue segment
        if (leftBlueLevel > 0f) {
            val leftBlueHeight = (tubeHeight - 8.dp.toPx()) * leftBlueLevel
            drawRoundRect(
                color = Color(0xFF3B82F6),
                topLeft = Offset(leftLocalX + 2.dp.toPx(), leftLocalY - 25.dp.toPx() - leftBlueHeight),
                size = Size(tubeWidth - 4.dp.toPx(), leftBlueHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // Left glass outline
        val leftOutlinePath = Path().apply {
            moveTo(leftLocalX, leftLocalY - tubeHeight)
            lineTo(leftLocalX, leftLocalY - 8.dp.toPx())
            quadraticTo(leftLocalX, leftLocalY, leftLocalX + 8.dp.toPx(), leftLocalY)
            lineTo(leftLocalX + tubeWidth - 8.dp.toPx(), leftLocalY)
            quadraticTo(leftLocalX + tubeWidth, leftLocalY, leftLocalX + tubeWidth, leftLocalY - 8.dp.toPx())
            lineTo(leftLocalX + tubeWidth, leftLocalY - tubeHeight)
        }
        drawPath(leftOutlinePath, Color.White.copy(alpha = 0.3f), style = Stroke(width = 2.dp.toPx()))

        drawContext.canvas.restore()
    }
}

@Composable
fun EmeraldForestAnimation() {
    val transition = rememberInfiniteTransition(label = "emerald_frost")
    
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f
        val tubeWidth = 40.dp.toPx()
        val tubeHeight = 110.dp.toPx()
        val tubeX = center - tubeWidth / 2f
        val tubeY = centerY + 35.dp.toPx()

        // 0.0 - 0.4: Tube is frozen solid (thick blue/white overlay)
        // 0.4 - 0.5: Sun ray glows, hits the tube (shaking)
        // 0.5 - 0.7: Ice melts/cracks, opacity drops to 0f
        // 0.7 - 1.0: Tube is unfrozen and glowing green, showing success
        
        val frostAlpha: Float
        val successGlowAlpha: Float
        val isShaking: Boolean
        val sunRayAlpha: Float

        when {
            progress < 0.4f -> {
                frostAlpha = 0.85f
                successGlowAlpha = 0f
                isShaking = false
                sunRayAlpha = 0f
            }
            progress < 0.5f -> {
                val p = (progress - 0.4f) / 0.1f
                frostAlpha = 0.85f
                successGlowAlpha = 0f
                isShaking = true
                sunRayAlpha = p
            }
            progress < 0.7f -> {
                val p = (progress - 0.5f) / 0.2f
                frostAlpha = 0.85f * (1f - p)
                successGlowAlpha = p * 0.4f
                isShaking = true
                sunRayAlpha = 1f
            }
            else -> {
                frostAlpha = 0f
                successGlowAlpha = 0.4f - 0.4f * ((progress - 0.7f) / 0.3f)
                isShaking = false
                sunRayAlpha = 1f - ((progress - 0.7f) / 0.3f)
            }
        }

        val shakeOffset = if (isShaking) (sin(progress * 100f) * 2.dp.toPx()) else 0f

        // Draw Liquid
        drawRoundRect(
            color = Color(0xFF10B981),
            topLeft = Offset(tubeX + 2.dp.toPx() + shakeOffset, tubeY - 80.dp.toPx()),
            size = Size(tubeWidth - 4.dp.toPx(), 75.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        // Draw Success Glow Ring
        if (successGlowAlpha > 0f) {
            drawRoundRect(
                color = Color(0xFF34D399).copy(alpha = successGlowAlpha),
                topLeft = Offset(tubeX - 6.dp.toPx() + shakeOffset, tubeY - tubeHeight - 6.dp.toPx()),
                size = Size(tubeWidth + 12.dp.toPx(), tubeHeight + 12.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Draw Glass Tube
        val glassPath = Path().apply {
            moveTo(tubeX + shakeOffset, tubeY - tubeHeight)
            lineTo(tubeX + shakeOffset, tubeY - 8.dp.toPx())
            quadraticTo(tubeX + shakeOffset, tubeY, tubeX + 8.dp.toPx() + shakeOffset, tubeY)
            lineTo(tubeX + tubeWidth - 8.dp.toPx() + shakeOffset, tubeY)
            quadraticTo(tubeX + tubeWidth + shakeOffset, tubeY, tubeX + tubeWidth + shakeOffset, tubeY - 8.dp.toPx())
            lineTo(tubeX + tubeWidth + shakeOffset, tubeY - tubeHeight)
        }
        drawPath(glassPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2.5.dp.toPx()))

        // Draw Frost Overlay
        if (frostAlpha > 0f) {
            drawRoundRect(
                color = Color(0xAA93C5FD).copy(alpha = frostAlpha),
                topLeft = Offset(tubeX - 2.dp.toPx() + shakeOffset, tubeY - tubeHeight - 2.dp.toPx()),
                size = Size(tubeWidth + 4.dp.toPx(), tubeHeight + 4.dp.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx())
            )
            // Frost borders/stars
            drawRoundRect(
                color = Color.White.copy(alpha = frostAlpha),
                topLeft = Offset(tubeX - 2.dp.toPx() + shakeOffset, tubeY - tubeHeight - 2.dp.toPx()),
                size = Size(tubeWidth + 4.dp.toPx(), tubeHeight + 4.dp.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Draw Golden Sun Ray
        if (sunRayAlpha > 0f) {
            drawCircle(
                color = Color(0xFFFFB703).copy(alpha = sunRayAlpha * 0.4f),
                radius = 32.dp.toPx(),
                center = Offset(center + 50.dp.toPx(), centerY - 40.dp.toPx())
            )
            drawLine(
                color = Color(0xFFFFB703).copy(alpha = sunRayAlpha * 0.6f),
                start = Offset(center + 50.dp.toPx(), centerY - 40.dp.toPx()),
                end = Offset(center + shakeOffset, centerY),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
fun DesertSunsetAnimation() {
    val transition = rememberInfiniteTransition(label = "desert_sunset_volatile")
    
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f
        val tubeWidth = 40.dp.toPx()
        val tubeHeight = 110.dp.toPx()
        val tubeX = center - tubeWidth / 2f
        val tubeY = centerY + 35.dp.toPx()

        // Phase values:
        // 0.0 - 0.5: Volatile liquid is highly unstable, counts down 3.. 2.. 1.. (shaking & pulsing red)
        // 0.5 - 0.7: Liquid receives stabilizing layer, sparks, turns stable green
        // 0.7 - 1.0: Calm state
        
        val countdownText: String
        val liquidColor: Color
        val isShaking: Boolean
        val pulseScale: Float
        val stabilitySparkleAlpha: Float

        when {
            progress < 0.16f -> {
                countdownText = "3"
                liquidColor = Color(0xFFEF4444)
                isShaking = true
                pulseScale = 1.05f
                stabilitySparkleAlpha = 0f
            }
            progress < 0.33f -> {
                countdownText = "2"
                liquidColor = Color(0xFFF97316)
                isShaking = true
                pulseScale = 1.12f
                stabilitySparkleAlpha = 0f
            }
            progress < 0.5f -> {
                countdownText = "1"
                liquidColor = Color(0xFFEF4444)
                isShaking = true
                pulseScale = 1.25f
                stabilitySparkleAlpha = 0f
            }
            progress < 0.7f -> {
                val p = (progress - 0.5f) / 0.2f
                countdownText = "STABLE"
                liquidColor = Color(0xFF10B981)
                isShaking = false
                pulseScale = 1.0f
                stabilitySparkleAlpha = p
            }
            else -> {
                countdownText = "SAFE"
                liquidColor = Color(0xFF059669)
                isShaking = false
                pulseScale = 1.0f
                stabilitySparkleAlpha = 1f - ((progress - 0.7f) / 0.3f)
            }
        }

        val shakeOffset = if (isShaking) (sin(progress * 130f) * 3.dp.toPx()) else 0f

        // Draw Liquid
        drawRoundRect(
            color = liquidColor,
            topLeft = Offset(tubeX + 2.dp.toPx() + shakeOffset, tubeY - 80.dp.toPx()),
            size = Size(tubeWidth - 4.dp.toPx(), 75.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        // Draw Volatile Warning Halo
        if (isShaking) {
            drawRoundRect(
                color = Color(0xFFEF4444).copy(alpha = 0.2f * pulseScale),
                topLeft = Offset(tubeX - 8.dp.toPx() + shakeOffset, tubeY - tubeHeight - 8.dp.toPx()),
                size = Size(tubeWidth + 16.dp.toPx(), tubeHeight + 16.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Draw Glass Tube
        val glassPath = Path().apply {
            moveTo(tubeX + shakeOffset, tubeY - tubeHeight)
            lineTo(tubeX + shakeOffset, tubeY - 8.dp.toPx())
            quadraticTo(tubeX + shakeOffset, tubeY, tubeX + 8.dp.toPx() + shakeOffset, tubeY)
            lineTo(tubeX + tubeWidth - 8.dp.toPx() + shakeOffset, tubeY)
            quadraticTo(tubeX + tubeWidth + shakeOffset, tubeY, tubeX + tubeWidth + shakeOffset, tubeY - 8.dp.toPx())
            lineTo(tubeX + tubeWidth + shakeOffset, tubeY - tubeHeight)
        }
        drawPath(glassPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2.5.dp.toPx()))

        // Draw Stability Ring
        if (stabilitySparkleAlpha > 0f) {
            drawCircle(
                color = Color(0xFF34D399).copy(alpha = stabilitySparkleAlpha * 0.8f),
                radius = 28.dp.toPx(),
                center = Offset(center + shakeOffset, centerY - 15.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
    
    // Draw Text Centered Over Potion
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        val countText = when {
            progress < 0.16f -> "3"
            progress < 0.33f -> "2"
            progress < 0.5f -> "1"
            progress < 0.7f -> "OK"
            else -> "SECURE"
        }
        val textColor = if (progress < 0.5f) Color.White else Color(0xFF10B981)
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (progress < 0.5f) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = countText,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = Shadow(Color.Black, blurRadius = 4f)
                )
            )
        }
    }
}

@Composable
fun FrozenGlacierAnimation() {
    val transition = rememberInfiniteTransition(label = "frozen_mystery")
    
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f
        val tubeWidth = 40.dp.toPx()
        val tubeHeight = 110.dp.toPx()
        val tubeX = center - tubeWidth / 2f
        val tubeY = centerY + 35.dp.toPx()

        // Phase values:
        // 0.0 - 0.4: Top layer is blue, bottom 2 layers are covered in grey mist (represented by grey segments)
        // 0.4 - 0.7: Blue layer is cleared (pours out)
        // 0.7 - 1.0: Mist dissolves, revealing sparkling Gold layer underneath!
        
        val topBlueHeight: Float
        val mistAlpha: Float
        val revealedGoldAlpha: Float

        when {
            progress < 0.4f -> {
                topBlueHeight = 35.dp.toPx()
                mistAlpha = 0.9f
                revealedGoldAlpha = 0f
            }
            progress < 0.7f -> {
                val p = (progress - 0.4f) / 0.3f
                topBlueHeight = 35.dp.toPx() * (1f - p)
                mistAlpha = 0.9f
                revealedGoldAlpha = 0f
            }
            else -> {
                val p = (progress - 0.7f) / 0.3f
                topBlueHeight = 0f
                mistAlpha = 0.9f * (1f - p)
                revealedGoldAlpha = p
            }
        }

        // 1. Draw Gold Layer (revealed)
        if (revealedGoldAlpha > 0f) {
            drawRoundRect(
                color = Color(0xFFFFD700).copy(alpha = revealedGoldAlpha),
                topLeft = Offset(tubeX + 2.dp.toPx(), tubeY - 45.dp.toPx()),
                size = Size(tubeWidth - 4.dp.toPx(), 40.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx())
            )
        }

        // 2. Draw Grey Mist (if active)
        if (mistAlpha > 0f) {
            drawRoundRect(
                color = Color(0xFF4B5563).copy(alpha = mistAlpha),
                topLeft = Offset(tubeX + 2.dp.toPx(), tubeY - 45.dp.toPx()),
                size = Size(tubeWidth - 4.dp.toPx(), 40.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx())
            )
        }

        // 3. Draw Top Blue Layer
        if (topBlueHeight > 0f) {
            drawRoundRect(
                color = Color(0xFF3B82F6),
                topLeft = Offset(tubeX + 2.dp.toPx(), tubeY - 45.dp.toPx() - topBlueHeight),
                size = Size(tubeWidth - 4.dp.toPx(), topBlueHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // 4. Draw Glass Outline
        val glassPath = Path().apply {
            moveTo(tubeX, tubeY - tubeHeight)
            lineTo(tubeX, tubeY - 8.dp.toPx())
            quadraticTo(tubeX, tubeY, tubeX + 8.dp.toPx(), tubeY)
            lineTo(tubeX + tubeWidth - 8.dp.toPx(), tubeY)
            quadraticTo(tubeX + tubeWidth, tubeY, tubeX + tubeWidth, tubeY - 8.dp.toPx())
            lineTo(tubeX + tubeWidth, tubeY - tubeHeight)
        }
        drawPath(glassPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2.5.dp.toPx()))
    }

    // Draw question mark or mist sparkles
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val showQuestion = progress < 0.7f
        if (showQuestion) {
            Text(
                "?",
                color = Color.LightGray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = Shadow(Color.Black, blurRadius = 4f)
                )
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp).rotate(45f)
            )
        }
    }
}

@Composable
fun ChameleonAnimation() {
    val transition = rememberInfiniteTransition(label = "chameleon_anim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f
        val tubeW = 40.dp.toPx()
        val tubeH = 110.dp.toPx()
        val tubeX = center - tubeW / 2f
        val tubeY = centerY + 30.dp.toPx()

        // Rainbow shimmer color shift
        val hue1 = (phase * 360f) % 360f
        val c1 = Color.hsv(hue1, 0.8f, 0.95f)
        val c2 = Color.hsv((hue1 + 120f) % 360f, 0.8f, 0.95f)

        // Draw bottom Chameleon liquid
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(c1, c2)),
            topLeft = Offset(tubeX + 3.dp.toPx(), tubeY - 40.dp.toPx()),
            size = Size(tubeW - 6.dp.toPx(), 36.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        // Glass tube
        val glassPath = Path().apply {
            moveTo(tubeX, tubeY - tubeH)
            lineTo(tubeX, tubeY - 8.dp.toPx())
            quadraticTo(tubeX, tubeY, tubeX + 8.dp.toPx(), tubeY)
            lineTo(tubeX + tubeW - 8.dp.toPx(), tubeY)
            quadraticTo(tubeX + tubeW, tubeY, tubeX + tubeW, tubeY - 8.dp.toPx())
            lineTo(tubeX + tubeW, tubeY - tubeH)
        }
        drawPath(glassPath, Color.White.copy(alpha = 0.5f), style = Stroke(width = 3.dp.toPx()))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("✨ Wildcard Adapter", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PortalAnimation() {
    val transition = rememberInfiniteTransition(label = "portal_anim")
    val rot by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "rot"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f

        // Portal Orbs
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFF8B5CF6), Color.Transparent)),
            radius = 35.dp.toPx(),
            center = Offset(center - 50.dp.toPx(), centerY)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFEC4899), Color.Transparent)),
            radius = 35.dp.toPx(),
            center = Offset(center + 50.dp.toPx(), centerY)
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🌀 Alpha", color = Color(0xFFA855F7), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("⚡ Beta", color = Color(0xFFEC4899), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun CrystalLocksAnimation() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f

        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0x33FFD700), Color.Transparent)),
            radius = 45.dp.toPx(),
            center = Offset(center, centerY)
        )
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(36.dp))
            Text("Key Tube Unlocks Padlock", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ArchmageFusionAnimation() {
    val transition = rememberInfiniteTransition(label = "archmage_anim")
    val pulse by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = size.width / 2f
        val centerY = size.height / 2f

        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFEAB308).copy(alpha = pulse), Color.Transparent)),
            radius = 55.dp.toPx(),
            center = Offset(center, centerY)
        )
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌟 DIVINE FUSION CORE", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text("Mana Meter Full! Double Gems Activated!", color = Color.LightGray, fontSize = 11.sp)
        }
    }
}
