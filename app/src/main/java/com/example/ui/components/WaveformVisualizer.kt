package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetPink

@Composable
fun AudioWaveformVisualizer(
    amplitudes: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    barColorStart: Color = NeonViolet,
    barColorEnd: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idlePulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height - 24.dp)) {
            val totalBars = amplitudes.size.coerceAtLeast(16)
            val barSpacing = 6.dp.toPx()
            val totalSpacing = barSpacing * (totalBars - 1)
            val availableWidth = size.width - totalSpacing
            val barWidth = (availableWidth / totalBars).coerceAtLeast(4.dp.toPx())
            val maxBarHeight = size.height

            val gradientBrush = Brush.verticalGradient(
                colors = listOf(SunsetPink, barColorStart, barColorEnd)
            )

            for (i in 0 until totalBars) {
                val amp = if (isPlaying) {
                    amplitudes.getOrElse(i) { 0.15f }.coerceIn(0.08f, 1.0f)
                } else {
                    idlePulse * (0.8f + (i % 3) * 0.15f)
                }

                val barH = (maxBarHeight * amp).coerceAtLeast(6.dp.toPx())
                val x = i * (barWidth + barSpacing)
                val y = (maxBarHeight - barH) / 2

                drawRoundRect(
                    brush = gradientBrush,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun PulsingMicWave(
    isRecordingOrPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isRecordingOrPlaying) 1.35f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension / 2) * 0.7f

        if (isRecordingOrPlaying) {
            drawCircle(
                color = SunsetPink.copy(alpha = 0.25f),
                radius = radius * glowScale,
                center = center
            )
            drawCircle(
                color = NeonViolet.copy(alpha = 0.4f),
                radius = radius * (glowScale * 0.85f),
                center = center
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonViolet, Color(0xFF4C1D95)),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}
