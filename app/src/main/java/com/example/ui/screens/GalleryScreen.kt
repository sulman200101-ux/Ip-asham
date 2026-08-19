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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectType
import com.example.ui.components.AdBannerView
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
fun GalleryScreen(
    viewModel: StudioViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val allProjects by viewModel.allProjects.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isSingingPlaying by viewModel.isSingingPlaying.collectAsState()
    val credits by viewModel.credits.collectAsState()

    var selectedFilter by remember { mutableStateOf<ProjectType?>(null) }

    val filteredProjects = if (selectedFilter == null) {
        allProjects
    } else {
        allProjects.filter { it.projectType == selectedFilter }
    }

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
                    title = "معرض الإنتاج والمشاريع 📁",
                    subtitle = "كل التسجيلات والأغاني والفيديوهات المحفوظة",
                    credits = credits,
                    onBackClick = onBackClick,
                    onRewardClick = {
                        viewModel.unlockRewardedCredits(activity) {}
                    }
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isAllSelected = selectedFilter == null
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedFilter = null },
                            color = if (isAllSelected) NeonViolet else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAllSelected) NeonViolet else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = "الكل (${allProjects.size})",
                                color = if (isAllSelected) TextWhite else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        val isVoiceSelected = selectedFilter == ProjectType.VOICE_OVER
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedFilter = ProjectType.VOICE_OVER },
                            color = if (isVoiceSelected) NeonViolet else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVoiceSelected) NeonViolet else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = "أصوات 🎙️",
                                color = if (isVoiceSelected) TextWhite else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        val isSongSelected = selectedFilter == ProjectType.AI_SONG
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedFilter = ProjectType.AI_SONG },
                            color = if (isSongSelected) SunsetPink else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSongSelected) SunsetPink else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = "أغانٍ 🎵",
                                color = if (isSongSelected) TextWhite else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        val isVideoSelected = selectedFilter == ProjectType.ANIMATED_VIDEO
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedFilter = ProjectType.ANIMATED_VIDEO },
                            color = if (isVideoSelected) NeonCyan else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVideoSelected) NeonCyan else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = "فيديوهات 🎬",
                                color = if (isVideoSelected) TextWhite else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Project Items
            if (filteredProjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✨", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد مشاريع محفوظة في هذا القسم بعد",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredProjects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("gallery_project_${project.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (project.projectType) {
                                                ProjectType.VOICE_OVER -> NeonViolet.copy(alpha = 0.25f)
                                                ProjectType.AI_SONG -> SunsetPink.copy(alpha = 0.25f)
                                                ProjectType.ANIMATED_VIDEO -> NeonCyan.copy(alpha = 0.25f)
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
                                        fontSize = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = project.title,
                                        color = TextWhite,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = project.description,
                                        color = TextMuted,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        when (project.projectType) {
                                            ProjectType.VOICE_OVER -> {
                                                if (isPlaying) {
                                                    viewModel.stopAudio()
                                                } else {
                                                    viewModel.setVoiceText(project.contentText)
                                                    viewModel.playVoice()
                                                }
                                            }
                                            ProjectType.AI_SONG -> {
                                                if (isSingingPlaying) {
                                                    viewModel.stopAudio()
                                                } else {
                                                    viewModel.setSongLyrics(project.contentText)
                                                    viewModel.playSingingSong()
                                                }
                                            }
                                            ProjectType.ANIMATED_VIDEO -> {
                                                viewModel.setVoiceText(project.contentText)
                                                viewModel.playVoice()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying || isSingingPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "تشغيل",
                                        tint = NeonCyan
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteProject(project.id)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = SunsetCoral.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
