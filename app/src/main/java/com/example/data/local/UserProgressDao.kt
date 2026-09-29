package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET totalStars = totalStars + :stars WHERE id = 1")
    suspend fun addStars(stars: Int)
}
