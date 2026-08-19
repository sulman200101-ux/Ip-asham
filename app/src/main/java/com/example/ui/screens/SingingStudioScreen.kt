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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MusicNote
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
import com.example.data.model.MusicGenre
import com.example.data.model.VocalStyle
import com.example.ui.components.AdBannerView
import com.example.ui.components.AiActionButton
import com.example.ui.components.AudioWaveformVisualizer
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
fun SingingStudioScreen(
    viewModel: StudioViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lyrics by viewModel.songLyrics.collectAsState()
    val songTheme by viewModel.songTheme.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedVocalStyle by viewModel.selectedVocalStyle.collectAsState()
    val isSingingPlaying by viewModel.isSingingPlaying.collectAsState()
    val isGenerating by viewModel.isSongGenerating.collectAsState()
    val activeNoteIndex by viewModel.currentSingingNoteIndex.collectAsState()
    val songLines by viewModel.songLines.collectAsState()
    val waveAmps by viewModel.waveAmplitudes.collectAsState()
    val credits by viewModel.credits.collectAsState()

    var themeInput by remember { mutableStateOf("") }

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
                    title = "استوديو الغناء والتلحين AI",
                    subtitle = "تأليف الكلمات وتوليد غناء ونغمات موسيقية حية",
                    credits = credits,
                    onBackClick = onBackClick,
                    onRewardClick = {
                        viewModel.unlockRewardedCredits(activity) {}
                    }
                )
            }

            // 1. Genre Selection
            item {
                Text(
                    text = "1. اختر اللون الموسيقي والإيقاع 🎵",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(MusicGenre.values()) { genre ->
                        val isSelected = selectedGenre == genre
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { viewModel.setGenre(genre) }
                                .testTag("genre_${genre.name}"),
                            color = if (isSelected) SunsetPink else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SunsetPink else DarkCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = genre.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = genre.titleAr,
                                        color = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${genre.bpm} BPM",
                                        color = if (isSelected) TextWhite.copy(alpha = 0.8f) else TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Vocal Style
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "2. أسلوب الغناء والصوت 🎤",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(VocalStyle.values()) { style ->
                        val isSelected = selectedVocalStyle == style
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setVocalStyle(style) },
                            color = if (isSelected) NeonViolet else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonViolet else DarkCardBorder
                            )
                        ) {
                            Text(
                                text = style.labelAr,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 3. AI Songwriting Composer (Gemini)
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
                                text = "3. تأليف الكلمات بالذكاء الاصطناعي",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SunsetPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = themeInput,
                            onValueChange = { themeInput = it },
                            placeholder = { Text("اكتب فكرة الأغنية (مثال: أغنية حماسية عن النجاح، أغنية طرب عن الشوق، أغنية للأطفال)...", fontSize = 13.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SunsetPink,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AiActionButton(
                            text = "ألّف كلمات الأغنية واللحن ✨",
                            isLoading = isGenerating,
                            onClick = {
                                viewModel.generateAiSongLyrics(themeInput.ifBlank { "أغنية ملهمة عن الأمل والمستقبل والنجاح" })
                            }
                        )
                    }
                }
            }

            // 4. Karaoke Interactive Lyric Viewer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16122E))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "شاشة الكاريوكي والألحان 🎤",
                                style = MaterialTheme.typography.titleMedium,
                                color = SunsetPink,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSingingPlaying) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SunsetPink.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "غناء حي متزامن 🎶",
                                        color = SunsetPink,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val displayLines = if (songLines.isNotEmpty()) songLines.map { it.arabicText } else lyrics.lines().filter { it.isNotBlank() }

                        displayLines.forEachIndexed { index, line ->
                            val isCurrentSinging = isSingingPlaying && (activeNoteIndex % displayLines.size) == index
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrentSinging) SunsetPink.copy(alpha = 0.25f) else Color.Transparent
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isCurrentSinging) {
                                        Text(text = "🎵", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = line,
                                        color = if (isCurrentSinging) SunsetPink else TextWhite,
                                        fontSize = if (isCurrentSinging) 16.sp else 14.sp,
                                        fontWeight = if (isCurrentSinging) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Waveform Visualizer
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AudioWaveformVisualizer(
                        amplitudes = waveAmps,
                        isPlaying = isSingingPlaying,
                        height = 70.dp,
                        barColorStart = SunsetPink,
                        barColorEnd = NeonCyan
                    )
                }
            }

            // 6. Singing Play / Stop / Save Buttons
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
                            if (isSingingPlaying) {
                                viewModel.stopAudio()
                            } else {
                                viewModel.playSingingSong()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("play_song_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSingingPlaying) SunsetCoral else SunsetPink
                        )
                    ) {
                        Icon(
                            imageVector = if (isSingingPlaying) Icons.Default.Stop else Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSingingPlaying) "إيقاف الغناء" else "غناء الأغنية بالذكاء الاصطناعي 🎤",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveSongProject("أغنية: ${selectedGenre.titleAr}", activity)
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("save_song_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "حفظ",
                            tint = SunsetPink
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
