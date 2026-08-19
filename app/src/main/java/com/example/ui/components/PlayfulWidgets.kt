package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sound.SoundSynthesizer
import com.example.ui.theme.StarGold
import kotlin.random.Random

@Composable
fun KidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    elevationDp: Dp = 6.dp,
    testTag: String = "kid_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = if (isPressed) 0.94f else 1f
    val currentElevation = if (isPressed) 2.dp else elevationDp

    Surface(
        modifier = modifier
            .scale(scale)
            .testTag(testTag)
            .shadow(
                elevation = currentElevation,
                shape = RoundedCornerShape(20.dp),
                ambientColor = backgroundColor.copy(alpha = 0.4f),
                spotColor = backgroundColor.copy(alpha = 0.6f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                SoundSynthesizer.playClick()
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        color = if (enabled) backgroundColor else Color.Gray.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StarBadge(
    starsCount: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_bounce")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = onClick != null) {
                SoundSynthesizer.playStar()
                onClick?.invoke()
            },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "النجوم",
                tint = StarGold,
                modifier = Modifier
                    .size(24.dp)
                    .scale(starScale)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$starsCount",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ConfettiExplosion(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 45
) {
    if (!isActive) return

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
    }

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                startY = 0.35f + Random.nextFloat() * 0.1f,
                velocityX = (Random.nextFloat() - 0.5f) * 1.6f,
                velocityY = -1.2f - Random.nextFloat() * 1.5f,
                color = listOf(
                    Color(0xFFFF4757), Color(0xFF2ED573), Color(0xFF1E90FF),
                    Color(0xFFFFA502), Color(0xFF9B59B6), Color(0xFFFF69B4),
                    Color(0xFF00D2D3), Color(0xFFFFD32A)
                ).random(),
                size = 12f + Random.nextFloat() * 16f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = animProgress.value
        val gravity = 2.8f

        for (p in particles) {
            val curX = (p.startX + p.velocityX * t) * size.width
            val curY = (p.startY + p.velocityY * t + 0.5f * gravity * t * t) * size.height
            val alpha = (1f - t).coerceIn(0f, 1f)

            if (curY < size.height && alpha > 0f) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size * (1f - t * 0.3f),
                    center = Offset(curX, curY)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)
