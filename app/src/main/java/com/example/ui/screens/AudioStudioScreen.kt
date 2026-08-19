package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AudioRecordEntity
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioHistory by viewModel.audioHistory.collectAsState()
    val isGenerating by viewModel.isGeneratingAudio.collectAsState()
    val activePlayingId by viewModel.activePlayingAudioId.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = TTS, 1 = Whisper
    var ttsInput by remember { mutableStateOf("تتيح مكتبة بايثون الخاصة بـ OpenAI توليد صوت بشري طبيعي عالي الجودة وفائق الواقعية في الوقت الفعلي.") }
    var selectedVoice by remember { mutableStateOf("alloy") }
    var selectedTtsModel by remember { mutableStateOf("tts-1-hd") }
    var speed by remember { mutableStateOf(1.0f) }

    var whisperSimulatedText by remember { mutableStateOf("يوفر نموذج Whisper من OpenAI قدرات متقدمة للتعرف على الكلام الصوتي وتحويله إلى نصوص متعددة اللغات بدقة مذهلة.") }
    var showPythonCodeSheet by remember { mutableStateOf(false) }

    val voices = listOf(
        Pair("alloy", "متوازن ومحايد"),
        Pair("echo", "دافئ وديناميكي"),
        Pair("fable", "تعبيري وسردي"),
        Pair("onyx", "عميق وواثق"),
        Pair("nova", "ودود ومفعم بالحيوية"),
        Pair("shimmer", "نقي ومتفائل")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "استوديو الصوت والكلام",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { showPythonCodeSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "كود بايثون للصوت",
                            tint = OpenAIGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OpenAIGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("تحويل النص إلى صوت (TTS)")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("تحويل الصوت إلى نص (Whisper)")
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (selectedTab == 0) {
                    // TTS Form
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "النص المراد تحويله لصوت",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = ttsInput,
                                    onValueChange = { ttsInput = it },
                                    placeholder = { Text("أدخل النص المراد نطقه بصوت ذكاء اصطناعي طبيعي...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Voice Selector
                                Text("اختر نبرة الصوت وشخصية المتحدث", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(voices) { (voiceName, voiceDesc) ->
                                        val isSelected = selectedVoice == voiceName
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedVoice = voiceName },
                                            label = {
                                                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                                    Text(
                                                        text = voiceName.replaceFirstChar { it.uppercase() },
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.5.sp
                                                    )
                                                    Text(
                                                        text = voiceDesc,
                                                        fontSize = 9.sp,
                                                        color = if (isSelected) Color.White else ObsidianSubtext
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Model & Speed
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilterChip(
                                            selected = selectedTtsModel == "tts-1-hd",
                                            onClick = { selectedTtsModel = "tts-1-hd" },
                                            label = { Text("TTS-1 HD (دقة فائقة)", fontSize = 11.sp) }
                                        )
                                        FilterChip(
                                            selected = selectedTtsModel == "tts-1",
                                            onClick = { selectedTtsModel = "tts-1" },
                                            label = { Text("TTS-1 (استجابة سريعة)", fontSize = 11.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("سرعة النطق: ${String.format("%.2f", speed)}x", style = MaterialTheme.typography.labelSmall)
                                Slider(
                                    value = speed,
                                    onValueChange = { speed = it },
                                    valueRange = 0.5f..2.0f,
                                    steps = 6,
                                    colors = SliderDefaults.colors(thumbColor = OpenAIGreen, activeTrackColor = OpenAIGreen)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (ttsInput.isNotBlank()) {
                                            viewModel.generateSpeech(
                                                text = ttsInput.trim(),
                                                voice = selectedVoice,
                                                model = selectedTtsModel,
                                                speed = speed.toDouble()
                                            )
                                        }
                                    },
                                    enabled = ttsInput.isNotBlank() && !isGenerating,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OpenAIGreen,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    if (isGenerating) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("جاري توليد الملف الصوتي...")
                                    } else {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("توليد الصوت بنبرة $selectedVoice", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Whisper Form
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "التعرف الصوتي بواسطة Whisper-1",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "تحويل التسجيلات الصوتية والكلام الحي إلى نصوص دقيقة متعددة اللغات.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = whisperSimulatedText,
                                    onValueChange = { whisperSimulatedText = it },
                                    label = { Text("النص أو الملاحظة الصوتية للتجربة") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.transcribeWhisperAudio(whisperSimulatedText) },
                                    enabled = !isGenerating,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyanAccent,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    if (isGenerating) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("جاري تفريغ الصوت عبر Whisper...")
                                    } else {
                                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("بدء التفريغ الصوتي (Whisper)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Audio Records List
                item {
                    Text(
                        text = "مكتبة التسجيلات والأصوات (${audioHistory.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (audioHistory.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("لا توجد تسجيلات صوتية بعد", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text("قم بتوليد صوت أو تفريغ كلام لإثراء مكتبتك الصوتية.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(audioHistory, key = { it.id }) { item ->
                        AudioRecordCard(
                            record = item,
                            isPlaying = activePlayingId == item.id,
                            onTogglePlay = { viewModel.toggleAudioPlayback(item.id) },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Audio Text", item.text))
                                Toast.makeText(context, "تم نسخ النص!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Python SDK Code Sheet
    if (showPythonCodeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPythonCodeSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (selectedTab == 0) "كود بايثون لتوليد الصوت (Speech TTS)" else "كود بايثون لتفريغ الصوت (Whisper)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (selectedTab == 0) "استدعاء client.audio.speech.create()" else "استدعاء client.audio.transcriptions.create()",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showPythonCodeSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                CodeBlockView(
                    code = if (selectedTab == 0) viewModel.getTtsPythonSnippet(ttsInput, selectedVoice) else viewModel.getWhisperPythonSnippet(),
                    language = "python",
                    title = if (selectedTab == 0) "client.audio.speech.create()" else "client.audio.transcriptions.create()"
                )
            }
        }
    }
}

@Composable
fun AudioRecordCard(
    record: AudioRecordEntity,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (record.type.startsWith("TTS")) OpenAIGreen.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (record.type.startsWith("TTS")) Icons.Default.VolumeUp else Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (record.type.startsWith("TTS")) OpenAIGreen else CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "${record.type} • ${record.model}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "الصوت: ${record.voiceOrLang}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ObsidianSubtext
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ النص", modifier = Modifier.size(16.dp))
                    }
                    FilledIconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isPlaying) AmberAccent else OpenAIGreen
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "تشغيل/إيقاف مؤقت",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = record.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isPlaying) {
                Spacer(modifier = Modifier.height(10.dp))
                AudioWaveVisualizer(isPlaying = true)
            }
        }
    }
}
