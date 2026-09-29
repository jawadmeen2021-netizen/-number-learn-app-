package com.example.data.repository

import com.example.data.local.UserProgressDao
import com.example.data.local.UserProgressEntity
import com.example.data.model.NUMBERS_DATA
import com.example.data.model.NumberItem
import com.example.data.model.REWARD_BADGES
import com.example.data.model.RewardBadge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NumbersRepository(private val dao: UserProgressDao) {

    val userProgress: Flow<UserProgressEntity> = dao.getProgress().map { entity ->
        entity ?: UserProgressEntity()
    }

    fun getAllNumbers(): List<NumberItem> = NUMBERS_DATA

    fun getNumber(value: Int): NumberItem? = NUMBERS_DATA.find { it.value == value }

    fun getBadges(): List<RewardBadge> = REWARD_BADGES

    suspend fun addStars(count: Int) {
        dao.addStars(count)
    }

    suspend fun updateHighScore(game: String, score: Int) {
        val current = dao.getProgress()
        // We read or create default
        // We'll update the specific game score
    }

    suspend fun saveFullProgress(progress: UserProgressEntity) {
        dao.saveProgress(progress)
    }

    suspend fun markNumberPracticed(numValue: Int, starsEarned: Int = 1) {
        // Will be called by ViewModel with current progress
    }
}
