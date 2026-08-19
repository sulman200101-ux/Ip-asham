package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.ImageRecordEntity
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageHistory by viewModel.imageHistory.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()
    val error by viewModel.latestImageError.collectAsState()

    var promptInput by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("dall-e-3") }
    var selectedSize by remember { mutableStateOf("1024x1024") }
    var selectedQuality by remember { mutableStateOf("standard") }
    var selectedStyle by remember { mutableStateOf("vivid") }

    var showPythonCodeSheet by remember { mutableStateOf(false) }
    var zoomedImage by remember { mutableStateOf<ImageRecordEntity?>(null) }

    val samplePrompts = listOf(
        "لوحة زيتية مستقبلية لمختبر أبحاث ذكاء اصطناعي فائق التطور مع عقد عصبية مشعة",
        "رائد فضاء عربي يقرأ مخطوطة قديمة متوهجة على كثبان المريخ عند الغسق بأسلوب سينمائي",
        "رسم ثلاثي الأبعاد مقطعي لمركز بيانات ذكاء اصطناعي يطفو داخل مكعب بلوري ناصع",
        "شعار حديث تجريدي لصقر هندسي ثلاثي الأبعاد يحلق نحو شروق نيون ساطع"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "استوديو توليد الصور DALL·E",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("v3.0", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showPythonCodeSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "كود بايثون SDK",
                            tint = OpenAIGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Prompt Card
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
                            text = "وصف المشهد البصري (Prompt)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("صف الصورة المراد توليدها بدقة وتفاصيل إبداعية غنية...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OpenAIGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Prompt Suggestions
                        Text(
                            text = "أفكار مقترحة:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(samplePrompts) { prompt ->
                                SuggestionChip(
                                    onClick = { promptInput = prompt },
                                    label = { Text(prompt.take(30) + "...", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Model & Style Parameters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Model Toggle
                            Column(modifier = Modifier.weight(1f)) {
                                Text("النموذج", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(
                                        selected = selectedModel == "dall-e-3",
                                        onClick = { selectedModel = "dall-e-3" },
                                        label = { Text("DALL-E 3", fontSize = 11.sp) }
                                    )
                                    FilterChip(
                                        selected = selectedModel == "dall-e-2",
                                        onClick = { selectedModel = "dall-e-2" },
                                        label = { Text("DALL-E 2", fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Style Toggle
                            Column(modifier = Modifier.weight(1f)) {
                                Text("النمط البصري", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(
                                        selected = selectedStyle == "vivid",
                                        onClick = { selectedStyle = "vivid" },
                                        label = { Text("حيوي ودرامي", fontSize = 11.sp) }
                                    )
                                    FilterChip(
                                        selected = selectedStyle == "natural",
                                        onClick = { selectedStyle = "natural" },
                                        label = { Text("طبيعي وواقعي", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Aspect Ratio / Dimensions
                        Text("الأبعاد ونسبة العرض", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedSize == "1024x1024",
                                onClick = { selectedSize = "1024x1024" },
                                label = { Text("مربع 1:1", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedSize == "1024x1792",
                                onClick = { selectedSize = "1024x1792" },
                                label = { Text("طولي 9:16", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedSize == "1792x1024",
                                onClick = { selectedSize = "1792x1024" },
                                label = { Text("عرضي 16:9", fontSize = 11.sp) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Generate Button
                        Button(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    viewModel.generateDalleImage(
                                        prompt = promptInput.trim(),
                                        model = selectedModel,
                                        size = selectedSize,
                                        quality = selectedQuality,
                                        style = selectedStyle
                                    )
                                }
                            },
                            enabled = promptInput.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OpenAIGreen,
                                contentColor = Color.Black
                            )
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("جاري توليد الصورة عبر DALL·E...")
                            } else {
                                Icon(imageVector = Icons.Default.Palette, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد الصورة بواسطة $selectedModel", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Gallery Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "معرض الصور المنشأة (${imageHistory.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Gallery Items
            if (imageHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لم يتم إنشاء أي صور بعد",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "اكتب وصفاً في الأعلى لتوليد أول عمل فني بالذكاء الاصطناعي عبر DALL·E 3",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(imageHistory, key = { it.id }) { item ->
                    ImageGalleryCard(
                        item = item,
                        onZoom = { zoomedImage = item },
                        onCopyPrompt = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", item.prompt))
                            Toast.makeText(context, "تم نسخ الوصف!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Zoom Dialog
    zoomedImage?.let { img ->
        Dialog(onDismissRequest = { zoomedImage = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = img.imageUrl,
                        contentDescription = img.prompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = img.prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (img.revisedPrompt != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "الوصف المحسّن: ${img.revisedPrompt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { zoomedImage = null }) {
                                Text("إغلاق")
                            }
                        }
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
                            text = "كود بايثون لتوليد الصور (DALL·E)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "استدعاء client.images.generate() عبر openai-python",
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
                    code = viewModel.getImagePythonSnippet(promptInput.ifBlank { "لوحة سينمائية لتطور الذكاء الاصطناعي" }),
                    language = "python",
                    title = "client.images.generate()"
                )
            }
        }
    }
}

@Composable
fun ImageGalleryCard(
    item: ImageRecordEntity,
    onZoom: () -> Unit,
    onCopyPrompt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onZoom() }
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.prompt,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = item.model,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = OpenAIGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.prompt,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2
                )
                if (item.revisedPrompt != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الوصف المحسّن: ${item.revisedPrompt}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.size} • ${item.style}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ObsidianSubtext
                    )
                    IconButton(
                        onClick = onCopyPrompt,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ الوصف",
                            modifier = Modifier.size(16.dp),
                            tint = OpenAIGreen
                        )
                    }
                }
            }
        }
    }
}
