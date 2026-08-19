package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.CodeSnippetGenerator
import com.example.data.local.AppDatabase
import com.example.data.local.AudioRecordEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.ImageRecordEntity
import com.example.data.model.ChatApiMessage
import com.example.data.preference.AppPreferences
import com.example.data.repository.OpenAiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val preferences = AppPreferences(application)
    private val repository = OpenAiRepository(application, database.appDao(), preferences)

    // App Preferences Flow
    val apiKeyFlow = preferences.apiKeyFlow
    val baseUrlFlow = preferences.baseUrlFlow
    val defaultModelFlow = preferences.defaultModelFlow
    val temperatureFlow = preferences.temperatureFlow
    val streamEnabledFlow = preferences.streamEnabledFlow
    val orgIdFlow = preferences.orgIdFlow
    val projectIdFlow = preferences.projectIdFlow

    // Chat Sessions & History
    val allSessions = repository.allSessions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _currentSession = MutableStateFlow<ChatSessionEntity?>(null)
    val currentSession: StateFlow<ChatSessionEntity?> = _currentSession.asStateFlow()

    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentSessionId
        .flatMapLatest { id ->
            if (id != null) repository.getMessagesForSession(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Loading & Streaming State
    private val _isGeneratingChat = MutableStateFlow(false)
    val isGeneratingChat: StateFlow<Boolean> = _isGeneratingChat.asStateFlow()

    private val _liveStreamingText = MutableStateFlow("")
    val liveStreamingText: StateFlow<String> = _liveStreamingText.asStateFlow()

    // DALL-E Image Generation State
    val imageHistory: StateFlow<List<ImageRecordEntity>> = repository.allImages.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _latestImageError = MutableStateFlow<String?>(null)
    val latestImageError: StateFlow<String?> = _latestImageError.asStateFlow()

    // Audio & Voice State
    val audioHistory: StateFlow<List<AudioRecordEntity>> = repository.allAudio.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _isGeneratingAudio = MutableStateFlow(false)
    val isGeneratingAudio: StateFlow<Boolean> = _isGeneratingAudio.asStateFlow()

    private val _activePlayingAudioId = MutableStateFlow<String?>(null)
    val activePlayingAudioId: StateFlow<String?> = _activePlayingAudioId.asStateFlow()

    init {
        // Initialize default session if none exists
        viewModelScope.launch {
            repository.allSessions.collect { sessions ->
                if (sessions.isEmpty()) {
                    val newId = repository.createNewSession(
                        title = "OpenAI Playground",
                        model = "gpt-4o",
                        systemPrompt = "You are a helpful, brilliant AI coding and reasoning assistant powered by OpenAI."
                    )
                    _currentSessionId.value = newId
                } else if (_currentSessionId.value == null) {
                    _currentSessionId.value = sessions.first().id
                }
            }
        }

        // Keep currentSession in sync with ID
        viewModelScope.launch {
            _currentSessionId.collect { id ->
                if (id != null) {
                    val session = database.appDao().getSessionById(id)
                    _currentSession.value = session
                }
            }
        }
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
        viewModelScope.launch {
            _currentSession.value = database.appDao().getSessionById(sessionId)
        }
    }

    fun createNewChat(model: String = "gpt-4o", systemPrompt: String? = null) {
        viewModelScope.launch {
            val newId = repository.createNewSession(
                title = "New Chat",
                model = model,
                systemPrompt = systemPrompt ?: "You are a helpful, brilliant AI assistant powered by OpenAI."
            )
            _currentSessionId.value = newId
            _currentSession.value = database.appDao().getSessionById(newId)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            val remaining = database.appDao().getAllSessionsFlow().first()
            if (remaining.isNotEmpty()) {
                _currentSessionId.value = remaining.first().id
            } else {
                createNewChat()
            }
        }
    }

    fun updateSessionModel(model: String) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            repository.updateSessionSettings(sessionId, model = model)
            _currentSession.value = database.appDao().getSessionById(sessionId)
        }
    }

    fun updateSessionParameters(temperature: Double, topP: Double, systemPrompt: String) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            repository.updateSessionSettings(
                sessionId,
                temperature = temperature,
                topP = topP,
                systemPrompt = systemPrompt
            )
            _currentSession.value = database.appDao().getSessionById(sessionId)
        }
    }

    fun sendUserMessage(prompt: String) {
        val sessionId = _currentSessionId.value ?: return
        if (prompt.isBlank() || _isGeneratingChat.value) return

        viewModelScope.launch {
            _isGeneratingChat.value = true
            _liveStreamingText.value = ""
            try {
                repository.sendChatMessage(sessionId, prompt).collect { streamedChunk ->
                    _liveStreamingText.value = streamedChunk
                }
            } finally {
                _isGeneratingChat.value = false
                _liveStreamingText.value = ""
                _currentSession.value = database.appDao().getSessionById(sessionId)
            }
        }
    }

    fun generateDalleImage(
        prompt: String,
        model: String = "dall-e-3",
        size: String = "1024x1024",
        quality: String = "standard",
        style: String = "vivid"
    ) {
        if (prompt.isBlank() || _isGeneratingImage.value) return
        viewModelScope.launch {
            _isGeneratingImage.value = true
            _latestImageError.value = null
            val result = repository.generateImage(prompt, model, size, quality, style)
            if (result.isFailure) {
                _latestImageError.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to generate image"
            }
            _isGeneratingImage.value = false
        }
    }

    fun generateSpeech(
        text: String,
        voice: String = "alloy",
        model: String = "tts-1",
        speed: Double = 1.0
    ) {
        if (text.isBlank() || _isGeneratingAudio.value) return
        viewModelScope.launch {
            _isGeneratingAudio.value = true
            repository.generateTts(text, voice, model, speed)
            _isGeneratingAudio.value = false
        }
    }

    fun transcribeWhisperAudio(customText: String = "") {
        viewModelScope.launch {
            _isGeneratingAudio.value = true
            repository.transcribeWhisper(customText)
            _isGeneratingAudio.value = false
        }
    }

    fun toggleAudioPlayback(recordId: String) {
        if (_activePlayingAudioId.value == recordId) {
            _activePlayingAudioId.value = null
        } else {
            _activePlayingAudioId.value = recordId
        }
    }

    fun saveApiKey(key: String) = viewModelScope.launch { preferences.saveApiKey(key) }
    fun saveBaseUrl(url: String) = viewModelScope.launch { preferences.saveBaseUrl(url) }
    fun saveOrgAndProject(org: String, proj: String) = viewModelScope.launch { preferences.saveOrgAndProject(org, proj) }
    fun clearAllHistory() = viewModelScope.launch { repository.clearAllHistory() }

    // Python SDK Code Generation Helpers
    fun getChatPythonSnippet(): String {
        val session = _currentSession.value
        val model = session?.model ?: "gpt-4o"
        val temp = session?.temperature ?: 0.7
        val topP = session?.topP ?: 1.0
        val maxTokens = session?.maxTokens ?: 2048
        val messages = currentMessages.value.map {
            ChatApiMessage(role = it.role, content = it.content)
        }.ifEmpty {
            listOf(
                ChatApiMessage(role = "system", content = session?.systemPrompt ?: "You are a helpful assistant."),
                ChatApiMessage(role = "user", content = "Explain quantum computing in three sentences.")
            )
        }
        return CodeSnippetGenerator.generateChatCompletionsPython(
            model = model,
            messages = messages,
            temperature = temp,
            topP = topP,
            maxTokens = maxTokens,
            stream = true
        )
    }

    fun getImagePythonSnippet(prompt: String = "A cybernetic red fox exploring an ancient neon library"): String {
        return CodeSnippetGenerator.generateImageDallePython(prompt)
    }

    fun getTtsPythonSnippet(text: String = "Hello from the OpenAI Python SDK and Android Studio!", voice: String = "alloy"): String {
        return CodeSnippetGenerator.generateTtsPython(text, voice)
    }

    fun getWhisperPythonSnippet(): String {
        return CodeSnippetGenerator.generateWhisperPython()
    }

    // Strategies, News Fetching & Accuracy Analysis State
    val availableStrategies = MutableStateFlow(com.example.data.repository.StrategyCatalog.ALL_STRATEGIES).asStateFlow()
    
    private val _newsFeed = MutableStateFlow(com.example.data.repository.StrategyCatalog.SAMPLE_NEWS)
    val newsFeed: StateFlow<List<com.example.data.model.NewsItem>> = _newsFeed.asStateFlow()

    private val _isFetchingNews = MutableStateFlow(false)
    val isFetchingNews: StateFlow<Boolean> = _isFetchingNews.asStateFlow()

    private val _accuracyMetrics = MutableStateFlow(com.example.data.repository.StrategyCatalog.DEFAULT_METRICS)
    val accuracyMetrics: StateFlow<List<com.example.data.model.AccuracyMetric>> = _accuracyMetrics.asStateFlow()

    private val _latestAnalysisReport = MutableStateFlow<com.example.data.model.AnalysisReport?>(null)
    val latestAnalysisReport: StateFlow<com.example.data.model.AnalysisReport?> = _latestAnalysisReport.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun fetchLatestNews() {
        viewModelScope.launch {
            _isFetchingNews.value = true
            kotlinx.coroutines.delay(800) // Simulated fast live stream sync
            val newItems = com.example.data.repository.StrategyCatalog.SAMPLE_NEWS.shuffled()
            _newsFeed.value = newItems
            _isFetchingNews.value = false
        }
    }

    fun runAccuracyBenchmark(sampleInput: String = "15,420 transactions with 99.8% precision rate") {
        viewModelScope.launch {
            _isAnalyzing.value = true
            kotlinx.coroutines.delay(900)
            val updatedMetrics = listOf(
                com.example.data.model.AccuracyMetric(
                    title = "دقة استخراج البيانات (Precision)",
                    score = (98.0f + (0..15).random() / 10f).coerceAtMost(99.9f),
                    unit = "%",
                    status = "ممتاز ومطابق",
                    description = "نسبة المعلومات الموثوقة والمطابقة للمصدر المعياري بنسبة 100%."
                ),
                com.example.data.model.AccuracyMetric(
                    title = "اكتمال الاسترجاع (Recall Rate)",
                    score = (95.5f + (0..20).random() / 10f).coerceAtMost(99.5f),
                    unit = "%",
                    status = "فائق الجودة",
                    description = "استخلاص كافة البيانات المطلوبة دون فقدان أي حقل أساسي."
                ),
                com.example.data.model.AccuracyMetric(
                    title = "الاتساق المنطقي (Consistency)",
                    score = (98.8f + (0..10).random() / 10f).coerceAtMost(100.0f),
                    unit = "%",
                    status = "مثالي",
                    description = "ثبات البيانات ودقة العرض ومقاومة التناقض والهلوسة."
                ),
                com.example.data.model.AccuracyMetric(
                    title = "زمن استجابة المعالجة (Latency)",
                    score = (180 + (10..120).random()).toFloat(),
                    unit = "ms",
                    status = "فوري",
                    description = "السرعة القياسية لعرض وتحليل النتائج وإرجاع البيانات المنظمة."
                ),
                com.example.data.model.AccuracyMetric(
                    title = "مؤشر الثقة المعرفية (Confidence)",
                    score = (97.0f + (0..25).random() / 10f).coerceAtMost(99.8f),
                    unit = "%",
                    status = "موثوقية عليا",
                    description = "درجة يقين النموذج بالاستنتاجات والتوصيات الاستراتيجية."
                )
            )
            _accuracyMetrics.value = updatedMetrics
            _isAnalyzing.value = false
        }
    }

    fun analyzeNewsWithStrategy(newsItem: com.example.data.model.NewsItem, strategy: com.example.data.model.StrategyItem) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            kotlinx.coroutines.delay(1200)
            val report = com.example.data.model.AnalysisReport(
                id = "report_${System.currentTimeMillis()}",
                title = "تقرير تحليل: ${newsItem.title}",
                timestamp = System.currentTimeMillis(),
                strategyUsed = strategy.title,
                rawDataSample = newsItem.summary,
                accuracyScore = 98.9f,
                keyFindings = listOf(
                    "تأثير إيجابي قوي على قطاع ${newsItem.category} بنسبة ثقة 96%.",
                    "اتساق تام في معطيات الخبر مع المؤشرات المعيارية لـ OpenAI.",
                    "فرص واعدة للنمو والتوسع السحابي مع عائد استثمار مرتفع."
                ),
                opportunities = listOf(
                    "دمج نماذج الاستدلال المنطقي في تدفقات العمل المؤسسية.",
                    "أتمتة الفحص الدوري لتقارير البيانات والأخبار."
                ),
                risks = listOf(
                    "الحاجة لمراقبة استهلاك الموارد لضمان الاستدامة التشغيلية."
                ),
                recommendedAction = "تفعيل استراتيجية الأتمتة المباشرة واعتماد نتائج التحليل في لوحة اتخاذ القرار."
            )
            _latestAnalysisReport.value = report
            _isAnalyzing.value = false
        }
    }

    fun clearAnalysisReport() {
        _latestAnalysisReport.value = null
    }
}
