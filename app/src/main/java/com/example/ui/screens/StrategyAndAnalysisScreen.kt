package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccuracyMetric
import com.example.data.model.AnalysisReport
import com.example.data.model.NewsItem
import com.example.data.model.StrategyItem
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.OpenAIGreen
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyAndAnalysisScreen(
    viewModel: StudioViewModel,
    onNavigateToPlayground: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strategies by viewModel.availableStrategies.collectAsState()
    val newsFeed by viewModel.newsFeed.collectAsState()
    val isFetchingNews by viewModel.isFetchingNews.collectAsState()
    val accuracyMetrics by viewModel.accuracyMetrics.collectAsState()
    val latestReport by viewModel.latestAnalysisReport.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: الاستراتيجيات, 1: جلب الأخبار, 2: اختبار دقة البيانات, 3: تقرير التحليل
    var selectedStrategyForRun by remember { mutableStateOf<StrategyItem?>(strategies.firstOrNull()) }
    var selectedNewsItem by remember { mutableStateOf<NewsItem?>(null) }
    var testDataInput by remember { mutableStateOf("15,420 معاملة بنسبة خطأ 0.02% وزمن استجابة 120ms مع 99.8% دقة مطابقة.") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "مركز الاستراتيجيات ودقة البيانات",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "جلب الأخبار الفورية • اختبار الدقة • تحليل وتوليف النتائج",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.fetchLatestNews()
                        viewModel.runAccuracyBenchmark(testDataInput)
                        Toast.makeText(context, "تم تحديث الأخبار واختبار الدقة بنجاح!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث البيانات",
                            tint = OpenAIGreen
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
            // Tabs Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OpenAIGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("الاستراتيجيات (${strategies.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.AutoGraph, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("جلب الأخبار (${newsFeed.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("اختبار دقة العرض", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("تحليل النتائج", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Progress banner if loading
            if (isFetchingNews || isAnalyzing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = OpenAIGreen,
                    trackColor = OpenAIGreen.copy(alpha = 0.2f)
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> StrategiesTabContent(
                    strategies = strategies,
                    onExecuteInPlayground = { strategy ->
                        onNavigateToPlayground("استراتيجية: ${strategy.title}\nالمعطيات: ${strategy.sampleInput}\nالطلب: قم بتحليل البيانات واستخراج التوصيات ومؤشرات الأداء.")
                    },
                    onSelectForNewsAnalysis = { strategy ->
                        selectedStrategyForRun = strategy
                        selectedTab = 1
                        Toast.makeText(context, "تم تحديد: ${strategy.title}، اختر خبراً لتحليله الآن", Toast.LENGTH_LONG).show()
                    }
                )
                1 -> NewsFeedTabContent(
                    newsList = newsFeed,
                    isFetching = isFetchingNews,
                    selectedStrategy = selectedStrategyForRun ?: strategies.first(),
                    onRefresh = { viewModel.fetchLatestNews() },
                    onAnalyzeNews = { newsItem ->
                        val strat = selectedStrategyForRun ?: strategies.first()
                        viewModel.analyzeNewsWithStrategy(newsItem, strat)
                        selectedNewsItem = newsItem
                        selectedTab = 3
                        Toast.makeText(context, "جاري تحليل الخبر وفق استراتيجية: ${strat.title}...", Toast.LENGTH_SHORT).show()
                    }
                )
                2 -> AccuracyBenchmarkTabContent(
                    metrics = accuracyMetrics,
                    testInput = testDataInput,
                    onTestInputChange = { testDataInput = it },
                    onRunTest = {
                        viewModel.runAccuracyBenchmark(testDataInput)
                        Toast.makeText(context, "تم تنفيذ اختبار دقة عرض البيانات!", Toast.LENGTH_SHORT).show()
                    }
                )
                3 -> AnalysisReportTabContent(
                    report = latestReport,
                    isAnalyzing = isAnalyzing,
                    onRunNewAnalysis = {
                        val strat = selectedStrategyForRun ?: strategies.first()
                        val news = newsFeed.firstOrNull() ?: com.example.data.repository.StrategyCatalog.SAMPLE_NEWS.first()
                        viewModel.analyzeNewsWithStrategy(news, strat)
                    },
                    onShareReport = { report ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val summaryText = "${report.title}\nالاستراتيجية: ${report.strategyUsed}\nالدقة: ${report.accuracyScore}%\nأبرز النتائج:\n${report.keyFindings.joinToString("\n• ")}"
                        clipboard.setPrimaryClip(ClipData.newPlainText("Analysis Report", summaryText))
                        Toast.makeText(context, "تم نسخ ملخص تقرير التحليل!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun StrategiesTabContent(
    strategies: List<StrategyItem>,
    onExecuteInPlayground: (StrategyItem) -> Unit,
    onSelectForNewsAnalysis: (StrategyItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(OpenAIGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = OpenAIGreen)
                    }
                    Column {
                        Text(
                            text = "كتالوج الاستراتيجيات الذكية المتكاملة",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "استراتيجيات متخصصة جاهزة لتحليل الأسواق، تدقيق جودة البيانات، استخراج الرؤى، وتوقع الأنماط.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(strategies, key = { it.id }) { strategy ->
            StrategyCardItem(
                strategy = strategy,
                onExecuteInPlayground = { onExecuteInPlayground(strategy) },
                onSelectForNewsAnalysis = { onSelectForNewsAnalysis(strategy) }
            )
        }
    }
}

@Composable
fun StrategyCardItem(
    strategy: StrategyItem,
    onExecuteInPlayground: () -> Unit,
    onSelectForNewsAnalysis: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (strategy.iconType) {
                                "trending" -> Icons.Default.TrendingUp
                                "check" -> Icons.Default.CheckCircle
                                "cpu" -> Icons.Default.Memory
                                "security" -> Icons.Default.Shield
                                else -> Icons.Default.Dashboard
                            },
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strategy.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text(strategy.category, fontSize = 10.sp) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strategy.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics tags
            Text(
                text = "مؤشرات المتابعة والدقة:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                strategy.metricsToTrack.forEach { metric ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "• $metric",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSelectForNewsAnalysis,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحليل الأخبار بهذه الاستراتيجية", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onExecuteInPlayground,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OpenAIGreen, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تشغيل في المحادثة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NewsFeedTabContent(
    newsList: List<NewsItem>,
    isFetching: Boolean,
    selectedStrategy: StrategyItem,
    onRefresh: () -> Unit,
    onAnalyzeNews: (NewsItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = OpenAIGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الاستراتيجية النشطة للتحليل:",
                            style = MaterialTheme.typography.labelSmall,
                            color = OpenAIGreen
                        )
                        Text(
                            text = selectedStrategy.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = "جلب أحدث الأخبار", tint = OpenAIGreen)
                    }
                }
            }
        }

        items(newsList, key = { it.id }) { news ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = news.source,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "• ${news.timeAgo}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = OpenAIGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "مؤشر التأثير: ${news.impactScore}/100",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = OpenAIGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المعنويات: ${news.sentiment}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = { onAnalyzeNews(news) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OpenAIGreen, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تحليل وتوليف النتائج", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccuracyBenchmarkTabContent(
    metrics: List<AccuracyMetric>,
    testInput: String,
    onTestInputChange: (String) -> Unit,
    onRunTest: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "محاكي فحص واختبار دقة عرض البيانات (Data Quality Engine)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اختبار معياري حي لفحص التطابق، الاتساق الإحصائي، ومقاومة الهلوسة في التقارير المستخرجة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = testInput,
                        onValueChange = onTestInputChange,
                        label = { Text("بيانات العينة للاختبار المعياري") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRunTest,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OpenAIGreen, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تشغيل اختبار الدقة المعياري الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "مؤشرات الدقة وجودة عرض البيانات الحالية",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(metrics) { metric ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = metric.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = metric.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${metric.score} ${metric.unit}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = OpenAIGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = OpenAIGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = metric.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = OpenAIGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressValue = if (metric.unit == "%") (metric.score / 100f).coerceIn(0f, 1f) else 0.95f
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = OpenAIGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AnalysisReportTabContent(
    report: AnalysisReport?,
    isAnalyzing: Boolean,
    onRunNewAnalysis: () -> Unit,
    onShareReport: (AnalysisReport) -> Unit
) {
    if (isAnalyzing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = OpenAIGreen, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "جاري تحليل الخبر وتوليف المؤشرات والنتائج الاستراتيجية...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "تطبيق فحص الدقة والتطابق المعياري مع محرك OpenAI",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "لا يوجد تقرير تحليل نتائج حتى الآن",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "اختر خبراً من تبويب 'جلب الأخبار' وانقر على 'تحليل وتوليف النتائج' لبدء التوليف الفوري.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRunNewAnalysis,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OpenAIGreen, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تشغيل تحليل تجريبي فوري", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OpenAIGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "معدل الدقة المعيارية: ${report.accuracyScore}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OpenAIGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(onClick = { onShareReport(report) }) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ التقرير", tint = OpenAIGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = report.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "الاستراتيجية المطبقة: ${report.strategyUsed}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Key Findings
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = OpenAIGreen)
                            Text(
                                text = "أبرز النتائج والاستنتاجات:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        report.keyFindings.forEach { finding ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = OpenAIGreen, fontWeight = FontWeight.Bold)
                                Text(finding, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Opportunities & Risks
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "الفرص والمخاطر الاستراتيجية:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        report.opportunities.forEach { opp ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("▲", color = OpenAIGreen, fontSize = 10.sp)
                                Text(opp, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        report.risks.forEach { risk ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("▼", color = MaterialTheme.colorScheme.error, fontSize = 10.sp)
                                Text(risk, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Action item
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OpenAIGreen.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "التوصية التنفيذية النهائية:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = OpenAIGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.recommendedAction,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}
