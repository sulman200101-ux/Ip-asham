package com.example.data.api

import com.example.data.model.AudioSpeechRequest
import com.example.data.model.ChatCompletionRequest
import com.example.data.model.ChatCompletionResponse
import com.example.data.model.ImageGenerateRequest
import com.example.data.model.ImageGenerateResponse
import com.example.data.model.ModelsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface OpenAiApiService {

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Organization") orgId: String? = null,
        @Header("OpenAI-Project") projectId: String? = null,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>

    @Streaming
    @POST("chat/completions")
    suspend fun createChatCompletionStream(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Organization") orgId: String? = null,
        @Header("OpenAI-Project") projectId: String? = null,
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>

    @POST("images/generations")
    suspend fun generateImage(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Organization") orgId: String? = null,
        @Header("OpenAI-Project") projectId: String? = null,
        @Body request: ImageGenerateRequest
    ): Response<ImageGenerateResponse>

    @POST("audio/speech")
    suspend fun createSpeech(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Organization") orgId: String? = null,
        @Header("OpenAI-Project") projectId: String? = null,
        @Body request: AudioSpeechRequest
    ): Response<ResponseBody>

    @Multipart
    @POST("audio/transcriptions")
    suspend fun createTranscription(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody? = null
    ): Response<ResponseBody>

    @GET("models")
    suspend fun listModels(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Organization") orgId: String? = null,
        @Header("OpenAI-Project") projectId: String? = null
    ): Response<ModelsResponse>
}
