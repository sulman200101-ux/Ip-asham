package com.example.data.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "openai_studio_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val BASE_URL = stringPreferencesKey("base_url")
        val DEFAULT_MODEL = stringPreferencesKey("default_model")
        val TEMPERATURE = doublePreferencesKey("temperature")
        val STREAM_ENABLED = booleanPreferencesKey("stream_enabled")
        val ORGANIZATION_ID = stringPreferencesKey("org_id")
        val PROJECT_ID = stringPreferencesKey("project_id")
    }

    val apiKeyFlow: Flow<String> = context.dataStore.data.map { it[API_KEY] ?: "" }
    val baseUrlFlow: Flow<String> = context.dataStore.data.map { it[BASE_URL] ?: "https://api.openai.com/v1/" }
    val defaultModelFlow: Flow<String> = context.dataStore.data.map { it[DEFAULT_MODEL] ?: "gpt-4o" }
    val temperatureFlow: Flow<Double> = context.dataStore.data.map { it[TEMPERATURE] ?: 0.7 }
    val streamEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[STREAM_ENABLED] ?: true }
    val orgIdFlow: Flow<String> = context.dataStore.data.map { it[ORGANIZATION_ID] ?: "" }
    val projectIdFlow: Flow<String> = context.dataStore.data.map { it[PROJECT_ID] ?: "" }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[API_KEY] = key.trim() }
    }

    suspend fun saveBaseUrl(url: String) {
        var formatted = url.trim()
        if (!formatted.endsWith("/")) formatted += "/"
        context.dataStore.edit { it[BASE_URL] = formatted }
    }

    suspend fun saveDefaultModel(model: String) {
        context.dataStore.edit { it[DEFAULT_MODEL] = model }
    }

    suspend fun saveTemperature(temp: Double) {
        context.dataStore.edit { it[TEMPERATURE] = temp }
    }

    suspend fun saveStreamEnabled(enabled: Boolean) {
        context.dataStore.edit { it[STREAM_ENABLED] = enabled }
    }

    suspend fun saveOrgAndProject(org: String, proj: String) {
        context.dataStore.edit {
            it[ORGANIZATION_ID] = org.trim()
            it[PROJECT_ID] = proj.trim()
        }
    }
}
