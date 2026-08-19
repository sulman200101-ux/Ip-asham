package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceAvatarCatalog
import com.example.data.model.VoiceEmotion
import com.example.ui.components.AdBannerView
import com.example.ui.components.AiActionButton
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.StudioHeader
import com.example.ui.components.VoiceAvatarCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun VoiceStudioScreen(
    viewModel: StudioViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val voiceText by viewModel.voiceText.collectAsState()
    val selectedAvatar by viewModel.selectedAvatar.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val pitch by viewModel.pitch.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val waveAmps by viewModel.waveAmplitudes.collectAsState()
    val isGenerating by viewModel.isVoiceGenerating.collectAsState()
    val credits by viewModel.credits.collectAsState()

    var promptTopic by remember { mutableStateOf("") }
    var saveTitle by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        bottomBar = { AdBannerView() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                StudioHeader(
                    title = "استوديو الصوت والتعليق AI",
                    subtitle = "توليد أصوات واقعية وشخصيات مميزة",
                    credits = credits,
                    onBackClick = onBackClick,
                    onRewardClick = {
                        viewModel.unlockRewardedCredits(activity) {}
                    }
                )
            }

            // 1. Choose Voice Avatar (Grid)
            item {
                Text(
                    text = "1. اختر شخصية المعلق الصوتي 🎙️",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(VoiceAvatarCatalog.avatars) { avatar ->
                        VoiceAvatarCard(
                            avatar = avatar,
                            isSelected = selectedAvatar.id == avatar.id,
                            onSelect = {
                                if (avatar.isPremium && credits < 15) {
                                    viewModel.unlockRewardedCredits(activity) {
                                        viewModel.setAvatar(avatar)
                                    }
                                } else {
                                    viewModel.setAvatar(avatar)
                                }
                            },
                            modifier = Modifier.width(130.dp)
                        )
                    }
                }
            }

            // 2. Select Emotion / Tone
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "2. نبرة الصوت والمشاعر 🎭",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(VoiceEmotion.values()) { emotion ->
                        val isSelected = selectedEmotion == emotion
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setEmotion(emotion) },
                            color = if (isSelected) NeonViolet else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonViolet else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = emotion.labelAr,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 3. AI Script Generator or Custom Text Input
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "3. صياغة النص الذكي (Gemini AI)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptTopic,
                            onValueChange = { promptTopic = it },
                            placeholder = { Text("اكتب موضوع التعليق (مثال: وثائقي عن المجرات، نصيحة صباحية، إعلان عصير)...", fontSize = 13.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AiActionButton(
                            text = "اكتب النص بالذكاء الاصطناعي ✨",
                            isLoading = isGenerating,
                            onClick = {
                                viewModel.generateAiVoiceScript(promptTopic.ifBlank { "رسالة ترحيبية ملهمة ومحفزة للنجاح" })
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "النص الصوتي النهائي:",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = voiceText,
                            onValueChange = { viewModel.setVoiceText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }
                }
            }

            // 4. Voice Controls (Pitch & Speed)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("طبقة الصوت (Pitch):", color = TextWhite, fontSize = 14.sp)
                            Text(String.format("%.2fx", pitch), color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = pitch,
                            onValueChange = { viewModel.setPitch(it) },
                            valueRange = 0.5f..1.8f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = DarkCardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سرعة الإلقاء (Speed):", color = TextWhite, fontSize = 14.sp)
                            Text(String.format("%.2fx", speechRate), color = NeonViolet, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = speechRate,
                            onValueChange = { viewModel.setSpeechRate(it) },
                            valueRange = 0.5f..1.8f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonViolet,
                                activeTrackColor = NeonViolet,
                                inactiveTrackColor = DarkCardBorder
                            )
                        )
                    }
                }
            }

            // 5. Audio Waveform Visualizer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AudioWaveformVisualizer(
                        amplitudes = waveAmps,
                        isPlaying = isPlaying,
                        height = 70.dp
                    )
                }
            }

            // 6. Play / Stop / Save Buttons
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isPlaying) {
                                viewModel.stopAudio()
                            } else {
                                viewModel.playVoice()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("play_voice_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) SunsetPink else NeonViolet
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlaying) "إيقاف الصوت" else "تشغيل ونطق الصوت",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveVoiceProject(saveTitle.ifBlank { "تعليق: ${selectedAvatar.nameAr}" }, activity)
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("save_voice_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "حفظ",
                            tint = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
