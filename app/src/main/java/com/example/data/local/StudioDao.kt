package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProjectType
import kotlinx.coroutines.flow.Flow

@Dao
interface StudioDao {
    @Query("SELECT * FROM studio_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<StudioProjectEntity>>

    @Query("SELECT * FROM studio_projects WHERE projectType = :type ORDER BY createdAt DESC")
    fun getProjectsByType(type: ProjectType): Flow<List<StudioProjectEntity>>

    @Query("SELECT * FROM studio_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): StudioProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: StudioProjectEntity): Long

    @Update
    suspend fun updateProject(project: StudioProjectEntity)

    @Delete
    suspend fun deleteProject(project: StudioProjectEntity)

    @Query("DELETE FROM studio_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}
