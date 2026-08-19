package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<ChatApiMessage>,
    @Json(name = "temperature") val temperature: Double? = null,
    @Json(name = "top_p") val topP: Double? = null,
    @Json(name = "max_tokens") val maxTokens: Int? = null,
    @Json(name = "stream") val stream: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class ChatApiMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "object") val objectType: String? = null,
    @Json(name = "created") val created: Long? = null,
    @Json(name = "model") val model: String? = null,
    @Json(name = "choices") val choices: List<ChatChoice>? = null,
    @Json(name = "usage") val usage: TokenUsage? = null
)

@JsonClass(generateAdapter = true)
data class ChatChoice(
    @Json(name = "index") val index: Int? = null,
    @Json(name = "message") val message: ChatApiMessage? = null,
    @Json(name = "finish_reason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class TokenUsage(
    @Json(name = "prompt_tokens") val promptTokens: Int? = 0,
    @Json(name = "completion_tokens") val completionTokens: Int? = 0,
    @Json(name = "total_tokens") val totalTokens: Int? = 0
)

@JsonClass(generateAdapter = true)
data class ImageGenerateRequest(
    @Json(name = "model") val model: String = "dall-e-3",
    @Json(name = "prompt") val prompt: String,
    @Json(name = "n") val n: Int = 1,
    @Json(name = "size") val size: String = "1024x1024",
    @Json(name = "quality") val quality: String = "standard",
    @Json(name = "style") val style: String = "vivid",
    @Json(name = "response_format") val responseFormat: String = "url"
)

@JsonClass(generateAdapter = true)
data class ImageGenerateResponse(
    @Json(name = "created") val created: Long? = null,
    @Json(name = "data") val data: List<ImageData>? = null
)

@JsonClass(generateAdapter = true)
data class ImageData(
    @Json(name = "url") val url: String? = null,
    @Json(name = "b64_json") val b64Json: String? = null,
    @Json(name = "revised_prompt") val revisedPrompt: String? = null
)

@JsonClass(generateAdapter = true)
data class AudioSpeechRequest(
    @Json(name = "model") val model: String = "tts-1",
    @Json(name = "input") val input: String,
    @Json(name = "voice") val voice: String = "alloy",
    @Json(name = "response_format") val responseFormat: String = "mp3",
    @Json(name = "speed") val speed: Double = 1.0
)

@JsonClass(generateAdapter = true)
data class ModelsResponse(
    @Json(name = "data") val data: List<RemoteModelItem>? = null
)

@JsonClass(generateAdapter = true)
data class RemoteModelItem(
    @Json(name = "id") val id: String,
    @Json(name = "created") val created: Long? = null,
    @Json(name = "owned_by") val ownedBy: String? = null
)
