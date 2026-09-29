package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.local.AppDatabase
import com.example.data.local.UserProgressEntity
import com.example.data.model.NUMBERS_DATA
import com.example.data.model.NumberItem
import com.example.data.model.REWARD_BADGES
import com.example.data.model.RewardBadge
import com.example.data.repository.NumbersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppScreen {
    data object Home : AppScreen
    data object ExploreNumbers : AppScreen
    data class NumberDetail(val number: NumberItem) : AppScreen
    data class Tracing(val number: NumberItem) : AppScreen
    data object CountingGame : AppScreen
    data object BalloonPopGame : AppScreen
    data object CompareGame : AppScreen
    data object MathGame : AppScreen
    data object Stickers : AppScreen
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NumbersRepository(db.userProgressDao())
    val soundManager = SoundManager(application)

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _navigationStack = mutableListOf<AppScreen>()

    val userProgress: StateFlow<UserProgressEntity> = repository.userProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgressEntity()
        )

    val numbersList: List<NumberItem> = repository.getAllNumbers()
    val badgesList: List<RewardBadge> = repository.getBadges()

    private val _confettiTrigger = MutableStateFlow(false)
    val confettiTrigger: StateFlow<Boolean> = _confettiTrigger.asStateFlow()

    init {
        // Initialize sound setting when progress loads
        viewModelScope.launch {
            userProgress.collect { progress ->
                soundManager.soundEnabled = progress.soundEnabled
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playPopSound()
        _navigationStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        soundManager.playPopSound()
        if (_navigationStack.isNotEmpty()) {
            _currentScreen.value = _navigationStack.removeAt(_navigationStack.size - 1)
            return true
        } else if (_currentScreen.value !is AppScreen.Home) {
            _currentScreen.value = AppScreen.Home
            return true
        }
        return false
    }

    fun toggleNumeralStyle() {
        val current = userProgress.value
        val updated = current.copy(useEasternArabic = !current.useEasternArabic)
        viewModelScope.launch {
            repository.saveFullProgress(updated)
        }
        soundManager.playPopSound()
    }

    fun toggleSound() {
        val current = userProgress.value
        val newSound = !current.soundEnabled
        soundManager.soundEnabled = newSound
        val updated = current.copy(soundEnabled = newSound)
        viewModelScope.launch {
            repository.saveFullProgress(updated)
        }
    }

    fun addStars(count: Int) {
        val current = userProgress.value
        val updated = current.copy(totalStars = current.totalStars + count)
        viewModelScope.launch {
            repository.saveFullProgress(updated)
        }
        soundManager.playStarSound()
        triggerCelebration()
    }

    fun triggerCelebration() {
        _confettiTrigger.value = true
        soundManager.playFanfareSound()
    }

    fun resetConfetti() {
        _confettiTrigger.value = false
    }

    fun markPracticed(numberValue: Int) {
        val current = userProgress.value
        val practicedSet = current.practicedNumbersList
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()

        val isNew = practicedSet.add(numberValue)
        val updatedList = practicedSet.joinToString(",")
        val bonus = if (isNew) 3 else 1
        val updated = current.copy(
            practicedNumbersList = updatedList,
            totalStars = current.totalStars + bonus
        )
        viewModelScope.launch {
            repository.saveFullProgress(updated)
        }
    }

    fun getDisplayNumeral(number: NumberItem): String {
        return if (userProgress.value.useEasternArabic) number.arabicIndic else number.western
    }

    fun getDisplayNumeralForInt(value: Int): String {
        val item = numbersList.find { it.value == value }
        return if (item != null) getDisplayNumeral(item) else value.toString()
    }

    fun speakNumber(number: NumberItem) {
        val numeral = getDisplayNumeral(number)
        soundManager.speak("${number.arabicWord}. $numeral")
    }

    fun speakText(text: String) {
        soundManager.speak(text)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
