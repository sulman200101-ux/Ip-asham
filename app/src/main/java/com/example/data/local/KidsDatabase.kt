package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlayerProfileEntity::class,
        LevelRecordEntity::class,
        SavedCreationEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KidsDatabase : RoomDatabase() {

    abstract fun kidsDao(): KidsDao

    companion object {
        @Volatile
        private var INSTANCE: KidsDatabase? = null

        fun getDatabase(context: Context): KidsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KidsDatabase::class.java,
                    "smart_kids_builder_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
