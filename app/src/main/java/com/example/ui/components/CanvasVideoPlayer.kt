package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimationStyle
import com.example.data.model.StoryboardScene
import com.example.data.model.VideoAspectRatio
import com.example.ui.theme.AmberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetCoral
import com.example.ui.theme.SunsetPink
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CanvasVideoPlayer(
    currentScene: StoryboardScene?,
    style: AnimationStyle,
    aspectRatio: VideoAspectRatio,
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "videoAnim")
    val timeStep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timeStep"
    )

    val ratio = when (aspectRatio) {
        VideoAspectRatio.PORTRAIT_9_16 -> 9f / 16f
        VideoAspectRatio.LANDSCAPE_16_9 -> 16f / 9f
        VideoAspectRatio.SQUARE_1_1 -> 1f
    }

    Box(
        modifier = modifier
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF090D16)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Dynamic Background based on style
            drawBackgroundGradient(style, timeStep, w, h)

            // 2. Animated Particle Grid / Cosmic Stars
            drawAnimatedParticles(style, timeStep, w, h)

            // 3. Central Animated Character Avatar / Visualizer
            val charY = h * 0.42f + sin(timeStep) * 12.dp.toPx()
            val charX = w * 0.5f

            // Character Glowing Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(style.primaryColor).copy(alpha = 0.55f),
                        Color(style.secondaryColor).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(charX, charY),
                    radius = w * 0.35f
                ),
                radius = w * 0.35f,
                center = Offset(charX, charY)
            )

            // Dynamic Audio Rings Pulsing
            val ringRadius = (w * 0.22f) * (1.0f + 0.1f * sin(timeStep * 2))
            drawCircle(
                color = Color(style.secondaryColor).copy(alpha = 0.6f),
                radius = ringRadius,
                center = Offset(charX, charY),
                style = Stroke(width = 3.dp.toPx())
            )

            // Cyber Motion Wave Lines
            drawMotionSoundwaves(timeStep, w, h, Color(style.primaryColor))
        }

        // Animated Character & Emoji Overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentScene?.characterEmoji ?: "✨",
                fontSize = 58.sp
            )
        }

        // Subtitle / Kinetic Caption Banner
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.88f))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = currentScene?.textCaption ?: "مشهد متحرك ذكي من صانع الفيديو بالذكاء الاصطناعي",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun DrawScope.drawBackgroundGradient(
    style: AnimationStyle,
    time: Float,
    w: Float,
    h: Float
) {
    val c1 = Color(style.primaryColor)
    val c2 = Color(style.secondaryColor)
    val dark = Color(0xFF090D16)

    val shiftX = sin(time) * (w * 0.25f)
    val shiftY = cos(time) * (h * 0.25f)

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(c1.copy(alpha = 0.35f), c2.copy(alpha = 0.2f), dark),
            center = Offset(w * 0.5f + shiftX, h * 0.4f + shiftY),
            radius = w.coerceAtLeast(h) * 0.8f
        )
    )
}

private fun DrawScope.drawAnimatedParticles(
    style: AnimationStyle,
    time: Float,
    w: Float,
    h: Float
) {
    val particleCount = 24
    for (i in 0 until particleCount) {
        val angle = (i.toFloat() / particleCount) * 2 * PI.toFloat() + time * 0.5f
        val distance = (w * 0.38f) * (0.3f + (i % 5) * 0.16f)
        val px = w * 0.5f + cos(angle) * distance
        val py = h * 0.42f + sin(angle) * distance * 0.8f

        val particleColor = if (i % 2 == 0) Color(style.primaryColor) else Color(style.secondaryColor)
        val radius = (3.dp.toPx() + (i % 3) * 1.5.dp.toPx()) * (0.8f + 0.3f * sin(time + i))

        drawCircle(
            color = particleColor.copy(alpha = 0.75f),
            radius = radius,
            center = Offset(px, py)
        )
    }
}

private fun DrawScope.drawMotionSoundwaves(
    time: Float,
    w: Float,
    h: Float,
    color: Color
) {
    val path = Path()
    val waveY = h * 0.72f
    val points = 30
    val step = w / points

    path.moveTo(0f, waveY)
    for (i in 0..points) {
        val x = i * step
        val waveOffset = sin(i * 0.35f + time * 2.5f) * 16.dp.toPx()
        path.lineTo(x, waveY + waveOffset)
    }

    drawPath(
        path = path,
        color = color.copy(alpha = 0.5f),
        style = Stroke(width = 2.5.dp.toPx())
    )
}
