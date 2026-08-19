package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProjectType
import com.example.ui.components.AdBannerView
import com.example.ui.components.FeatureCard
import com.example.ui.components.StudioHeader
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SunsetCoral
import com.example.ui.theme.SunsetPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    onNavigateToVoice: () -> Unit,
    onNavigateToSinging: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val credits by viewModel.credits.collectAsState()
    val recentProjects by viewModel.allProjects.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            AdBannerView()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header
            item {
                StudioHeader(
                    title = "صوت وأنيميشن AI",
                    subtitle = "استوديو توليد الصوت والغناء والرسوم المتحركة",
                    credits = credits,
                    onRewardClick = {
                        viewModel.unlockRewardedCredits(activity) {}
                    }
                )
            }

            // Hero Visual Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF1E1B4B))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.studio_hero_banner),
                        contentDescription = "استوديو الإبداع الذكي",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF0B0F19).copy(alpha = 0.85f))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonViolet.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TextWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مدعوم بنماذج Gemini AI الفائقة",
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "حوّل أفكارك إلى أصوات وأغانٍ وفيديوهات متحركة",
                            color = TextWhite,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Studio Hub Features Section
            item {
                Text(
                    text = "استوديوهات الإنتاج الذكي 🚀",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                )
            }

            // 1. Voice AI Studio Card
            item {
                FeatureCard(
                    title = "استوديو الصوت والتعليق الذكي",
                    description = "تحويل النص إلى كلام، أصوات شخصيات وثائقية وكرتونية وروبوتية مع التحكم في النبرة والمشاعر",
                    iconEmoji = "🎙️",
                    gradientColors = listOf(NeonViolet, Color(0xFF6366F1)),
                    badgeText = "10+ أصوات",
                    onClick = onNavigateToVoice,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("hub_voice_card")
                )
            }

            // 2. Singing & Music Studio Card
            item {
                FeatureCard(
                    title = "استوديو الغناء وتأليف الأغاني",
                    description = "تأليف كلمات الأغاني بالذكاء الاصطناعي، تلحين وغناء مباشر مع إيقاعات بوب، لو-فاي، وطرب شرقي",
                    iconEmoji = "🎤",
                    gradientColors = listOf(SunsetPink, SunsetCoral),
                    badgeText = "غناء وموسيقى",
                    onClick = onNavigateToSinging,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("hub_singing_card")
                )
            }

            // 3. Animated Video Creator Card
            item {
                FeatureCard(
                    title = "صانع الفيديو والرسوم المتحركة",
                    description = "تحويل الأفكار إلى ستوري بورد وفيديوهات متحركة بدقة 60fps مع تأثيرات نيون، فضاء، وشاشات ريلز ويوتيوب",
                    iconEmoji = "🎬",
                    gradientColors = listOf(NeonCyan, Color(0xFF0284C7)),
                    badgeText = "أنيميشن 60fps",
                    onClick = onNavigateToVideo,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("hub_video_card")
                )
            }

            // Gallery Quick Access Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onNavigateToGallery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .testTag("open_gallery_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCard,
                        contentColor = NeonCyan
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "معرض مشاريعي ومكتبة الإنتاج (${recentProjects.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Recent Projects
            if (recentProjects.isNotEmpty()) {
                item {
                    Text(
                        text = "أحدث الإبداعات المحفوظة 📂",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }

                items(recentProjects.take(3)) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (project.projectType) {
                                                ProjectType.VOICE_OVER -> NeonViolet.copy(alpha = 0.2f)
                                                ProjectType.AI_SONG -> SunsetPink.copy(alpha = 0.2f)
                                                ProjectType.ANIMATED_VIDEO -> NeonCyan.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (project.projectType) {
                                            ProjectType.VOICE_OVER -> "🎙️"
                                            ProjectType.AI_SONG -> "🎵"
                                            ProjectType.ANIMATED_VIDEO -> "🎬"
                                        },
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = project.title,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = project.description,
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    when (project.projectType) {
                                        ProjectType.VOICE_OVER -> {
                                            viewModel.setVoiceText(project.contentText)
                                            viewModel.playVoice()
                                        }
                                        ProjectType.AI_SONG -> {
                                            viewModel.setSongLyrics(project.contentText)
                                            viewModel.playSingingSong()
                                        }
                                        ProjectType.ANIMATED_VIDEO -> {
                                            onNavigateToVideo()
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "تشغيل",
                                    tint = NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
