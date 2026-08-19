package com.example.data.model

data class StrategyItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconType: String,
    val promptTemplate: String,
    val sampleInput: String,
    val metricsToTrack: List<String>
)

data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String,
    val summary: String,
    val content: String,
    val sentiment: String,
    val impactScore: Int,
    val tags: List<String>
)

data class AccuracyMetric(
    val title: String,
    val score: Float,
    val unit: String,
    val status: String,
    val description: String
)

data class AnalysisReport(
    val id: String,
    val title: String,
    val timestamp: Long,
    val strategyUsed: String,
    val rawDataSample: String,
    val accuracyScore: Float,
    val keyFindings: List<String>,
    val opportunities: List<String>,
    val risks: List<String>,
    val recommendedAction: String
)
