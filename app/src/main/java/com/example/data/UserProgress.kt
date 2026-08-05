package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val currentLevel: Int = 1,
    val coins: Int = 200,
    val gems: Int = 50,
    val lives: Int = 5,
    val lastLifeRefillTime: Long = System.currentTimeMillis(),
    
    // Serialized Lists (comma separated IDs)
    val unlockedTubes: String = "default",
    val unlockedThemes: String = "cyberpunk", // Starts with cyberpunk theme!
    val equippedTube: String = "default",
    val equippedTheme: String = "cyberpunk",
    
    val completedAchievements: String = "",
    val claimedSectionRewards: String = "",
    val dailyRewardLastClaimed: Long = 0,
    val dailyRewardDayIndex: Int = 0,
    val luckyWheelLastSpun: Long = 0,
    val selectedAvatarId: Int = 0,
    
    // Game statistics
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val bestTimeSeconds: Int = 0,
    val totalStars: Int = 0,
    
    // Settings
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val fpsLimit: Int = 60,
    val graphicsQuality: String = "High",
    val viewedTutorials: String = ""
)
