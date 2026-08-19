package com.example.data.repository

import com.example.data.local.AchievementEntity
import com.example.data.local.KidsDao
import com.example.data.local.LevelRecordEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.SavedCreationEntity
import com.example.data.model.BlockThemeSkin
import com.example.data.model.KidBadge
import com.example.data.model.PlacedBlock
import com.example.data.sound.SoundSynthesizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class KidsGameRepository(private val dao: KidsDao) {

    val profileFlow: Flow<PlayerProfileEntity> = dao.getPlayerProfile().map {
        it ?: PlayerProfileEntity()
    }

    val levelRecordsFlow: Flow<List<LevelRecordEntity>> = dao.getAllLevelRecords()
    val savedCreationsFlow: Flow<List<SavedCreationEntity>> = dao.getAllSavedCreations()
    val achievementsFlow: Flow<List<AchievementEntity>> = dao.getAllAchievements()

    suspend fun initializeIfEmpty() {
        val current = dao.getPlayerProfile().firstOrNull()
        if (current == null) {
            dao.insertOrUpdateProfile(PlayerProfileEntity())
        }
    }

    suspend fun completeLevel(levelId: Int, starsEarned: Int, timeSeconds: Int) {
        val currentProfile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        val existingRecord = dao.getAllLevelRecords().firstOrNull()?.find { it.levelId == levelId }

        val previousStars = existingRecord?.starsEarned ?: 0
        val addedStars = (starsEarned - previousStars).coerceAtLeast(0)

        val newTotalStars = currentProfile.totalStars + addedStars
        val newHighestLevel = (levelId + 1).coerceAtLeast(currentProfile.highestUnlockedLevel)

        dao.insertLevelRecord(
            LevelRecordEntity(
                levelId = levelId,
                starsEarned = maxOf(previousStars, starsEarned),
                completionTimeSeconds = if (existingRecord == null) timeSeconds else minOf(existingRecord.completionTimeSeconds, timeSeconds)
            )
        )

        dao.insertOrUpdateProfile(
            currentProfile.copy(
                totalStars = newTotalStars,
                highestUnlockedLevel = newHighestLevel
            )
        )

        checkAndUnlockAchievements(newTotalStars, newHighestLevel, currentProfile.creationsCount)
    }

    suspend fun saveCreation(title: String, blocks: List<PlacedBlock>): Long {
        val jsonArray = JSONArray()
        for (b in blocks) {
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("shape", b.shapeType.name)
            obj.put("color", b.color.id)
            obj.put("gx", b.gridX)
            obj.put("gy", b.gridY)
            obj.put("rot", b.rotation)
            jsonArray.put(obj)
        }

        val creation = SavedCreationEntity(
            title = title,
            blocksJson = jsonArray.toString(),
            blocksCount = blocks.size
        )
        val id = dao.insertCreation(creation)

        val currentProfile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        val newCount = currentProfile.creationsCount + 1
        dao.insertOrUpdateProfile(currentProfile.copy(creationsCount = newCount))

        checkAndUnlockAchievements(currentProfile.totalStars, currentProfile.highestUnlockedLevel, newCount)
        return id
    }

    suspend fun deleteCreation(id: Long) {
        dao.deleteCreation(id)
    }

    suspend fun updateSoundSetting(enabled: Boolean) {
        SoundSynthesizer.isSoundEnabled = enabled
        val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(current.copy(isSoundEnabled = enabled))
    }

    suspend fun updateTowerBestHeight(height: Int) {
        val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        if (height > current.physicsTowerBestHeight) {
            val rewardStars = if (height >= 10 && current.physicsTowerBestHeight < 10) 5 else 2
            dao.insertOrUpdateProfile(
                current.copy(
                    physicsTowerBestHeight = height,
                    totalStars = current.totalStars + rewardStars
                )
            )
        }
    }

    suspend fun unlockThemeSkin(themeId: String, cost: Int): Boolean {
        val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        if (current.totalStars >= cost) {
            val themes = current.unlockedThemes.split(",").toMutableSet()
            themes.add(themeId)
            dao.insertOrUpdateProfile(
                current.copy(
                    totalStars = current.totalStars - cost,
                    unlockedThemes = themes.joinToString(",")
                )
            )
            return true
        }
        return false
    }

    suspend fun selectThemeSkin(themeId: String) {
        val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(current.copy(selectedTheme = themeId))
    }

    private suspend fun checkAndUnlockAchievements(stars: Int, levels: Int, creations: Int) {
        if (levels >= 2) unlockBadge("first_puzzle")
        if (levels >= 5) unlockBadge("master_architect")
        if (creations >= 1) unlockBadge("first_creation")
        if (creations >= 5) unlockBadge("creative_genius")
        if (stars >= 25) unlockBadge("star_collector")
        if (stars >= 50) unlockBadge("star_champion")
    }

    private suspend fun unlockBadge(badgeId: String) {
        dao.insertAchievement(
            AchievementEntity(
                id = badgeId,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
        )
    }

    fun getAllBadges(unlockedIds: Set<String>, stars: Int, creations: Int, highestLevel: Int): List<KidBadge> {
        return listOf(
            KidBadge(
                id = "first_puzzle",
                titleAr = "مهندس البداية 🚀",
                descriptionAr = "أكمل أول لغز تركيب بنجاح!",
                emoji = "🚀",
                isUnlocked = unlockedIds.contains("first_puzzle") || highestLevel >= 2,
                progress = (highestLevel - 1).toFloat().coerceIn(0f, 1f)
            ),
            KidBadge(
                id = "master_architect",
                titleAr = "المعماري العبقري 🏰",
                descriptionAr = "أكمل 5 مستويات تركيب مختلفة!",
                emoji = "🏰",
                isUnlocked = unlockedIds.contains("master_architect") || highestLevel >= 5,
                progress = (highestLevel.toFloat() / 5f).coerceIn(0f, 1f)
            ),
            KidBadge(
                id = "first_creation",
                titleAr = "المخترع الصغير 🎨",
                descriptionAr = "اصنع واحفظ أول مجسم في ورشة الإبداع!",
                emoji = "🎨",
                isUnlocked = unlockedIds.contains("first_creation") || creations >= 1,
                progress = creations.toFloat().coerceIn(0f, 1f)
            ),
            KidBadge(
                id = "creative_genius",
                titleAr = "أسطورة البناء 🌟",
                descriptionAr = "اصنع 5 مجسمات مدهشة في المعرض!",
                emoji = "🌟",
                isUnlocked = unlockedIds.contains("creative_genius") || creations >= 5,
                progress = (creations.toFloat() / 5f).coerceIn(0f, 1f)
            ),
            KidBadge(
                id = "star_collector",
                titleAr = "جامع النجوم ⭐",
                descriptionAr = "اجمع 25 نجمة ذهبية من الألغاز!",
                emoji = "⭐",
                isUnlocked = unlockedIds.contains("star_collector") || stars >= 25,
                progress = (stars.toFloat() / 25f).coerceIn(0f, 1f)
            ),
            KidBadge(
                id = "tower_master",
                titleAr = "ملك برج التوازن 🗼",
                descriptionAr = "ابنِ برجاً بارتفاع 10 مكعبات دون أن يسقط!",
                emoji = "🗼",
                isUnlocked = unlockedIds.contains("tower_master"),
                progress = 0.5f
            )
        )
    }
}
