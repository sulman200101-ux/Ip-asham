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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Movie
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
import com.example.data.model.AnimationStyle
import com.example.data.model.VideoAspectRatio
import com.example.ui.components.AdBannerView
import com.example.ui.components.AiActionButton
import com.example.ui.components.CanvasVideoPlayer
import com.example.ui.components.StudioHeader
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetCoral
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun VideoStudioScreen(
    viewModel: StudioViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val videoPrompt by viewModel.videoPrompt.collectAsState()
    val aspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val animStyle by viewModel.selectedAnimationStyle.collectAsState()
    val scenes by viewModel.storyboardScenes.collectAsState()
    val currentSceneIndex by viewModel.currentSceneIndex.collectAsState()
    val isGenerating by viewModel.isVideoGenerating.collectAsState()
    val isPlaying by viewModel.isVideoPlaying.collectAsState()
    val credits by viewModel.credits.collectAsState()

    var storyInput by remember { mutableStateOf("") }
    val currentScene = scenes.getOrNull(currentSceneIndex)

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
                    title = "صانع الفيديو والرسوم المتحركة",
                    subtitle = "توليد فيديوهات كرتونية وأنيميشن تفاعلي بدقة 60fps",
                    credits = credits,
                    onBackClick = onBackClick,
                    onRewardClick = {
                        viewModel.unlockRewardedCredits(activity) {}
                    }
                )
            }

            // 1. Live 60 FPS Video Canvas Player
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CanvasVideoPlayer(
                        currentScene = currentScene,
                        style = animStyle,
                        aspectRatio = aspectRatio,
                        isPlaying = isPlaying,
                        progress = 0.5f,
                        modifier = Modifier.fillMaxWidth(if (aspectRatio == VideoAspectRatio.PORTRAIT_9_16) 0.68f else 1.0f)
                    )
                }
            }

            // Timeline Scene Selector Tabs
            item {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(scenes) { index, scene ->
                        val isSelected = currentSceneIndex == index
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.setCurrentSceneIndex(index)
                                    viewModel.setVoiceText(scene.textCaption)
                                    if (isPlaying) {
                                        viewModel.playVoice()
                                    }
                                }
                                .testTag("scene_tab_$index"),
                            color = if (isSelected) NeonCyan else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = scene.characterEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المشهد ${index + 1}",
                                    color = if (isSelected) Color.Black else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Video Play / Pause / Save Controls
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (currentScene != null) {
                                viewModel.setVoiceText(currentScene.textCaption)
                            }
                            viewModel.toggleVideoPlayback()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("toggle_video_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) SunsetCoral else NeonCyan,
                            contentColor = if (isPlaying) TextWhite else Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isPlaying) TextWhite else Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "إيقاف الفيديو" else "معاينة الفيديو بالصوت والحركة",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveVideoProject("فيديو: ${currentScene?.title ?: videoPrompt.take(20)}", activity)
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("save_video_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "حفظ",
                            tint = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. Aspect Ratio Selector (Reels 9:16 / YouTube 16:9 / Square 1:1)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "2. مقاس وأبعاد الفيديو 📐",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(VideoAspectRatio.values()) { ratio ->
                        val isSelected = aspectRatio == ratio
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.setAspectRatio(ratio) }
                                .testTag("ratio_${ratio.name}"),
                            color = if (isSelected) NeonViolet else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonViolet else DarkCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = ratio.iconEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ratio.titleAr,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 3. Animation Visual Style
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "3. نمط ومؤثرات الأنيميشن 🎨",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(AnimationStyle.values()) { style ->
                        val isSelected = animStyle == style
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.setAnimationStyle(style) },
                            color = if (isSelected) Color(style.primaryColor) else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(style.secondaryColor) else DarkCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = style.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = style.titleAr,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 4. AI Storyboard Generator (Gemini)
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
                                text = "4. توليد ستوري بورد الفيديو (Gemini AI)",
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
                            value = storyInput,
                            onValueChange = { storyInput = it },
                            placeholder = { Text("اكتب فكرة المشهد أو القصة المتحركة (مثال: رحلة سفينة فضائية، قطة صغيرة تتعلم الطيران، مدينة مستقبلية عائمة)...", fontSize = 13.sp, color = TextMuted) },
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
                            text = "أنشئ مشاهد الأنيميشن بالذكاء الاصطناعي 🎬",
                            isLoading = isGenerating,
                            onClick = {
                                viewModel.generateAiStoryboard(storyInput.ifBlank { "مغامرة كرتونية ممتعة في عالم سحري مليء بالنجوم" })
                            }
                        )
                    }
                }
            }
        }
    }
}
