package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "البطل الصغير",
    val totalStars: Int = 10,
    val highestUnlockedLevel: Int = 1,
    val unlockedThemes: String = "classic,candy",
    val selectedTheme: String = "classic",
    val isSoundEnabled: Boolean = true,
    val creationsCount: Int = 0,
    val physicsTowerBestHeight: Int = 0
)

@Entity(tableName = "level_records")
data class LevelRecordEntity(
    @PrimaryKey val levelId: Int,
    val starsEarned: Int,
    val completionTimeSeconds: Int,
    val isCompleted: Boolean = true
)

@Entity(tableName = "saved_creations")
data class SavedCreationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val blocksJson: String,
    val blocksCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0
)
