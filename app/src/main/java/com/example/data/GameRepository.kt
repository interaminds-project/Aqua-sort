package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(
    private val userProgressDao: UserProgressDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_progress_backup", Context.MODE_PRIVATE)

    val userProgress: Flow<UserProgress> = userProgressDao.getUserProgress().map { dbProgress ->
        val current = dbProgress ?: UserProgress()
        val backupLevel = prefs.getInt("currentLevel", 1)
        if (backupLevel > current.currentLevel) {
            val restored = restoreFromPrefs(current)
            userProgressDao.insertProgress(restored)
            restored
        } else {
            current
        }
    }

    suspend fun getProgress(): UserProgress {
        val dbProgress = userProgressDao.getProgressSynchronously() ?: UserProgress()
        val backupLevel = prefs.getInt("currentLevel", 1)
        return if (backupLevel > dbProgress.currentLevel) {
            val restored = restoreFromPrefs(dbProgress)
            userProgressDao.insertProgress(restored)
            restored
        } else {
            dbProgress
        }
    }

    suspend fun saveProgress(progress: UserProgress) {
        userProgressDao.insertProgress(progress)

        // Save secondary backup to SharedPreferences to protect player progress across Play Store updates
        prefs.edit().apply {
            putInt("currentLevel", progress.currentLevel)
            putInt("coins", progress.coins)
            putInt("gems", progress.gems)
            putInt("lives", progress.lives)
            putLong("lastLifeRefillTime", progress.lastLifeRefillTime)
            putString("unlockedTubes", progress.unlockedTubes)
            putString("unlockedThemes", progress.unlockedThemes)
            putString("equippedTube", progress.equippedTube)
            putString("equippedTheme", progress.equippedTheme)
            putString("completedAchievements", progress.completedAchievements)
            putString("claimedSectionRewards", progress.claimedSectionRewards)
            putLong("dailyRewardLastClaimed", progress.dailyRewardLastClaimed)
            putInt("dailyRewardDayIndex", progress.dailyRewardDayIndex)
            putLong("luckyWheelLastSpun", progress.luckyWheelLastSpun)
            putInt("selectedAvatarId", progress.selectedAvatarId)
            putInt("totalGamesPlayed", progress.totalGamesPlayed)
            putInt("totalGamesWon", progress.totalGamesWon)
            putInt("bestTimeSeconds", progress.bestTimeSeconds)
            putInt("totalStars", progress.totalStars)
            putBoolean("isSoundEnabled", progress.isSoundEnabled)
            putBoolean("isMusicEnabled", progress.isMusicEnabled)
            putBoolean("isVibrationEnabled", progress.isVibrationEnabled)
            putInt("fpsLimit", progress.fpsLimit)
            putString("graphicsQuality", progress.graphicsQuality)
            putString("viewedTutorials", progress.viewedTutorials)
            apply()
        }
    }

    private fun restoreFromPrefs(base: UserProgress): UserProgress {
        return base.copy(
            currentLevel = maxOf(base.currentLevel, prefs.getInt("currentLevel", 1)),
            coins = maxOf(base.coins, prefs.getInt("coins", 200)),
            gems = maxOf(base.gems, prefs.getInt("gems", 50)),
            lives = prefs.getInt("lives", base.lives),
            lastLifeRefillTime = prefs.getLong("lastLifeRefillTime", base.lastLifeRefillTime),
            unlockedTubes = prefs.getString("unlockedTubes", base.unlockedTubes) ?: base.unlockedTubes,
            unlockedThemes = prefs.getString("unlockedThemes", base.unlockedThemes) ?: base.unlockedThemes,
            equippedTube = prefs.getString("equippedTube", base.equippedTube) ?: base.equippedTube,
            equippedTheme = prefs.getString("equippedTheme", base.equippedTheme) ?: base.equippedTheme,
            completedAchievements = prefs.getString("completedAchievements", base.completedAchievements) ?: base.completedAchievements,
            claimedSectionRewards = prefs.getString("claimedSectionRewards", base.claimedSectionRewards) ?: base.claimedSectionRewards,
            dailyRewardLastClaimed = prefs.getLong("dailyRewardLastClaimed", base.dailyRewardLastClaimed),
            dailyRewardDayIndex = prefs.getInt("dailyRewardDayIndex", base.dailyRewardDayIndex),
            luckyWheelLastSpun = prefs.getLong("luckyWheelLastSpun", base.luckyWheelLastSpun),
            selectedAvatarId = prefs.getInt("selectedAvatarId", base.selectedAvatarId),
            totalGamesPlayed = maxOf(base.totalGamesPlayed, prefs.getInt("totalGamesPlayed", 0)),
            totalGamesWon = maxOf(base.totalGamesWon, prefs.getInt("totalGamesWon", 0)),
            bestTimeSeconds = prefs.getInt("bestTimeSeconds", base.bestTimeSeconds),
            totalStars = maxOf(base.totalStars, prefs.getInt("totalStars", 0)),
            isSoundEnabled = prefs.getBoolean("isSoundEnabled", base.isSoundEnabled),
            isMusicEnabled = prefs.getBoolean("isMusicEnabled", base.isMusicEnabled),
            isVibrationEnabled = prefs.getBoolean("isVibrationEnabled", base.isVibrationEnabled),
            fpsLimit = prefs.getInt("fpsLimit", base.fpsLimit),
            graphicsQuality = prefs.getString("graphicsQuality", base.graphicsQuality) ?: base.graphicsQuality,
            viewedTutorials = prefs.getString("viewedTutorials", base.viewedTutorials) ?: base.viewedTutorials
        )
    }
}
