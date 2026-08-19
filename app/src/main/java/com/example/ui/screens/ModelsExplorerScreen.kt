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
import com.example.data.api.CodeSnippetGenerator
import com.example.data.model.AvailableModels
import com.example.data.model.ModelCategory
import com.example.data.model.ModelInfo
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsExplorerScreen(
    viewModel: StudioViewModel,
    onLaunchPlayground: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<ModelCategory?>(null) }
    var showModelsPythonCode by remember { mutableStateOf(false) }

    val categories = ModelCategory.values().toList()

    val filteredModels = if (selectedCategory == null) {
        AvailableModels.ALL_MODELS
    } else {
        AvailableModels.ALL_MODELS.filter { it.category == selectedCategory }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "دليل نماذج OpenAI",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { showModelsPythonCode = true }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "كود بايثون للنماذج",
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Category Filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("الكل (${AvailableModels.ALL_MODELS.size})", fontSize = 11.5.sp) }
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName, fontSize = 11.5.sp) }
                        )
                    }
                }
            }

            // Model Cards
            items(filteredModels, key = { it.id }) { model ->
                ModelDetailCard(
                    model = model,
                    onLaunchPlayground = { onLaunchPlayground(model.id) },
                    onCopyId = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Model ID", model.id))
                        Toast.makeText(context, "تم نسخ معرّف النموذج ${model.id}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showModelsPythonCode) {
        ModalBottomSheet(
            onDismissRequest = { showModelsPythonCode = false },
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
                            text = "كود بايثون للاستعلام عن النماذج",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "استرجاع قائمة النماذج ديناميكياً عبر client.models.list()",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showModelsPythonCode = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                CodeBlockView(
                    code = CodeSnippetGenerator.generateModelsListPython(),
                    language = "python",
                    title = "client.models.list()"
                )
            }
        }
    }
}

@Composable
fun ModelDetailCard(
    model: ModelInfo,
    onLaunchPlayground: () -> Unit,
    onCopyId: () -> Unit
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
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OpenAIGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = model.id,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = OpenAIGreen,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onCopyId, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ المعرف",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specs grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("نافذة السياق", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ObsidianSubtext)
                    Text(model.contextWindow, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                }
                Column {
                    Text("أقصى إخراج", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ObsidianSubtext)
                    Text(model.maxOutputTokens, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                }
                Column {
                    Text("تاريخ التدريب", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ObsidianSubtext)
                    Text(model.trainingCutoff, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Capabilities and Launch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (model.supportsVision) {
                        SuggestionChip(onClick = {}, label = { Text("رؤية بصرية", fontSize = 9.sp) }, modifier = Modifier.height(22.dp))
                    }
                    if (model.supportsReasoning) {
                        SuggestionChip(onClick = {}, label = { Text("تفكير منطقي", fontSize = 9.sp) }, modifier = Modifier.height(22.dp))
                    }
                    if (model.supportsAudio) {
                        SuggestionChip(onClick = {}, label = { Text("صوت", fontSize = 9.sp) }, modifier = Modifier.height(22.dp))
                    }
                }

                if (model.category == ModelCategory.FLAGSHIP || model.category == ModelCategory.REASONING || model.category == ModelCategory.FAST) {
                    FilledTonalButton(
                        onClick = onLaunchPlayground,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("استخدم في المحادثة", fontSize = 10.5.sp)
                    }
                }
            }
        }
    }
}
