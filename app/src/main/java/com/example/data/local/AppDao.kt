package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Chat Sessions
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessionsFlow(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSessionFlow(sessionId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    // Image Records
    @Query("SELECT * FROM image_records ORDER BY createdAt DESC")
    fun getAllImagesFlow(): Flow<List<ImageRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageRecordEntity)

    @Query("DELETE FROM image_records WHERE id = :id")
    suspend fun deleteImage(id: String)

    @Query("DELETE FROM image_records")
    suspend fun clearAllImages()

    // Audio Records
    @Query("SELECT * FROM audio_records ORDER BY createdAt DESC")
    fun getAllAudioFlow(): Flow<List<AudioRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: AudioRecordEntity)

    @Query("DELETE FROM audio_records WHERE id = :id")
    suspend fun deleteAudio(id: String)
}
