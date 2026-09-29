package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val totalStars: Int = 0,
    val balloonHighScore: Int = 0,
    val countingHighScore: Int = 0,
    val mathHighScore: Int = 0,
    val compareHighScore: Int = 0,
    val practicedNumbersList: String = "", // e.g. "0,1,2"
    val useEasternArabic: Boolean = true, // ٠ ١ ٢ ٣ vs 0 1 2 3
    val soundEnabled: Boolean = true
)
