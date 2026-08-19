package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
import com.example.data.model.VoiceAvatar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun StudioHeader(
    title: String,
    subtitle: String? = null,
    credits: Int = 50,
    onBackClick: (() -> Unit)? = null,
    onRewardClick: (() -> Unit)? = null,
    onAiConfigClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isConnected = GeminiApiClient.getApiKey().isNotBlank() && GeminiApiClient.getApiKey() != "MY_GEMINI_API_KEY"
    val modelName = when (GeminiApiClient.getSelectedModel()) {
        GeminiApiClient.MODEL_PRO -> "Gemini Pro"
        else -> "Gemini Flash"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // AI Hub Connection Badge
            if (onAiConfigClick != null) {
                AiConnectionBadge(
                    isConnected = isConnected,
                    modelName = modelName,
                    onClick = onAiConfigClick
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Credits / Rewarded Ad Button
            if (onRewardClick != null) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onRewardClick)
                        .testTag("credits_badge"),
                    color = Color(0xFF1E1B4B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "نقاط الإبداع",
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$credits",
                            color = AmberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    iconEmoji: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkCard)
            .border(1.dp, gradientColors.first().copy(alpha = 0.35f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(gradientColors.first().copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = gradientColors.first(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VoiceAvatarCard(
    avatar: VoiceAvatar,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(avatar.accentColor) else DarkCardBorder
    val bgColor = if (isSelected) Color(avatar.accentColor).copy(alpha = 0.15f) else DarkCard

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(avatar.accentColor).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = avatar.iconEmoji, fontSize = 24.sp)
                if (avatar.isPremium) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(AmberGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "VIP",
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = avatar.nameAr.substringBefore("-").trim(),
                style = MaterialTheme.typography.labelLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = avatar.nameAr.substringAfter("-").trim(),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AiConnectionBadge(
    isConnected: Boolean,
    modelName: String = "Gemini 3.5 Flash",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("ai_connection_badge"),
        color = if (isConnected) Color(0xFF064E3B) else Color(0xFF451A03),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isConnected) Color(0xFF10B981) else Color(0xFFF59E0B)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) Color(0xFF34D399) else Color(0xFFFBBF24))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) "AI: $modelName" else "إعدادات AI ⚙️",
                color = if (isConnected) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiConnectionHubDialog(
    onDismiss: () -> Unit,
    onSave: (apiKey: String, model: String, temperature: Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf(GeminiApiClient.getApiKey()) }
    var showPassword by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf(GeminiApiClient.getSelectedModel()) }
    var temperature by remember { mutableFloatStateOf(GeminiApiClient.getTemperature()) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTestSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مركز مولدات الذكاء الاصطناعي",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "تحكم في خوادم ومحركات Gemini الذكية لتوليد الصوت، الأغاني، والرسوم المتحركة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Model Selection
                Text(
                    text = "اختر نموذج الذكاء الاصطناعي (Model):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isFlash = selectedModel == GeminiApiClient.MODEL_FLASH
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFlash) NeonViolet.copy(alpha = 0.3f) else DarkCard)
                            .border(1.dp, if (isFlash) NeonViolet else DarkCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedModel = GeminiApiClient.MODEL_FLASH }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gemini 3.5 Flash", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("سريع وإبداعي", color = NeonCyan, fontSize = 10.sp)
                        }
                    }

                    val isPro = selectedModel == GeminiApiClient.MODEL_PRO
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isPro) SunsetPink.copy(alpha = 0.3f) else DarkCard)
                            .border(1.dp, if (isPro) SunsetPink else DarkCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedModel = GeminiApiClient.MODEL_PRO }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gemini 3.1 Pro", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("احترافي عالي الدقة", color = SunsetPink, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // API Key Field
                Text(
                    text = "مفتاح API الخاص بك (Gemini API Key):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("AIzaSy...", color = TextMuted, fontSize = 13.sp) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedIndicatorColor = NeonViolet,
                        unfocusedIndicatorColor = DarkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Temperature Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "درجة الإبداع (Temperature):",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextWhite
                    )
                    Text(
                        text = String.format("%.1f", temperature),
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0.2f..1.0f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonViolet,
                        inactiveTrackColor = DarkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test Connection Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isTesting = true
                            testResult = null
                            val res = GeminiApiClient.testConnection(apiKey)
                            if (res.isSuccess) {
                                isTestSuccess = true
                                testResult = "🟢 متصل بنجاح بالمولد! زمن الاستجابة: ${res.getOrNull()}ms"
                            } else {
                                isTestSuccess = false
                                testResult = "🔴 خطأ في الاتصال: ${res.exceptionOrNull()?.message}"
                            }
                            isTesting = false
                        }
                    },
                    enabled = !isTesting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = TextWhite
                    )
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جارِ اختبار الاتصال بالخادم...", fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("اختبار الاتصال المباشر", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (testResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = testResult!!,
                        color = if (isTestSuccess) Color(0xFF34D399) else SunsetPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(apiKey, selectedModel, temperature)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
            ) {
                Text("حفظ وتطبيق", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextMuted)
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun AiActionButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = TextWhite,
            modifier = Modifier.size(18.dp)
        )
    }
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("ai_generate_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonViolet,
            contentColor = TextWhite
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = TextWhite,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("جارِ التوليد بالذكاء الاصطناعي...", fontWeight = FontWeight.Bold)
        } else {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

