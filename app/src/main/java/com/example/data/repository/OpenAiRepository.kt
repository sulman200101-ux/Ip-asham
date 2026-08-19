package com.example.data.repository

import android.content.Context
import com.example.data.api.OpenAiApiService
import com.example.data.local.AppDao
import com.example.data.local.AudioRecordEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.ImageRecordEntity
import com.example.data.model.*
import com.example.data.preference.AppPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class OpenAiRepository(
    private val context: Context,
    private val appDao: AppDao,
    private val preferences: AppPreferences
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private suspend fun getApiService(): Pair<OpenAiApiService, String> {
        val apiKey = preferences.apiKeyFlow.first()
        val baseUrl = preferences.baseUrlFlow.first()
        val retrofit = Retrofit.Builder()
            .baseUrl(if (baseUrl.isNotBlank()) baseUrl else "https://api.openai.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        return Pair(retrofit.create(OpenAiApiService::class.java), apiKey)
    }

    // Sessions & Chat Flow
    val allSessions: Flow<List<ChatSessionEntity>> = appDao.getAllSessionsFlow()
    val allImages: Flow<List<ImageRecordEntity>> = appDao.getAllImagesFlow()
    val allAudio: Flow<List<AudioRecordEntity>> = appDao.getAllAudioFlow()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> =
        appDao.getMessagesForSessionFlow(sessionId)

    suspend fun createNewSession(
        title: String = "New Chat",
        model: String = "gpt-4o",
        systemPrompt: String = "You are a helpful, brilliant AI assistant powered by OpenAI.",
        temperature: Double = 0.7
    ): String {
        val id = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            id = id,
            title = title,
            model = model,
            systemPrompt = systemPrompt,
            temperature = temperature,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        appDao.insertSession(session)
        return id
    }

    suspend fun updateSessionSettings(
        sessionId: String,
        title: String? = null,
        model: String? = null,
        systemPrompt: String? = null,
        temperature: Double? = null,
        topP: Double? = null
    ) {
        val existing = appDao.getSessionById(sessionId) ?: return
        val updated = existing.copy(
            title = title ?: existing.title,
            model = model ?: existing.model,
            systemPrompt = systemPrompt ?: existing.systemPrompt,
            temperature = temperature ?: existing.temperature,
            topP = topP ?: existing.topP,
            updatedAt = System.currentTimeMillis()
        )
        appDao.updateSession(updated)
    }

    suspend fun deleteSession(sessionId: String) {
        appDao.deleteMessagesForSession(sessionId)
        appDao.deleteSession(sessionId)
    }

    suspend fun clearAllHistory() {
        appDao.clearAllSessions()
        appDao.clearAllImages()
    }

    /**
     * Send message & stream/fetch completion
     */
    fun sendChatMessage(
        sessionId: String,
        userPrompt: String
    ): Flow<String> = flow {
        val session = appDao.getSessionById(sessionId) ?: return@flow
        val startTime = System.currentTimeMillis()

        // 1. Insert User Message
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            role = "user",
            content = userPrompt,
            timestamp = startTime,
            tokenCount = (userPrompt.length / 4).coerceAtLeast(1)
        )
        appDao.insertMessage(userMsg)

        // Update session title if default
        if (session.title == "New Chat") {
            val shortTitle = if (userPrompt.length > 28) userPrompt.take(28) + "..." else userPrompt
            appDao.updateSession(session.copy(title = shortTitle, updatedAt = System.currentTimeMillis()))
        } else {
            appDao.updateSession(session.copy(updatedAt = System.currentTimeMillis()))
        }

        val (apiService, apiKey) = getApiService()
        val orgId = preferences.orgIdFlow.first().takeIf { it.isNotBlank() }
        val projectId = preferences.projectIdFlow.first().takeIf { it.isNotBlank() }

        // Fetch history
        val history = appDao.getMessagesForSession(sessionId)
        val apiMessages = mutableListOf<ChatApiMessage>()
        if (session.systemPrompt.isNotBlank()) {
            apiMessages.add(ChatApiMessage(role = "system", content = session.systemPrompt))
        }
        for (m in history) {
            apiMessages.add(ChatApiMessage(role = m.role, content = m.content))
        }

        var assistantText = ""

        if (apiKey.isNotBlank()) {
            // Real API Call
            try {
                val request = ChatCompletionRequest(
                    model = session.model,
                    messages = apiMessages,
                    temperature = session.temperature,
                    topP = session.topP,
                    maxTokens = session.maxTokens,
                    stream = false
                )
                val response = apiService.createChatCompletion(
                    authorization = "Bearer $apiKey",
                    orgId = orgId,
                    projectId = projectId,
                    request = request
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    assistantText = body.choices?.firstOrNull()?.message?.content ?: "No response received."
                    emit(assistantText)
                } else {
                    val err = response.errorBody()?.string() ?: response.message()
                    assistantText = "Error (${response.code()}): $err\n\nEnsure your OpenAI API Key is valid in Settings."
                    emit(assistantText)
                }
            } catch (e: Exception) {
                assistantText = "Network request failed: ${e.localizedMessage ?: "Unknown error"}\n\nCheck your connection or API key in Settings."
                emit(assistantText)
            }
        } else {
            // Interactive Developer Playground / Sandbox Simulator
            val simulatedResponse = generateSimulatedResponse(session.model, userPrompt, session.systemPrompt)
            val chunks = simulatedResponse.chunked(12)
            for (chunk in chunks) {
                assistantText += chunk
                emit(assistantText)
                delay(35)
            }
        }

        val latency = System.currentTimeMillis() - startTime
        val assistantMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            role = "assistant",
            content = assistantText,
            timestamp = System.currentTimeMillis(),
            tokenCount = (assistantText.length / 4).coerceAtLeast(1),
            latencyMs = latency
        )
        appDao.insertMessage(assistantMsg)
    }.flowOn(Dispatchers.IO)

    /**
     * DALL-E Image Generation
     */
    suspend fun generateImage(
        prompt: String,
        model: String = "dall-e-3",
        size: String = "1024x1024",
        quality: String = "standard",
        style: String = "vivid"
    ): Result<ImageRecordEntity> = withContext(Dispatchers.IO) {
        val (apiService, apiKey) = getApiService()
        val orgId = preferences.orgIdFlow.first().takeIf { it.isNotBlank() }
        val projectId = preferences.projectIdFlow.first().takeIf { it.isNotBlank() }

        if (apiKey.isNotBlank()) {
            try {
                val request = ImageGenerateRequest(
                    model = model,
                    prompt = prompt,
                    n = 1,
                    size = size,
                    quality = quality,
                    style = style,
                    responseFormat = "url"
                )
                val response = apiService.generateImage(
                    authorization = "Bearer $apiKey",
                    orgId = orgId,
                    projectId = projectId,
                    request = request
                )
                if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
                    val data = response.body()!!.data!!.first()
                    val record = ImageRecordEntity(
                        id = UUID.randomUUID().toString(),
                        prompt = prompt,
                        revisedPrompt = data.revisedPrompt,
                        imageUrl = data.url,
                        model = model,
                        size = size,
                        style = style,
                        createdAt = System.currentTimeMillis()
                    )
                    appDao.insertImage(record)
                    Result.success(record)
                } else {
                    val error = response.errorBody()?.string() ?: response.message()
                    Result.failure(Exception("DALL-E API error ($error)"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Interactive sandbox generated placeholder
            delay(1200)
            val sampleImages = listOf(
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1024&q=80",
                "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?w=1024&q=80",
                "https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?w=1024&q=80",
                "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=1024&q=80"
            )
            val sampleUrl = sampleImages.random()
            val record = ImageRecordEntity(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                revisedPrompt = "A cinematic, ultra-detailed visualization of $prompt rendered with raytraced atmospheric volumetric lighting and vibrant color grading in $style style.",
                imageUrl = sampleUrl,
                model = model,
                size = size,
                style = style,
                createdAt = System.currentTimeMillis()
            )
            appDao.insertImage(record)
            Result.success(record)
        }
    }

    /**
     * Text to Speech (TTS)
     */
    suspend fun generateTts(
        text: String,
        voice: String = "alloy",
        model: String = "tts-1",
        speed: Double = 1.0
    ): Result<AudioRecordEntity> = withContext(Dispatchers.IO) {
        val (apiService, apiKey) = getApiService()
        val orgId = preferences.orgIdFlow.first().takeIf { it.isNotBlank() }

        if (apiKey.isNotBlank()) {
            try {
                val request = AudioSpeechRequest(
                    model = model,
                    input = text,
                    voice = voice,
                    responseFormat = "mp3",
                    speed = speed
                )
                val response = apiService.createSpeech(
                    authorization = "Bearer $apiKey",
                    orgId = orgId,
                    request = request
                )
                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    val audioFile = File(context.cacheDir, "tts_${System.currentTimeMillis()}.mp3")
                    FileOutputStream(audioFile).use { it.write(bytes) }

                    val record = AudioRecordEntity(
                        id = UUID.randomUUID().toString(),
                        type = "TTS",
                        text = text,
                        voiceOrLang = voice,
                        model = model,
                        audioPath = audioFile.absolutePath,
                        durationSec = (text.length / 15.0).coerceAtLeast(1.0),
                        createdAt = System.currentTimeMillis()
                    )
                    appDao.insertAudio(record)
                    Result.success(record)
                } else {
                    Result.failure(Exception("TTS API call failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Interactive sandbox record
            delay(1000)
            val record = AudioRecordEntity(
                id = UUID.randomUUID().toString(),
                type = "TTS (Sandbox)",
                text = text,
                voiceOrLang = voice,
                model = model,
                audioPath = null,
                durationSec = (text.length / 14.0).coerceIn(1.0, 30.0),
                createdAt = System.currentTimeMillis()
            )
            appDao.insertAudio(record)
            Result.success(record)
        }
    }

    /**
     * Speech to Text (Whisper)
     */
    suspend fun transcribeWhisper(
        simulatedText: String = "Transcribing audio input using OpenAI Whisper model..."
    ): Result<AudioRecordEntity> = withContext(Dispatchers.IO) {
        delay(1500)
        val transcript = if (simulatedText.isNotBlank()) simulatedText else "Welcome to OpenAI Studio on Android. openai-python enables seamless speech transcription with whisper-1."
        val record = AudioRecordEntity(
            id = UUID.randomUUID().toString(),
            type = "WHISPER",
            text = transcript,
            voiceOrLang = "en",
            model = "whisper-1",
            durationSec = 4.5,
            createdAt = System.currentTimeMillis()
        )
        appDao.insertAudio(record)
        Result.success(record)
    }

    /**
     * Helper to create rich developer simulation responses when no custom API key is active
     */
    private fun generateSimulatedResponse(model: String, prompt: String, systemPrompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("python") || lower.contains("sdk") || lower.contains("code") || lower.contains("openai") -> {
                """### OpenAI Python SDK Example (`$model`)

Here is how you execute this in **openai-python**:

```python
from openai import OpenAI

# Initialize client
client = OpenAI()

# Create chat completion
response = client.chat.completions.create(
    model="$model",
    messages=[
        {"role": "system", "content": "${systemPrompt.take(60)}..."},
        {"role": "user", "content": "${prompt.take(60)}..."}
    ],
    temperature=0.7,
)

print(response.choices[0].message.content)
```

**Key Features in openai-python 1.x+:**
- Automatic retries with exponential backoff
- Full type hints with Pydantic v2 validation
- Native async client support with `AsyncOpenAI`
- Streaming generator support with `stream=True`"""
            }
            lower.contains("o1") || lower.contains("o3") || model.startsWith("o") -> {
                """Thinking Process:
1. Analyze user inquiry: "$prompt"
2. Synthesize logical steps and constraints
3. Generate structured conclusion

### Solution ($model Reasoning Engine)

To solve this systematically:
- **Core Concept**: Modern OpenAI reasoning models (`o1`, `o3-mini`) allocate dynamic internal thinking tokens before surfacing the final response.
- **Accuracy**: Optimized for competitive programming, advanced mathematical proofs, and complex architectural trade-offs.

*Tip*: Configure your OpenAI API key in Settings (gear icon) to dispatch live requests directly to OpenAI servers."""
            }
            else -> {
                """Hello! I am **$model** running in **OpenAI Studio**.

Your prompt:
> "$prompt"

### Summary & Insights:
1. **Model Capability**: `$model` is loaded and ready for natural language tasks, structured JSON extraction, and reasoning.
2. **Interactive Developer Features**:
   - Tap **"Python Code"** in the top bar to inspect the exact `openai-python` code snippet.
   - Adjust **Temperature**, **Top P**, and **System Prompt** in the parameters drawer.
   - Switch to the **DALL-E** or **Voice** tabs to explore image and audio generation.

*You can add your own OpenAI API Key in the Settings tab anytime to execute live cloud completions!*"""
            }
        }
    }
}
