package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ProjectType

@Entity(tableName = "studio_projects")
data class StudioProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val projectType: ProjectType,
    val createdAt: Long = System.currentTimeMillis(),
    val contentText: String, // Script or Lyrics or Storyboard JSON
    val avatarOrGenreId: String,
    val audioDurationSeconds: Int = 10,
    val aspectRatioName: String = "PORTRAIT_9_16",
    val styleThemeName: String = "NEON_PULSE",
    val isFavorite: Boolean = false
)
