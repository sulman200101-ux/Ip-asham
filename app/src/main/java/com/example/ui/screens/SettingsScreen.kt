package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apiKey by viewModel.apiKeyFlow.collectAsState(initial = "")
    val baseUrl by viewModel.baseUrlFlow.collectAsState(initial = "https://api.openai.com/v1/")
    val orgId by viewModel.orgIdFlow.collectAsState(initial = "")
    val projectId by viewModel.projectIdFlow.collectAsState(initial = "")

    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey) }
    var baseUrlInput by remember(baseUrl) { mutableStateOf(baseUrl) }
    var orgIdInput by remember(orgId) { mutableStateOf(orgId) }
    var projectIdInput by remember(projectId) { mutableStateOf(projectId) }

    var isKeyVisible by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات والتكوين",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            // Connection Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = if (apiKey.isNotBlank()) Icons.Default.CheckCircle else Icons.Default.Science,
                            contentDescription = null,
                            tint = if (apiKey.isNotBlank()) OpenAIGreen else AmberAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = if (apiKey.isNotBlank()) "متصل بسحابة OpenAI الحية" else "وضع بيئة التطوير التجريبية (Sandbox)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (apiKey.isNotBlank()) OpenAIGreen else AmberAccent
                            )
                            Text(
                                text = if (apiKey.isNotBlank()) "الطلبات ستخاطب خوادم OpenAI المباشرة باستخدام مفتاح API الخاص بك." else "الاستجابات محاكاة. أضف مفتاح API أدناه للاتصال بالخوادم المباشرة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // API Key Card
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
                            text = "مفتاح OpenAI API Key",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يتم حفظ المفتاح بأمان محلياً على جهازك. يبدأ بـ sk-...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            placeholder = { Text("sk-proj-...") },
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                    Icon(
                                        imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isKeyVisible) "إخفاء المفتاح" else "إظهار المفتاح"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = clipboard.primaryClip
                                    if (clip != null && clip.itemCount > 0) {
                                        apiKeyInput = clip.getItemAt(0).text.toString().trim()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("لصق")
                            }

                            Button(
                                onClick = {
                                    viewModel.saveApiKey(apiKeyInput)
                                    Toast.makeText(context, "تم حفظ مفتاح API بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OpenAIGreen, contentColor = Color.Black),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حفظ المفتاح", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Custom Base URL & Headers Card
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
                            text = "نقطة النهاية المخصصة (Base URL)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يدعم الخوادم المخصصة (مثل OpenRouter أو Azure أو Ollama أو LocalAI)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = baseUrlInput,
                            onValueChange = { baseUrlInput = it },
                            label = { Text("عنوان الرابط الأساسي (Base URL)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = orgIdInput,
                            onValueChange = { orgIdInput = it },
                            label = { Text("معرّف المنظمة OpenAI Org ID (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = projectIdInput,
                            onValueChange = { projectIdInput = it },
                            label = { Text("معرّف المشروع OpenAI Project ID (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.saveBaseUrl(baseUrlInput)
                                viewModel.saveOrgAndProject(orgIdInput, projectIdInput)
                                Toast.makeText(context, "تم حفظ إعدادات الاتصال والشبكة بنجاح!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("حفظ إعدادات نقطة النهاية")
                        }
                    }
                }
            }

            // Data Management Card
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
                            text = "التخزين المحلي وسجل المحادثات",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "قاعدة بيانات Room محلية لحفظ المحادثات والصور المنشأة والتسجيلات الصوتية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showClearHistoryDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مسح كامل السجل المحلي")
                        }
                    }
                }
            }

            // About Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "استوديو OpenAI لأندرويد",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تطبيق متطور وميدان تجارب تفاعلي مبني على Jetpack Compose ومستوحى من حزمة openai-python. يدعم نماذج GPT-4o ونماذج التفكير المنطقي o1 و o3-mini و DALL·E 3 و TTS-1 و Whisper مع مولّد فوري لأكواد بايثون.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("مسح جميع البيانات والسجل؟") },
            text = { Text("سيؤدي هذا الإجراء إلى حذف جميع جلسات الدردشة والصور والتسجيلات الصوتية المخزنة محلياً على جهازك بشكل دائم.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                        Toast.makeText(context, "تم مسح السجل بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("مسح الكل", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
