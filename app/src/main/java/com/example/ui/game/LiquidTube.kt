package com.example.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidTube(
    colors: List<Int>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xAA6C63FF),
    maxCapacity: Int = 4,
    skinType: String = "default",
    tiltAngle: Float = 0f,
    pourFraction: Float = 0f,
    isSource: Boolean = false,
    isDest: Boolean = false,
    pouredColorId: Int? = null,
    pourAmount: Int = 0,
    translationOffset: Offset = Offset.Zero,
    levelNumber: Int = 1,
    isVolatile: Boolean = false,
    volatileMovesLeft: Int = 5,
    isFrozen: Boolean = false,
    portalPairId: Int? = null,
    isCrystalLocked: Boolean = false
) {
    // Selection Elevation Animation (hops up by 16dp)
    val elevationOffsetState = animateDpAsState(
        targetValue = if (isSelected && !isSource) (-16).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "tube_elevation"
    )

    // Glowing Halo Pulsing Animation
    val glowAlphaState = animateFloatAsState(
        targetValue = if (isSelected) 0.8f else 0.0f,
        animationSpec = spring(),
        label = "tube_glow"
    )

    // Infinite transitions for dynamic water ripple and rising bubbles - optimized States to avoid 60fps recompositions
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_infinite")
    val wavePhaseState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquid_wave"
    )

    val bubbleOffsetPhaseState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_offset"
    )

    val glassPath = remember { Path() }
    val blockPath = remember { Path() }
    val bubbleRand = remember { java.util.Random() }
    
    val blockColors = remember { IntArray(8) }
    val blockHeights = remember { FloatArray(8) }

    Box(
        modifier = modifier
            .width(60.dp)
            .height(210.dp)
            .graphicsLayer {
                // Apply selection elevation offset and translation offset
                translationX = translationOffset.x
                translationY = translationOffset.y + elevationOffsetState.value.toPx()
                // Apply rotation and transform origin (centered horizontally, near the top lip)
                rotationZ = tiltAngle
                transformOrigin = TransformOrigin(0.5f, 0.02f)
            }
            .drawBehind {
                val glowAlpha = glowAlphaState.value
                if (glowAlpha > 0f) {
                    // Draw glowing shadow halo behind the selected tube
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.35f * glowAlpha), Color.Transparent),
                            radius = size.height * 0.4f,
                            center = Offset(size.width / 2, size.height * 0.4f)
                        ),
                        cornerRadius = CornerRadius(size.width / 2)
                    )
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No standard ripple, handled by motion elevation/glow
                onClick = onClick
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radius = w / 2

            // --- 1. DEFINE GLASS INNER CONTAINER PATH (based on skinType) ---
            glassPath.reset()
            glassPath.apply {
                when (skinType) {
                    "beaker" -> {
                        val neckWidth = w * 0.58f
                        val neckLeft = (w - neckWidth) / 2
                        val neckRight = neckLeft + neckWidth
                        val flareY = h * 0.22f
                        moveTo(neckLeft, 0f)
                        lineTo(neckLeft, flareY)
                        lineTo(0f, h - radius)
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(0f, h - radius * 2, w, h),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = -180f,
                            forceMoveTo = false
                        )
                        lineTo(w, h - radius)
                        lineTo(neckRight, flareY)
                        lineTo(neckRight, 0f)
                    }
                    "crystal" -> {
                        moveTo(w * 0.15f, 0f)
                        lineTo(0f, h * 0.8f)
                        lineTo(w / 2, h)
                        lineTo(w, h * 0.8f)
                        lineTo(w * 0.85f, 0f)
                    }
                    "futuristic" -> {
                        // Flat bottom with small corner bevels for high-tech tank style
                        moveTo(w * 0.05f, 0f)
                        lineTo(w * 0.05f, h - 14.dp.toPx())
                        lineTo(14.dp.toPx(), h)
                        lineTo(w - 14.dp.toPx(), h)
                        lineTo(w - w * 0.05f, h - 14.dp.toPx())
                        lineTo(w - w * 0.05f, 0f)
                    }
                    else -> { // "default" classic cylinder
                        moveTo(0f, 0f)
                        lineTo(0f, h - radius)
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(0f, h - radius * 2, w, h),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = -180f,
                            forceMoveTo = false
                        )
                        lineTo(w, 0f)
                    }
                }
            }

            // --- 2. DRAW LIQUID LAYERS (Bottom-to-Top, Tilted horizontally) ---
            val blockHeight = (h - 22.dp.toPx()) / maxCapacity
            val liquidBottomMargin = 8.dp.toPx()

            var numBlocks = 0
            if (isSource && colors.isNotEmpty() && pourAmount > 0) {
                // Source Tube Draining Animation
                val topColor = colors.last()
                var remainingShrink = pourAmount * pourFraction * blockHeight
                
                colors.forEachIndexed { index, colorId ->
                    if (colorId == topColor && index >= colors.size - pourAmount) {
                        val thisBlockShrink = minOf(blockHeight, remainingShrink)
                        remainingShrink -= thisBlockShrink
                        val drawnHeight = blockHeight - thisBlockShrink
                        if (drawnHeight > 0.5f) {
                            if (numBlocks < 8) {
                                blockColors[numBlocks] = colorId
                                blockHeights[numBlocks] = drawnHeight
                                numBlocks++
                            }
                        }
                    } else {
                        if (numBlocks < 8) {
                            blockColors[numBlocks] = colorId
                            blockHeights[numBlocks] = blockHeight
                            numBlocks++
                        }
                    }
                }
            } else if (isDest && pourAmount > 0 && pouredColorId != null) {
                // Destination Tube Filling Animation
                colors.forEach { colorId ->
                    if (numBlocks < 8) {
                        blockColors[numBlocks] = colorId
                        blockHeights[numBlocks] = blockHeight
                        numBlocks++
                    }
                }
                val addedHeight = pourAmount * pourFraction * blockHeight
                if (addedHeight > 0.5f) {
                    if (numBlocks < 8) {
                        blockColors[numBlocks] = pouredColorId
                        blockHeights[numBlocks] = addedHeight
                        numBlocks++
                    }
                }
            } else {
                // Static default rendering
                colors.forEach { colorId ->
                    if (numBlocks < 8) {
                        blockColors[numBlocks] = colorId
                        blockHeights[numBlocks] = blockHeight
                        numBlocks++
                    }
                }
            }

            // Draw liquid clipped strictly to the glass boundary
            clipPath(glassPath) {
                // To keep the water level horizontal as the tube tilts, we reverse-rotate around the tube pivot
                val pivotY = 4.dp.toPx()
                withTransform({
                    rotate(-tiltAngle, pivot = Offset(w / 2, pivotY))
                }) {
                    var currentY = h - liquidBottomMargin
                    
                    for (index in 0 until numBlocks) {
                        val blockColorId = blockColors[index]
                        val blockHeightVal = blockHeights[index]
                        val isChameleon = blockColorId == 99
                        
                        val liquidColor = if (isChameleon) {
                            LiquidColor(99, "Chameleon Rainbow", Color(0xFF00F0FF), Color(0xFFFF007A), Color(0x4400F0FF))
                        } else {
                            LiquidColors.find { it.id == blockColorId } ?: continue
                        }
                        val startY = currentY - blockHeightVal
                        
                        // Extend slightly downwards for subsequent blocks to prevent rendering seams
                        val drawHeight = if (index == 0) blockHeightVal + 15.dp.toPx() else blockHeightVal + 1.2f
                        currentY = startY // move upward for the next block

                        blockPath.reset()
                        blockPath.apply {
                            if (index == 0) {
                                // Bottom block - flat rect is fine since clipPath shapes it perfectly
                                moveTo(-w * 0.5f, startY)
                                lineTo(w * 1.5f, startY)
                                lineTo(w * 1.5f, startY + drawHeight)
                                lineTo(-w * 0.5f, startY + drawHeight)
                                close()
                            } else {
                                // Upper blocks have dynamic sine waves for flowing water texture
                                moveTo(-w * 0.5f, startY)
                                
                                val waveHeight = when {
                                    isDest -> 5.dp.toPx()
                                    isSource -> 3.dp.toPx()
                                    else -> 1.5.dp.toPx() // Calm idle ripple
                                }
                                val waveSpeed = if (isDest || isSource) 2.2f else 1.0f
                                
                                val waveOffset1 = sin(wavePhaseState.value * waveSpeed) * waveHeight
                                val waveOffset2 = cos(wavePhaseState.value * waveSpeed) * waveHeight
                                
                                cubicTo(
                                    w * 0.25f, startY + waveOffset1,
                                    w * 0.75f, startY + waveOffset2,
                                    w * 1.5f, startY
                                )
                                lineTo(w * 1.5f, startY + drawHeight)
                                lineTo(-w * 0.5f, startY + drawHeight)
                                close()
                            }
                        }

                        val startColor = if (isChameleon) Color(0xFF00F0FF) else liquidColor.startColor
                        val endColor = if (isChameleon) Color(0xFFFF007A) else liquidColor.endColor

                        // Render gradient-filled fluid block
                        drawPath(
                            path = blockPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(startColor, endColor),
                                startY = startY,
                                endY = startY + blockHeightVal
                            )
                        )

                        if (isChameleon) {
                            // Sparkling rainbow particles for Chameleon adapter
                            val randSeed = (index * 13 + 7).toLong()
                            val mistRand = java.util.Random(randSeed)
                            repeat(3) {
                                val mx = mistRand.nextFloat() * (w - 12.dp.toPx()) + 6.dp.toPx()
                                val my = startY + mistRand.nextFloat() * blockHeightVal
                                drawCircle(
                                    color = Color(0xFFFFD700).copy(alpha = 0.6f),
                                    radius = 2.dp.toPx(),
                                    center = Offset(mx, my)
                                )
                            }
                        } else {
                            // Floating dynamic micro-bubbles (only for revealed segments)
                            val randSeed = (blockColorId * 31 + index * 17).toLong()
                            bubbleRand.setSeed(randSeed)
                            
                            repeat(4) { bIndex ->
                                val initialBx = bubbleRand.nextFloat() * (w - 12.dp.toPx()) + 6.dp.toPx()
                                val initialBy = startY + bubbleRand.nextFloat() * blockHeightVal
                                
                                // Float upward linearly
                                val bubbleSpeed = 0.4f + bubbleRand.nextFloat() * 0.6f
                                val animatedBy = startY + ((initialBy - startY - (bubbleOffsetPhaseState.value * blockHeightVal * bubbleSpeed)) % blockHeightVal)
                                
                                // Fade out gracefully near the surface
                                val distanceToSurface = animatedBy - startY
                                val alpha = (distanceToSurface / blockHeightVal).coerceIn(0f, 1f) * 0.55f
                                
                                val sizePx = bubbleRand.nextFloat() * 2.5.dp.toPx() + 0.6.dp.toPx()
                                val sway = sin(bubbleOffsetPhaseState.value * 2 * Math.PI.toFloat() + bIndex) * 2.2.dp.toPx()

                                if (animatedBy in startY..(startY + blockHeightVal)) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = alpha),
                                        radius = sizePx,
                                        center = Offset(initialBx + sway, animatedBy)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. DRAW PREMIUM CONTAINER ACCENTS AND MARKS ---
            if (skinType == "beaker") {
                // Alchemical calibration markings
                repeat(3) { mark ->
                    val markY = h - liquidBottomMargin - (mark + 1) * blockHeight
                    drawLine(
                        color = Color.White.copy(alpha = 0.22f),
                        start = Offset(w * 0.18f, markY),
                        end = Offset(w * 0.28f, markY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            } else if (skinType == "crystal") {
                // Diamond/faceted glass edge lines
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(w / 2, 8.dp.toPx()),
                    end = Offset(w / 2, h - 2.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(w * 0.25f, 8.dp.toPx()),
                    end = Offset(w * 0.25f, h * 0.8f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(w * 0.75f, 8.dp.toPx()),
                    end = Offset(w * 0.75f, h * 0.8f),
                    strokeWidth = 1.dp.toPx()
                )
            } else if (skinType == "futuristic") {
                // Metal caps top/bottom
                drawRect(
                    color = Color(0xFF2C313D),
                    topLeft = Offset(w * 0.05f, 0f),
                    size = Size(w * 0.9f, 6.dp.toPx())
                )
                drawRect(
                    color = Color(0xFF2C313D),
                    topLeft = Offset(w * 0.05f, h - 8.dp.toPx()),
                    size = Size(w * 0.9f, 8.dp.toPx())
                )
                // Small glowing green cyber-indicator bar
                drawRect(
                    color = Color(0xFF00C2A8).copy(alpha = 0.6f),
                    topLeft = Offset(w * 0.35f, h - 6.dp.toPx()),
                    size = Size(w * 0.3f, 4.dp.toPx())
                )
            }

            // --- 4. DRAW GLASS CONTOUR BOUNDS (Translucent silver outlines) ---
            // Outer translucent container tint
            drawPath(
                path = glassPath,
                color = Color.White.copy(alpha = 0.04f)
            )

            // Glass boundary outline with volatile/frozen support
            val borderStrokeWidth = if (isVolatile) 3.5.dp.toPx() else 2.dp.toPx()
            val borderColor = if (isVolatile) {
                val pulse = (sin(wavePhaseState.value * 3f) + 1f) / 2f
                Color(0xFFEF4444).copy(alpha = 0.5f + pulse * 0.5f)
            } else {
                Color.White.copy(alpha = 0.22f)
            }

            drawPath(
                path = glassPath,
                color = borderColor,
                style = Stroke(width = borderStrokeWidth)
            )

            // Frost glass overlays if frozen
            if (isFrozen) {
                drawPath(
                    path = glassPath,
                    color = Color(0x66B3F0FF) // Frozen cyan translucent tint
                )
                drawPath(
                    path = glassPath,
                    color = Color(0xFF38BDF8),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Volatile countdown label
            if (isVolatile) {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(230, 239, 68, 68)
                    textSize = 12.dp.toPx()
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "💣 $volatileMovesLeft",
                    w / 2,
                    24.dp.toPx(),
                    paint
                )
            }

            // Portal Ring Indicator
            if (portalPairId != null) {
                val portalColor = if (portalPairId == 1) Color(0xFFA855F7) else Color(0xFFEC4899)
                drawCircle(
                    brush = Brush.radialGradient(listOf(portalColor, Color.Transparent)),
                    radius = 12.dp.toPx(),
                    center = Offset(w / 2, 12.dp.toPx())
                )
            }

            // Crystal Padlock Overlay
            if (isCrystalLocked) {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.65f),
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(w / 2)
                )
                val lockPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(255, 255, 215, 0)
                    textSize = 22.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText("🔒", w / 2, h / 2 + 8.dp.toPx(), lockPaint)
            }

            // Dynamic rim lip drawing
            when (skinType) {
                "beaker" -> {
                    val neckWidth = w * 0.58f
                    val neckLeft = (w - neckWidth) / 2
                    drawOval(
                        color = Color.White.copy(alpha = 0.35f),
                        topLeft = Offset(neckLeft, 0f),
                        size = Size(neckWidth, 6.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                "crystal" -> {
                    drawOval(
                        color = Color.White.copy(alpha = 0.35f),
                        topLeft = Offset(w * 0.15f, 0f),
                        size = Size(w * 0.7f, 6.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                "futuristic" -> {
                    drawOval(
                        color = Color.White.copy(alpha = 0.35f),
                        topLeft = Offset(w * 0.05f, 0f),
                        size = Size(w * 0.9f, 6.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                else -> {
                    drawOval(
                        color = Color.White.copy(alpha = 0.35f),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, 7.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // --- 5. REFLECTIONS & GLOSS HIGHLIGHTS ---
            // Soft vertical reflection shine
            if (skinType != "crystal") {
                drawLine(
                    color = Color.White.copy(alpha = 0.13f),
                    start = Offset(w * 0.15f, 10.dp.toPx()),
                    end = Offset(w * 0.15f, h - radius),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(w * 0.85f, 10.dp.toPx()),
                    end = Offset(w * 0.85f, h - radius),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }
    }
}
