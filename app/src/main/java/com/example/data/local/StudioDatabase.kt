package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ProjectType

class ProjectTypeConverters {
    @TypeConverter
    fun fromProjectType(type: ProjectType): String = type.name

    @TypeConverter
    fun toProjectType(value: String): ProjectType = try {
        ProjectType.valueOf(value)
    } catch (e: Exception) {
        ProjectType.VOICE_OVER
    }
}

@Database(entities = [StudioProjectEntity::class], version = 1, exportSchema = false)
@TypeConverters(ProjectTypeConverters::class)
abstract class StudioDatabase : RoomDatabase() {
    abstract fun studioDao(): StudioDao

    companion object {
        @Volatile
        private var INSTANCE: StudioDatabase? = null

        fun getDatabase(context: Context): StudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudioDatabase::class.java,
                    "aura_studio_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
