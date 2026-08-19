package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val model: String = "gpt-4o",
    val systemPrompt: String = "You are a helpful, brilliant AI assistant powered by OpenAI.",
    val temperature: Double = 0.7,
    val topP: Double = 1.0,
    val maxTokens: Int = 2048,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tokenCount: Int = 0,
    val latencyMs: Long = 0,
    val isError: Boolean = false
)

@Entity(tableName = "image_records")
data class ImageRecordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val revisedPrompt: String? = null,
    val imageUrl: String? = null,
    val localUri: String? = null,
    val model: String = "dall-e-3",
    val size: String = "1024x1024",
    val style: String = "vivid",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audio_records")
data class AudioRecordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "TTS" or "WHISPER"
    val text: String,
    val voiceOrLang: String,
    val model: String,
    val audioPath: String? = null,
    val durationSec: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
