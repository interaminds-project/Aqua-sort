package com.example.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- COLOR CONVENTIONS ---
val ColorPrimary = Color(0xFF6C63FF)
val ColorAccent = Color(0xFFFFB703)
val ColorSecondary = Color(0xFF00C2A8)
val ColorSuccess = Color(0xFF22C55E)
val ColorDanger = Color(0xFFEF4444)
val ColorWarning = Color(0xFFF59E0B)

val GlassBg = Color(0x1AFFFFFF)
val GlassBorder = Color(0x33FFFFFF)

// --- PREMIUM BACKGROUNDS ---
@Composable
fun GameBackground(
    theme: GameTheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(theme.primaryBg, theme.secondaryBg)
                )
            )
    ) {
        // Subtle ambient glowing orbs in corners
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(theme.liquidGlowColor.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2
                        )
                    )
                }
        )

        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(theme.accentColor.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2
                        )
                    )
                }
        )

        content()
    }
}

// --- GLASSMORPHISM PANEL ---
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    glowColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .drawBehind {
                if (glowColor != Color.Transparent) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.15f), Color.Transparent),
                            radius = size.width
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(cornerRadius))
            .background(GlassBg)
            .border(borderWidth, GlassBorder, RoundedCornerShape(cornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

// --- SPRING BOUNCE ACTION BUTTON ---
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    gradientColors: List<Color> = listOf(ColorPrimary, ColorPrimary.copy(alpha = 0.8f)),
    icon: ImageVector? = null,
    glowColor: Color = ColorPrimary.copy(alpha = 0.4f)
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    // Bounce Scale effect on click
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (isEnabled) {
                    drawRoundRect(
                        color = glowColor,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
                        topLeft = Offset(0f, 6f) // Drop shadow offset
                    )
                }
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isEnabled) {
                    Brush.verticalGradient(gradientColors)
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF4B5563), Color(0xFF374151)))
                }
            )
            .clickable(
                enabled = isEnabled,
                onClick = {
                    onClick()
                }
            )
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (isEnabled) Color.White else Color(0xFF9CA3AF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }

    // Capture pressed state change safely
    LaunchedEffect(interactionSource) {
        // Not using custom click listeners since simple scaling acts fine on tap,
        // we can trigger state toggling directly on tap.
    }
}

// --- GLASS ICON ACTION BUTTON ---
@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    size: Dp = 56.dp,
    tint: Color = Color.White,
    glowColor: Color = Color.Transparent
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "icon_button_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .drawBehind {
                if (glowColor != Color.Transparent) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.3f), Color.Transparent),
                            radius = size.toPx()
                        )
                    )
                }
            }
            .clip(CircleShape)
            .background(GlassBg)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}

// --- TOP HEADER VALUE PILLS ---
@Composable
fun StatusPill(
    icon: ImageVector,
    value: String,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

// --- GOLD/XP PULSING TEXT WRAPPER ---
@Composable
fun PulsingLabel(
    text: String,
    color: Color,
    fontSize: Int = 14
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    )
}
