package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.data.model.PromptPresets
import com.example.ui.components.CodeBlockView
import com.example.ui.components.ModelSelectorSheet
import com.example.ui.components.ParametersSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGeneratingChat.collectAsState()
    val streamingText by viewModel.liveStreamingText.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val apiKey by viewModel.apiKeyFlow.collectAsState(initial = "")

    var inputPrompt by remember { mutableStateOf("") }
    var showModelSheet by remember { mutableStateOf(false) }
    var showParamsSheet by remember { mutableStateOf(false) }
    var showPythonCodeSheet by remember { mutableStateOf(false) }
    var showSessionsDrawer by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            listState.animateScrollToItem((messages.size).coerceAtLeast(0))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showModelSheet = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (apiKey.isNotBlank()) OpenAIGreen else AmberAccent)
                        )
                        Text(
                            text = currentSession?.model ?: "gpt-4o",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Model",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showSessionsDrawer = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Chat Sessions History"
                        )
                    }
                },
                actions = {
                    // Parameters Slider
                    IconButton(onClick = { showParamsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "معايير الاستجابة",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Python SDK Code Viewer
                    IconButton(onClick = { showPythonCodeSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "عرض كود بايثون SDK",
                            tint = OpenAIGreen
                        )
                    }
                    // New Chat
                    IconButton(onClick = { viewModel.createNewChat() }) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "محادثة جديدة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // API Key Status Banner if offline/sandbox
            if (apiKey.isBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E261E))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "وضع التجربة",
                        tint = OpenAIGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "وضع البيئة التجريبية نشط • أضف مفتاح API في الإعدادات للاتصال السحابي المباشر",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = Color.White
                    )
                }
            }

            // Chat Messages Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // Empty state hero / presets
                if (messages.isEmpty() && !isGenerating) {
                    item {
                        PlaygroundWelcomeCard(
                            currentModel = currentSession?.model ?: "gpt-4o",
                            onSelectPreset = { preset ->
                                viewModel.updateSessionParameters(
                                    temperature = 0.7,
                                    topP = 1.0,
                                    systemPrompt = preset.prompt
                                )
                                inputPrompt = preset.defaultSampleUserMessage
                            }
                        )
                    }
                }

                // Chat Messages List
                items(messages, key = { it.id }) { msg ->
                    ChatMessageBubble(msg = msg)
                }

                // Live Streaming Bubble
                if (isGenerating && streamingText.isNotEmpty()) {
                    item {
                        ChatMessageBubble(
                            msg = ChatMessageEntity(
                                id = "streaming",
                                sessionId = "",
                                role = "assistant",
                                content = streamingText,
                                timestamp = System.currentTimeMillis()
                            ),
                            isStreaming = true
                        )
                    }
                } else if (isGenerating) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = OpenAIGreen
                            )
                            Text(
                                text = "نموذج OpenAI يفكر ويقوم بتوليد الإجابة...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Quick Prompts Chips
                    if (messages.isEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(PromptPresets.PRESETS) { preset ->
                                SuggestionChip(
                                    onClick = { inputPrompt = preset.defaultSampleUserMessage },
                                    label = { Text(preset.title, fontSize = 11.sp) },
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }
                    }

                    // Input Field & Send Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputPrompt,
                            onValueChange = { inputPrompt = it },
                            placeholder = { Text("اكتب رسالة لـ ${currentSession?.model ?: "OpenAI"}...") },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp, max = 130.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OpenAIGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        FilledIconButton(
                            onClick = {
                                val text = inputPrompt.trim()
                                if (text.isNotBlank() && !isGenerating) {
                                    inputPrompt = ""
                                    viewModel.sendUserMessage(text)
                                }
                            },
                            enabled = inputPrompt.isNotBlank() && !isGenerating,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = OpenAIGreen,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "إرسال الرسالة",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // Model Selector Sheet
    if (showModelSheet) {
        ModelSelectorSheet(
            selectedModel = currentSession?.model ?: "gpt-4o",
            onModelSelected = { viewModel.updateSessionModel(it) },
            onDismiss = { showModelSheet = false }
        )
    }

    // Parameters Sheet
    if (showParamsSheet) {
        ParametersSheet(
            temperature = currentSession?.temperature ?: 0.7,
            topP = currentSession?.topP ?: 1.0,
            systemPrompt = currentSession?.systemPrompt ?: "",
            onTemperatureChange = { viewModel.updateSessionParameters(it, currentSession?.topP ?: 1.0, currentSession?.systemPrompt ?: "") },
            onTopPChange = { viewModel.updateSessionParameters(currentSession?.temperature ?: 0.7, it, currentSession?.systemPrompt ?: "") },
            onSystemPromptChange = { viewModel.updateSessionParameters(currentSession?.temperature ?: 0.7, currentSession?.topP ?: 1.0, it) },
            onDismiss = { showParamsSheet = false }
        )
    }

    // Python SDK Code Bottom Sheet
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
                            text = "كود بايثون (openai-python)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "الكود المماثل لهذه الجلسة باستخدام حزمة بايثون الرسمية",
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
                    code = viewModel.getChatPythonSnippet(),
                    language = "python",
                    title = "client.chat.completions.create()"
                )
            }
        }
    }

    // Sessions Drawer / Dialog
    if (showSessionsDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showSessionsDrawer = false },
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
                    Text(
                        text = "سجل المحادثات",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { showSessionsDrawer = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allSessions) { session ->
                        val isSelected = session.id == currentSession?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.selectSession(session.id)
                                    showSessionsDrawer = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OpenAIGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${session.model} • درجة إبداع ${session.temperature}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteSession(session.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.error
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

@Composable
fun PlaygroundWelcomeCard(
    currentModel: String,
    onSelectPreset: (com.example.data.model.SystemPromptPreset) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OpenAIGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = OpenAIGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "مختبر OpenAI التفاعلي",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "متصل بنموذج $currentModel • جاهز لأكواد بايثون v1.x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "اختر قالباً وتوجيهاً برمجياً للبدء أو أرسل رسالتك مباشرة:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (preset in PromptPresets.PRESETS) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPreset(preset) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = OpenAIGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = preset.defaultSampleUserMessage,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    msg: ChatMessageEntity,
    isStreaming: Boolean = false
) {
    val isUser = msg.role == "user"
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            Icon(
                imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
                contentDescription = msg.role,
                tint = if (isUser) CyanAccent else OpenAIGreen,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (isUser) "أنت" else "المساعد الذكي",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isUser && msg.latencyMs > 0) {
                Text(
                    text = "• ${msg.latencyMs} م.ث",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ObsidianSubtext
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Color(0xFF1E3A5F) else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) CyanAccent.copy(alpha = 0.3f) else ObsidianBorder
            ),
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Parse code snippets if present
                val content = msg.content
                if (content.contains("```")) {
                    val parts = content.split("```")
                    for (i in parts.indices) {
                        if (i % 2 == 1) {
                            // Code segment
                            val codePart = parts[i]
                            val lines = codePart.lines()
                            val lang = if (lines.isNotEmpty() && lines[0].isNotBlank()) lines[0].trim() else "python"
                            val codeOnly = lines.drop(1).joinToString("\n")
                            CodeBlockView(
                                code = codeOnly.ifBlank { codePart },
                                language = lang,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        } else if (parts[i].isNotBlank()) {
                            Text(
                                text = parts[i].trim(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                // Copy Action Button
                if (!isUser && !isStreaming) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { clipboardManager.setText(AnnotatedString(msg.content)) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ الرسالة",
                                modifier = Modifier.size(12.dp),
                                tint = OpenAIGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "نسخ",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = OpenAIGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

