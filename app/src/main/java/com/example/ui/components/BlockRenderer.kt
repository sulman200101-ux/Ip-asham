package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.BlockColor
import com.example.data.model.ShapeType

@Composable
fun BlockView(
    shapeType: ShapeType,
    blockColor: BlockColor,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 48.dp,
    rotation: Int = 0,
    isGhost: Boolean = false,
    ghostAlpha: Float = 0.35f,
    animationProgress: Float = 0f
) {
    Box(modifier = modifier.size(sizeDp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val primary = if (isGhost) blockColor.color.copy(alpha = ghostAlpha) else blockColor.color
            val highlight = if (isGhost) blockColor.highlightColor.copy(alpha = ghostAlpha) else blockColor.highlightColor
            val shadow = if (isGhost) blockColor.shadowColor.copy(alpha = ghostAlpha) else blockColor.shadowColor
            val strokeColor = if (isGhost) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.15f)

            when (shapeType) {
                ShapeType.CUBE -> drawCube(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.RECTANGLE -> drawRectangle(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.TALL_RECTANGLE -> drawTallRectangle(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.TRIANGLE -> drawTriangle(w, h, primary, highlight, shadow, strokeColor, rotation)
                ShapeType.WIDE_TRIANGLE -> drawWideTriangle(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.CYLINDER -> drawCylinder(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.ARCH -> drawArch(w, h, primary, highlight, shadow, strokeColor)
                ShapeType.WHEEL -> drawWheel(w, h, primary, highlight, shadow, strokeColor, animationProgress)
                ShapeType.EYES -> drawCuteEyes(w, h, primary, highlight, shadow, strokeColor, animationProgress)
                ShapeType.PROPELLER -> drawPropeller(w, h, primary, highlight, strokeColor, animationProgress)
                ShapeType.FLAG -> drawFlag(w, h, primary, highlight, shadow, strokeColor, animationProgress)
                ShapeType.STAR_TOP -> drawStarTop(w, h, primary, highlight, shadow, strokeColor)
            }
        }
    }
}

private fun DrawScope.drawCube(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val pad = w * 0.06f
    val r = w * 0.18f

    // Main face
    drawRoundRect(
        color = primary,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
    )

    // Top 3D highlight edge
    drawRoundRect(
        color = highlight,
        topLeft = Offset(pad + 2, pad + 2),
        size = Size(w - pad * 2 - 4, (h - pad * 2) * 0.35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.8f, r * 0.8f)
    )

    // Stud / Pin (Lego-style top pin)
    val studRadius = w * 0.16f
    drawCircle(
        color = highlight,
        radius = studRadius,
        center = Offset(w / 2, h / 2 - 2)
    )
    drawCircle(
        color = shadow,
        radius = studRadius,
        center = Offset(w / 2, h / 2 + 2),
        style = Stroke(width = 2f)
    )

    // Border
    drawRoundRect(
        color = stroke,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawRectangle(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val pad = 4f
    val r = 12f
    drawRoundRect(
        color = primary,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
    )
    drawRoundRect(
        color = highlight,
        topLeft = Offset(pad + 2, pad + 2),
        size = Size(w - pad * 2 - 4, (h - pad * 2) * 0.35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.7f, r * 0.7f)
    )
    // 2 Studs
    drawCircle(color = highlight, radius = w * 0.12f, center = Offset(w * 0.3f, h / 2))
    drawCircle(color = highlight, radius = w * 0.12f, center = Offset(w * 0.7f, h / 2))
    drawRoundRect(
        color = stroke,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawTallRectangle(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val pad = 4f
    val r = 12f
    drawRoundRect(
        color = primary,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
    )
    drawRoundRect(
        color = highlight,
        topLeft = Offset(pad + 2, pad + 2),
        size = Size((w - pad * 2) * 0.4f, h - pad * 2 - 4),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.7f, r * 0.7f)
    )
    drawRoundRect(
        color = stroke,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawTriangle(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color, rotation: Int) {
    val path = Path()
    when (rotation % 360) {
        90 -> {
            path.moveTo(w * 0.1f, h * 0.1f)
            path.lineTo(w * 0.9f, h * 0.5f)
            path.lineTo(w * 0.1f, h * 0.9f)
            path.close()
        }
        180 -> {
            path.moveTo(w * 0.1f, h * 0.1f)
            path.lineTo(w * 0.9f, h * 0.1f)
            path.lineTo(w * 0.5f, h * 0.9f)
            path.close()
        }
        270 -> {
            path.moveTo(w * 0.9f, h * 0.1f)
            path.lineTo(w * 0.1f, h * 0.5f)
            path.lineTo(w * 0.9f, h * 0.9f)
            path.close()
        }
        else -> {
            path.moveTo(w * 0.5f, h * 0.1f)
            path.lineTo(w * 0.9f, h * 0.9f)
            path.lineTo(w * 0.1f, h * 0.9f)
            path.close()
        }
    }
    drawPath(path = path, color = primary)
    drawPath(path = path, color = stroke, style = Stroke(width = 3f))
}

private fun DrawScope.drawWideTriangle(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.1f)
        lineTo(w * 0.95f, h * 0.9f)
        lineTo(w * 0.05f, h * 0.9f)
        close()
    }
    drawPath(path = path, color = primary)
    drawPath(path = path, color = stroke, style = Stroke(width = 3f))
}

private fun DrawScope.drawCylinder(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val pad = 4f
    drawRoundRect(
        color = primary,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2, w / 2)
    )
    drawCircle(
        color = highlight,
        radius = (w - pad * 2) * 0.35f,
        center = Offset(w / 2, h * 0.35f)
    )
    drawRoundRect(
        color = stroke,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2, w / 2),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawArch(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val path = Path().apply {
        moveTo(w * 0.05f, h * 0.95f)
        lineTo(w * 0.05f, h * 0.1f)
        lineTo(w * 0.95f, h * 0.1f)
        lineTo(w * 0.95f, h * 0.95f)
        lineTo(w * 0.7f, h * 0.95f)
        lineTo(w * 0.7f, h * 0.5f)
        arcTo(
            rect = Rect(w * 0.3f, h * 0.25f, w * 0.7f, h * 0.75f),
            startAngleDegrees = 0f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        lineTo(w * 0.3f, h * 0.95f)
        close()
    }
    drawPath(path, color = primary)
    drawPath(path, color = stroke, style = Stroke(width = 3f))
}

private fun DrawScope.drawWheel(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color, progress: Float) {
    val center = Offset(w / 2, h / 2)
    val outerRadius = w * 0.44f
    val innerRadius = w * 0.22f

    // Tire
    drawCircle(color = Color(0xFF2C3E50), radius = outerRadius, center = center)
    // Rim
    drawCircle(color = primary, radius = innerRadius * 1.5f, center = center)
    drawCircle(color = highlight, radius = innerRadius, center = center)
    // Hub cap
    drawCircle(color = Color(0xFFE74C3C), radius = innerRadius * 0.5f, center = center)

    // Spokes
    val angleOffset = progress * 360f
    for (i in 0 until 4) {
        val angleRad = Math.toRadians((i * 90.0 + angleOffset))
        val end = Offset(
            center.x + (outerRadius * 0.85f * Math.cos(angleRad)).toFloat(),
            center.y + (outerRadius * 0.85f * Math.sin(angleRad)).toFloat()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = center,
            end = end,
            strokeWidth = 3f
        )
    }
    drawCircle(color = stroke, radius = outerRadius, center = center, style = Stroke(width = 3f))
}

private fun DrawScope.drawCuteEyes(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color, progress: Float) {
    val pad = 4f
    drawRoundRect(
        color = primary,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )

    val eyeY = h * 0.5f
    val eyeRadius = w * 0.2f
    val leftEyeCenter = Offset(w * 0.32f, eyeY)
    val rightEyeCenter = Offset(w * 0.68f, eyeY)

    // White eye background
    drawCircle(color = Color.White, radius = eyeRadius, center = leftEyeCenter)
    drawCircle(color = Color.White, radius = eyeRadius, center = rightEyeCenter)

    // Pupils (Blinking logic or looking around)
    val pupilRadius = eyeRadius * 0.55f
    val pupilOffset = (Math.sin(progress.toDouble() * 2 * Math.PI) * (eyeRadius * 0.25f)).toFloat()

    drawCircle(color = Color(0xFF1E272E), radius = pupilRadius, center = Offset(leftEyeCenter.x + pupilOffset, leftEyeCenter.y))
    drawCircle(color = Color(0xFF1E272E), radius = pupilRadius, center = Offset(rightEyeCenter.x + pupilOffset, rightEyeCenter.y))

    // Pupil shine
    drawCircle(color = Color.White, radius = pupilRadius * 0.4f, center = Offset(leftEyeCenter.x + pupilOffset - 2, leftEyeCenter.y - 2))
    drawCircle(color = Color.White, radius = pupilRadius * 0.4f, center = Offset(rightEyeCenter.x + pupilOffset - 2, rightEyeCenter.y - 2))

    // Smile
    val smilePath = Path().apply {
        moveTo(w * 0.4f, h * 0.78f)
        quadraticTo(w * 0.5f, h * 0.9f, w * 0.6f, h * 0.78f)
    }
    drawPath(smilePath, color = Color(0xFF1E272E), style = Stroke(width = 3f))

    drawRoundRect(
        color = stroke,
        topLeft = Offset(pad, pad),
        size = Size(w - pad * 2, h - pad * 2),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawPropeller(w: Float, h: Float, primary: Color, highlight: Color, stroke: Color, progress: Float) {
    val center = Offset(w / 2, h / 2)
    val length = w * 0.45f
    val angle = progress * 360f

    val rad1 = Math.toRadians(angle.toDouble())
    val rad2 = Math.toRadians((angle + 180.0))

    val blade1 = Offset(center.x + (length * Math.cos(rad1)).toFloat(), center.y + (length * Math.sin(rad1)).toFloat())
    val blade2 = Offset(center.x + (length * Math.cos(rad2)).toFloat(), center.y + (length * Math.sin(rad2)).toFloat())

    drawLine(color = primary, start = center, end = blade1, strokeWidth = 14f)
    drawLine(color = highlight, start = center, end = blade2, strokeWidth = 14f)

    drawCircle(color = Color(0xFFE74C3C), radius = w * 0.16f, center = center)
    drawCircle(color = stroke, radius = w * 0.16f, center = center, style = Stroke(width = 2f))
}

private fun DrawScope.drawFlag(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color, progress: Float) {
    // Flag pole
    drawLine(
        color = Color(0xFF7F8C8D),
        start = Offset(w * 0.25f, h * 0.95f),
        end = Offset(w * 0.25f, h * 0.1f),
        strokeWidth = 5f
    )
    // Flag cloth
    val flagPath = Path().apply {
        moveTo(w * 0.25f, h * 0.15f)
        lineTo(w * 0.85f, h * 0.35f)
        lineTo(w * 0.25f, h * 0.55f)
        close()
    }
    drawPath(flagPath, color = primary)
    drawPath(flagPath, color = stroke, style = Stroke(width = 2f))
}

private fun DrawScope.drawStarTop(w: Float, h: Float, primary: Color, highlight: Color, shadow: Color, stroke: Color) {
    val center = Offset(w / 2, h / 2)
    val outerR = w * 0.42f
    val innerR = w * 0.2f
    val path = Path()

    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = Math.toRadians((i * 36.0 - 90.0))
        val x = (center.x + r * Math.cos(angle)).toFloat()
        val y = (center.y + r * Math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = Color(0xFFFFD32A))
    drawPath(path, color = stroke, style = Stroke(width = 2f))
}
