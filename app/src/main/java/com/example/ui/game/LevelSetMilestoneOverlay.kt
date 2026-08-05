package com.example.ui.game

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.audio.SoothingAudioEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class MilestoneParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var size: Float,
    var alpha: Float = 1f,
    var rotation: Float = 0f,
    var vr: Float = 0f,
    var isStar: Boolean = false
)

@Composable
fun LevelSetMilestoneOverlay(
    section: LevelSection,
    isVibrationEnabled: Boolean,
    onClaim: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Particles state
    val particles = remember { mutableStateListOf<MilestoneParticle>() }

    // Particle burst function
    fun spawnBurst(centerX: Float, centerY: Float, count: Int = 45) {
        val palette = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFF00F0FF), // Neon Cyan
            Color(0xFFFF007A), // Hot Pink
            Color(0xFF34D399), // Emerald
            Color(0xFFA855F7), // Purple
            Color(0xFFFFB703)  // Amber
        )
        for (i in 0 until count) {
            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val speed = Random.nextFloat() * 18f + 4f
            particles.add(
                MilestoneParticle(
                    x = centerX,
                    y = centerY,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed - Random.nextFloat() * 6f).toFloat(),
                    color = palette.random(),
                    size = Random.nextFloat() * 12f + 6f,
                    rotation = Random.nextFloat() * 360f,
                    vr = Random.nextFloat() * 10f - 5f,
                    isStar = Random.nextBoolean()
                )
            )
        }
    }

    // Trigger initial haptics and audio on display
    LaunchedEffect(Unit) {
        SoothingAudioEngine.playMilestoneFanfareSound()
        HapticEffects.triggerMilestoneBurst(context, haptic, isVibrationEnabled)
    }

    // Animation variables
    val cardScale = remember { Animatable(0.2f) }
    val cardAlpha = remember { Animatable(0f) }
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    val coinsCount = remember { Animatable(0f) }
    val gemsCount = remember { Animatable(0f) }

    // Infinite rotation for background rays
    val infiniteTransition = rememberInfiniteTransition(label = "sunburst")
    val sunburstRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunburst_rotation"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Sequence launcher
    LaunchedEffect(Unit) {
        // Step 1: Scale in main card with elastic spring
        launch {
            cardAlpha.animateTo(1f, tween(300))
        }
        cardScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.58f,
                stiffness = Spring.StiffnessLow
            )
        )

        // Step 2: Animate reward count
        launch {
            coinsCount.animateTo(section.rewardCoins.toFloat(), tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            gemsCount.animateTo(section.rewardGems.toFloat(), tween(1000, easing = FastOutSlowInEasing))
        }

        // Step 3: Stars slam down sequentially
        delay(200)
        star1Scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)

        delay(180)
        star2Scale.animateTo(1.25f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)

        delay(180)
        star3Scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
    }

    // Particle ticker loop
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                val iter = particles.iterator()
                while (iter.hasNext()) {
                    val p = iter.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.vy += 0.35f // Gravity
                    p.rotation += p.vr
                    p.alpha -= 0.012f
                    if (p.alpha <= 0f) {
                        iter.remove()
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Tap anywhere to burst extra fireworks!
                    spawnBurst(500f, 800f, 30)
                    HapticEffects.triggerStarDropPop(context, haptic, isVibrationEnabled)
                },
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas: Sunburst Rays & Particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val rayCount = 12
                val maxRadius = size.width.coerceAtLeast(size.height) * 1.2f

                // Draw Sunburst Rays
                for (i in 0 until rayCount) {
                    val angle1 = (i * 360f / rayCount + sunburstRotation) * (Math.PI / 180.0)
                    val angle2 = ((i + 0.5f) * 360f / rayCount + sunburstRotation) * (Math.PI / 180.0)

                    val p1 = Offset(
                        center.x + (cos(angle1) * maxRadius).toFloat(),
                        center.y + (sin(angle1) * maxRadius).toFloat()
                    )
                    val p2 = Offset(
                        center.x + (cos(angle2) * maxRadius).toFloat(),
                        center.y + (sin(angle2) * maxRadius).toFloat()
                    )

                    val rayPath = Path().apply {
                        moveTo(center.x, center.y)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        close()
                    }

                    drawPath(
                        path = rayPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = 0.12f),
                                Color(0xFF00F0FF).copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = maxRadius
                        )
                    )
                }

                // Draw Active Particles
                particles.forEach { p ->
                    if (p.isStar) {
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = p.size,
                            center = Offset(p.x, p.y)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            topLeft = Offset(p.x, p.y),
                            size = Size(p.size, p.size * 0.7f)
                        )
                    }
                }
            }

            // Foreground Milestone Card
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .scale(cardScale.value)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B),
                                Color(0xFF0F172A),
                                Color(0xFF020617)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFF00F0FF),
                                Color(0xFFFF007A)
                            )
                        ),
                        RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Header Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.18f))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LEVEL SET MASTERED",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grand Main Title
                Text(
                    text = "CHAPTER COMPLETED!",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = Shadow(
                            color = Color(0xFFFFD700).copy(alpha = 0.6f),
                            offset = Offset(0f, 4f),
                            blurRadius = 12f
                        )
                    )
                )

                Text(
                    text = section.name,
                    color = Color(0xFF38BDF8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = section.subtitle,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Stars Crest Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .scale(star1Scale.value)
                            .size(48.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.MilitaryTech,
                        contentDescription = null,
                        tint = Color(0xFF00F0FF),
                        modifier = Modifier
                            .scale(star2Scale.value)
                            .size(72.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .scale(star3Scale.value)
                            .size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Unlocked Reward Banner Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF38BDF8).copy(alpha = 0.4f), Color(0xFFA855F7).copy(alpha = 0.4f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "UNLOCKED BADGE & TITLE",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = section.rewardTitle,
                            color = Color(0xFFF43F5E),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Rewards breakdown counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${coinsCount.value.toInt()}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Diamond, null, tint = Color(0xFF00F0FF), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${gemsCount.value.toInt()}",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CLAIM & CONTINUE Premium Button
                Button(
                    onClick = {
                        scope.launch {
                            HapticEffects.triggerGrandFinaleRumble(context, haptic, isVibrationEnabled)
                            SoothingAudioEngine.playSelectSound()
                            onClaim()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF22C55E),
                                        Color(0xFF10B981),
                                        Color(0xFF059669)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CLAIM REWARDS & CONTINUE",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
