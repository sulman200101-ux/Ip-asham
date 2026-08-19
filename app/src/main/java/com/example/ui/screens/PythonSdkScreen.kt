package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.*

data class SdkRecipe(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val code: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonSdkScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "الاستراتيجيات والتحليل", "الدردشة", "التفكير المنطقي", "المخرجات المنظمة", "الصوت والرؤية", "التضمين الشعاعي", "الأدوات والدوال")

    val recipes = remember {
        listOf(
            SdkRecipe(
                id = "init_client",
                title = "تهيئة العميل (Sync و Async)",
                category = "الدردشة",
                description = "تهيئة عميل OpenAI عبر المتغيرات البيئية أو عنوان خادم مخصص.",
                code = """
import os
from openai import OpenAI, AsyncOpenAI

# Synchronous client (reads OPENAI_API_KEY from env)
client = OpenAI(
    api_key=os.environ.get("OPENAI_API_KEY"),
    base_url="https://api.openai.com/v1/", # Optional custom proxy/Ollama
    max_retries=3,
    timeout=20.0,
)

# Asynchronous client for asyncio / FastAPI / tornado
async_client = AsyncOpenAI()
                """.trimIndent()
            ),
            SdkRecipe(
                id = "chat_stream",
                title = "البث المباشر للردود (Streaming)",
                category = "الدردشة",
                description = "استقبال أجزاء الرد تدريجياً في الوقت الفعلي مع انعدام التأخير.",
                code = """
from openai import OpenAI

client = OpenAI()

stream = client.chat.completions.create(
    model="gpt-4o",
    messages=[
        {"role": "system", "content": "You are a helpful software architect."},
        {"role": "user", "content": "Explain Redis Pub/Sub architecture."}
    ],
    temperature=0.7,
    stream=True,
)

for chunk in stream:
    if chunk.choices[0].delta.content is not None:
        print(chunk.choices[0].delta.content, end="", flush=True)
print()
                """.trimIndent()
            ),
            SdkRecipe(
                id = "reasoning_o1",
                title = "نماذج التفكير والتعليل (o1 / o3-mini)",
                category = "التفكير المنطقي",
                description = "استخدام رموز التفكير المتعمقة للبراهين الخوارزمية وحل المسائل البرمجية المعقدة.",
                code = """
from openai import OpenAI

client = OpenAI()

# o3-mini and o1 use max_completion_tokens instead of max_tokens
response = client.chat.completions.create(
    model="o3-mini",
    messages=[
        {
            "role": "user",
            "content": "Write an optimal 2-D Segment Tree implementation in Python with range query updates."
        }
    ],
    max_completion_tokens=4096,
)

print(response.choices[0].message.content)
                """.trimIndent()
            ),
            SdkRecipe(
                id = "structured_pydantic",
                title = "المخرجات المنظمة الصارمة مع Pydantic",
                category = "المخرجات المنظمة",
                description = "ضمان الحصول على بيانات JSON مطابقة بنسبة 100% لنموذج Pydantic محدد مسبقاً.",
                code = """
from pydantic import BaseModel
from openai import OpenAI

client = OpenAI()

class ResearchPaperSummary(BaseModel):
    title: str
    key_findings: list[str]
    methodology: str
    confidence_score: float

completion = client.beta.chat.completions.parse(
    model="gpt-4o-2024-08-06",
    messages=[
        {"role": "system", "content": "Extract structured research metadata."},
        {"role": "user", "content": "FlashAttention reduces memory complexity from O(N^2) to O(N)..."}
    ],
    response_format=ResearchPaperSummary,
)

summary: ResearchPaperSummary = completion.choices[0].message.parsed
print(f"Title: {summary.title}")
print(f"Key findings: {summary.key_findings}")
                """.trimIndent()
            ),
            SdkRecipe(
                id = "vision_multimodal",
                title = "تحليل الصور المتعدد الوسائط (Vision)",
                category = "الصوت والرؤية",
                description = "إرسال روابط صور عالية الدقة أو صور بصيغة Base64 إلى GPT-4o لتحليلها بدقة.",
                code = """
from openai import OpenAI

client = OpenAI()

response = client.chat.completions.create(
    model="gpt-4o",
    messages=[
        {
            "role": "user",
            "content": [
                {"type": "text", "text": "What UI components and layout flaws do you see in this screenshot?"},
                {
                    "type": "image_url",
                    "image_url": {
                        "url": "https://example.com/mobile_app_screenshot.png",
                        "detail": "high"
                    }
                }
            ]
        }
    ],
    max_tokens=1000
)

print(response.choices[0].message.content)
                """.trimIndent()
            ),
            SdkRecipe(
                id = "audio_speech_whisper",
                title = "توليد الصوت وتحويل الكلام لنص (TTS & Whisper)",
                category = "الصوت والرؤية",
                description = "توليد أصوات بشرية طبيعية وتفريغ التسجيلات الصوتية بدقة متناهية.",
                code = """
from openai import OpenAI

client = OpenAI()

# 1. Synthesize Speech (TTS-1-HD)
speech = client.audio.speech.create(
    model="tts-1-hd",
    voice="nova",
    input="Welcome to the OpenAI Python SDK interactive guide on Android."
)
speech.stream_to_file("output.mp3")

# 2. Transcribe Audio (Whisper-1)
with open("output.mp3", "rb") as audio:
    transcription = client.audio.transcriptions.create(
        model="whisper-1",
        file=audio,
        response_format="text"
    )
print("Transcript:", transcription)
                """.trimIndent()
            ),
            SdkRecipe(
                id = "embeddings_v3",
                title = "التضمين الشعاعي والبحث الدلالي (Embeddings)",
                category = "التضمين الشعاعي",
                description = "إنشاء متجهات دلالية كثيفة لأنظمة استرجاع المعلومات RAG ومحركات البحث الذكية.",
                code = """
from openai import OpenAI

client = OpenAI()

response = client.embeddings.create(
    model="text-embedding-3-small",
    input=[
        "Semantic similarity search in vector databases",
        "openai-python official SDK documentation"
    ],
    dimensions=512, # Optional dimension reduction
)

for data in response.data:
    print(f"Vector Index: {data.index}, Dim: {len(data.embedding)}")
                """.trimIndent()
            ),
            SdkRecipe(
                id = "tools_functions",
                title = "استدعاء الدوال والأدوات (Function Calling)",
                category = "الأدوات والدوال",
                description = "تعريف واستدعاء أدوات ودوال برمجية واستقبال المعاملات المعمارية بدقة.",
                code = """
import json
from openai import OpenAI

client = OpenAI()

tools = [{
    "type": "function",
    "function": {
        "name": "get_stock_price",
        "description": "Fetch real-time ticker quotes",
        "parameters": {
            "type": "object",
            "properties": {
                "symbol": {"type": "string", "description": "e.g. AAPL, GOOG"}
            },
            "required": ["symbol"]
        }
    }
}]

response = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "What is the price of GOOG?"}],
    tools=tools,
    tool_choice="auto"
)

tool_calls = response.choices[0].message.tool_calls
if tool_calls:
    for call in tool_calls:
        fn_name = call.function.name
        fn_args = json.loads(call.function.arguments)
        print(f"Model called: {fn_name}({fn_args})")
                """.trimIndent()
            ),
            SdkRecipe(
                id = "news_sentiment_analysis",
                title = "استراتيجية جلب وتحليل الأخبار المالية والفورية",
                category = "الاستراتيجيات والتحليل",
                description = "جلب وتلخيص الأخبار في الوقت الفعلي واستخراج معنويات السوق (Sentiment) ودرجة التأثير.",
                code = """
from openai import OpenAI
from pydantic import BaseModel

client = OpenAI()

class NewsAnalysisReport(BaseModel):
    headline: str
    sentiment_score: float  # -1.0 (Bearish) to +1.0 (Bullish)
    impact_level: str       # Low, Medium, High
    key_drivers: list[str]
    strategic_action: str

news_text = "Tech leaders report 45% surge in server infrastructure orders."

completion = client.beta.chat.completions.parse(
    model="gpt-4o",
    messages=[
        {"role": "system", "content": "Analyze market news with precise quantitative sentiment."},
        {"role": "user", "content": news_text}
    ],
    response_format=NewsAnalysisReport,
)

report = completion.choices[0].message.parsed
print(f"Sentiment: {report.sentiment_score}, Action: {report.strategic_action}")
                """.trimIndent()
            ),
            SdkRecipe(
                id = "data_accuracy_benchmark",
                title = "اختبار دقة عرض وتدقيق البيانات (Precision Benchmark)",
                category = "الاستراتيجيات والتحليل",
                description = "فحص الاتساق الرقمي والتحقق من صحة المخرجات المستخرجة ومكافحة الهلوسة.",
                code = """
from openai import OpenAI
from pydantic import BaseModel

client = OpenAI()

class AccuracyMetrics(BaseModel):
    precision_score: float
    recall_rate: float
    hallucination_detected: bool
    data_consistency_index: float
    discrepancies: list[str]

raw_data = "Transaction volume: 15,420 with 99.8% match accuracy."

completion = client.beta.chat.completions.parse(
    model="o3-mini", # Utilizing high-precision reasoning
    messages=[
        {"role": "system", "content": "Execute rigorous benchmark on data accuracy and detect inconsistencies."},
        {"role": "user", "content": raw_data}
    ],
    response_format=AccuracyMetrics,
)

metrics = completion.choices[0].message.parsed
print(f"Data Precision: {metrics.precision_score}%, Consistency: {metrics.data_consistency_index}%")
                """.trimIndent()
            )
        )
    }

    val filteredRecipes = if (selectedCategory == "الكل") {
        recipes
    } else {
        recipes.filter { it.category == selectedCategory }
    }

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
                            text = "مكتبة openai-python SDK",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("v1.60+", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.height(24.dp)
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
            // Package Banner Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = OpenAIGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "مكتبة OpenAI الرسمية للغة بايثون",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "github.com/openai/openai-python",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = OpenAIGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // pip install command box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CodeBgDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "pip install openai",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = ObsidianText
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("pip", "pip install openai"))
                                        Toast.makeText(context, "تم نسخ أمر التثبيت!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "نسخ الأمر",
                                        tint = OpenAIGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories Filter Bar
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.5.sp) }
                        )
                    }
                }
            }

            // Recipe List
            items(filteredRecipes, key = { it.id }) { recipe ->
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
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text(recipe.category, fontSize = 9.5.sp) },
                                modifier = Modifier.height(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        CodeBlockView(
                            code = recipe.code,
                            language = "python",
                            title = recipe.title
                        )
                    }
                }
            }
        }
    }
}
