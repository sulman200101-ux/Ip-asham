package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailableModels
import com.example.data.model.ModelCategory
import com.example.data.model.ModelInfo
import com.example.data.model.PromptPresets
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectorSheet(
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "اختر نموذج OpenAI",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AvailableModels.CHAT_MODELS) { model ->
                    val isSelected = model.id == selectedModel
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onModelSelected(model.id)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) OpenAIGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, OpenAIGreen) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = model.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = if (isSelected) OpenAIGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = model.category.displayName.take(16),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                            )
                                        },
                                        modifier = Modifier.height(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = model.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "السياق: ${model.contextWindow}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = ObsidianSubtext
                                    )
                                    if (model.supportsReasoning) {
                                        Text(
                                            text = "★ استنتاج وتفكير",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AmberAccent)
                                        )
                                    }
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "تم الاختيار",
                                    tint = OpenAIGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametersSheet(
    temperature: Double,
    topP: Double,
    systemPrompt: String,
    onTemperatureChange: (Double) -> Unit,
    onTopPChange: (Double) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var promptState by remember { mutableStateOf(systemPrompt) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "معايير وإعدادات الاستجابة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Temperature Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("درجة الإبداع (Temperature): ${String.format("%.2f", temperature)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(
                    text = if (temperature < 0.3) "محدد ودقيق" else if (temperature > 1.2) "إبداعي ومتنوع" else "متوازن",
                    style = MaterialTheme.typography.labelSmall,
                    color = OpenAIGreen
                )
            }
            Slider(
                value = temperature.toFloat(),
                onValueChange = { onTemperatureChange(it.toDouble()) },
                valueRange = 0.0f..2.0f,
                steps = 19,
                colors = SliderDefaults.colors(thumbColor = OpenAIGreen, activeTrackColor = OpenAIGreen)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Top P Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("عينات النواة (Top P): ${String.format("%.2f", topP)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            Slider(
                value = topP.toFloat(),
                onValueChange = { onTopPChange(it.toDouble()) },
                valueRange = 0.0f..1.0f,
                steps = 10,
                colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("توجيهات النظام والشخصية (System Prompt)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(6.dp))

            // Quick Preset Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (preset in PromptPresets.PRESETS.take(3)) {
                    FilterChip(
                        selected = promptState == preset.prompt,
                        onClick = {
                            promptState = preset.prompt
                            onSystemPromptChange(preset.prompt)
                        },
                        label = { Text(preset.title, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promptState,
                onValueChange = {
                    promptState = it
                    onSystemPromptChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = { Text("أدخل تعليمات وتوجيهات النظام المخصصة للنموذج...") },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OpenAIGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun AudioWaveVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    waveColor: Color = OpenAIGreen
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.height(36.dp).fillMaxWidth()) {
        val barCount = 28
        val spacing = size.width / (barCount * 1.5f)
        val barWidth = spacing * 0.7f

        for (i in 0 until barCount) {
            val progress = if (isPlaying) {
                val sinVal = kotlin.math.sin(Math.toRadians((phase + i * 25).toDouble())).toFloat()
                (kotlin.math.abs(sinVal) * 0.7f) + 0.3f
            } else {
                0.2f
            }
            val barHeight = size.height * progress
            val x = i * (barWidth + spacing) + spacing
            val y = (size.height - barHeight) / 2

            drawRoundRect(
                color = waveColor.copy(alpha = if (isPlaying) 0.85f else 0.4f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
