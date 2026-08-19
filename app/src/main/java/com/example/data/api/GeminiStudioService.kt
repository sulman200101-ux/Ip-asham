package com.example.data.api

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float = 0.7f,
    @Json(name = "topP") val topP: Float = 0.95f,
    @Json(name = "topK") val topK: Int = 40
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = GeminiGenerationConfig()
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val PREFS_NAME = "ai_studio_prefs"
    private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"
    private const val KEY_SELECTED_MODEL = "selected_gemini_model"
    private const val KEY_TEMPERATURE = "ai_temperature"

    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"

    private var sharedPrefs: SharedPreferences? = null

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getApiKey(): String {
        val customKey = sharedPrefs?.getString(KEY_CUSTOM_API_KEY, "")?.trim() ?: ""
        if (customKey.isNotBlank()) return customKey
        return try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (e: Exception) {
            ""
        }
    }

    fun saveApiKey(key: String) {
        sharedPrefs?.edit()?.putString(KEY_CUSTOM_API_KEY, key.trim())?.apply()
    }

    fun getSelectedModel(): String {
        return sharedPrefs?.getString(KEY_SELECTED_MODEL, MODEL_FLASH) ?: MODEL_FLASH
    }

    fun saveSelectedModel(model: String) {
        sharedPrefs?.edit()?.putString(KEY_SELECTED_MODEL, model)?.apply()
    }

    fun getTemperature(): Float {
        return sharedPrefs?.getFloat(KEY_TEMPERATURE, 0.7f) ?: 0.7f
    }

    fun saveTemperature(temp: Float) {
        sharedPrefs?.edit()?.putFloat(KEY_TEMPERATURE, temp)?.apply()
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun generate(prompt: String, modelOverride: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("يرجى إدخال مفتاح Gemini API Key في إعدادات التطبيق أو ملف .env للتوليد بالذكاء الاصطناعي."))
        }

        val model = modelOverride ?: getSelectedModel()
        val temp = getTemperature()

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = temp)
            )
            val response = api.generateContent(model, apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("لم يتم استلام نص من الذكاء الاصطناعي"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(apiKeyToTest: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        val key = apiKeyToTest?.takeIf { it.isNotBlank() } ?: getApiKey()
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("المفتاح فارغ! يرجى إدخال مفتاح صالح."))
        }

        val startTime = System.currentTimeMillis()
        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = "Respond with: OK"))
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.1f)
            )
            val response = api.generateContent(MODEL_FLASH, key, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            val duration = System.currentTimeMillis() - startTime
            if (!text.isNullOrBlank()) {
                Result.success(duration)
            } else {
                Result.failure(Exception("استجابة غير مكتملة من خادم الذكاء الاصطناعي"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
