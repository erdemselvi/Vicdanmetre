package com.erdemselvi.vicdanmetre.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdemselvi.vicdanmetre.models.*
import com.erdemselvi.vicdanmetre.repository.FirebaseRepository
import com.erdemselvi.vicdanmetre.utils.ScenarioLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepository()
    private var currentUserId: String? = null

    // State flows
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _scenarios = MutableStateFlow<List<Scenario>>(emptyList())
    val scenarios: StateFlow<List<Scenario>> = _scenarios.asStateFlow()

    private val _currentScenario = MutableStateFlow<Scenario?>(null)
    val currentScenario: StateFlow<Scenario?> = _currentScenario.asStateFlow()

    private val _currentChapter = MutableStateFlow<Chapter?>(null)
    val currentChapter: StateFlow<Chapter?> = _currentChapter.asStateFlow()

    private val _completedScenarios = MutableStateFlow<Set<String>>(emptySet())
    val completedScenarios: StateFlow<Set<String>> = _completedScenarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Kullanıcı ID'sini al
                currentUserId = firebaseRepo.getCurrentUserId()

                // Kullanıcı profilini yükle veya oluştur
                loadUserProfile()

                // Senaryoları yükle
                loadScenarios()

                // Tamamlanan senaryoları yükle
                loadCompletedScenarios()

                // Streak güncelle
                updateDailyStreak()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                var profile = firebaseRepo.getUserProfile(userId)

                if (profile == null) {
                    profile = firebaseRepo.createUserProfile(userId)
                }

                _userProfile.value = profile
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadScenarios() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val scenarios = ScenarioLoader.loadScenariosFromAssets(context)
                _scenarios.value = scenarios
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadCompletedScenarios() {
        try {
            val userId = currentUserId ?: return
            val completed = mutableSetOf<String>()

            _scenarios.value.forEach { scenario ->
                if (firebaseRepo.isScenarioCompleted(userId, scenario.id)) {
                    completed.add(scenario.id)
                }
            }

            _completedScenarios.value = completed
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadScenario(scenarioId: String) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val scenario = ScenarioLoader.loadScenarioById(context, scenarioId)
                _currentScenario.value = scenario
                _currentChapter.value = scenario?.chapters?.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun makeChoice(choice: Choice) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch

                // Vicdan etkisini kaydet
                firebaseRepo.updateConscienceProfile(userId, choice.conscienceImpact)

                // XP ver
                val xpGained = calculateXP(choice)
                firebaseRepo.addExperience(userId, xpGained)

                // Sonraki bölüme geç
                val currentScenario = _currentScenario.value ?: return@launch
                val currentChapter = _currentChapter.value ?: return@launch

                val nextChapterId = currentChapter.nextChapterIds[choice.id]
                val nextChapter = currentScenario.chapters.find { it.id == nextChapterId }

                _currentChapter.value = nextChapter

                // Profili yeniden yükle
                loadUserProfile()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun completeScenario(scenarioId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch

                // Senaryo tamamlanma durumunu kaydet
                firebaseRepo.saveScenarioProgress(userId, scenarioId, true)

                // Tamamlanan senaryolar listesini güncelle
                _completedScenarios.value = _completedScenarios.value + scenarioId

                // Profili yeniden yükle
                loadUserProfile()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isScenarioCompleted(scenarioId: String): Boolean {
        return _completedScenarios.value.contains(scenarioId)
    }

    fun saveJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                firebaseRepo.saveJournalEntry(userId, entry)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateDailyStreak() {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                firebaseRepo.updateStreak(userId)
                loadUserProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateXP(choice: Choice): Int {
        val baseXP = 10
        val impactBonus = with(choice.conscienceImpact) {
            (honesty + justice + empathy + responsibility + patience + courage + wisdom) * 2
        }
        return (baseXP + impactBonus).coerceAtLeast(0)
    }

    fun getProgressToNextLevel(): Float {
        val profile = _userProfile.value ?: return 0f
        val currentXP = profile.experiencePoints

        // Sonraki seviye için gereken XP
        val nextLevelXP = when (profile.level) {
            1 -> 100
            2 -> 250
            3 -> 500
            4 -> 1000
            5 -> 1500
            6 -> 2500
            7 -> 4000
            8 -> 6000
            9 -> 9000
            10 -> 13000
            else -> 13000 + (profile.level - 10) * 2000
        }

        val currentLevelXP = when (profile.level) {
            1 -> 0
            2 -> 100
            3 -> 250
            4 -> 500
            5 -> 1000
            6 -> 1500
            7 -> 2500
            8 -> 4000
            9 -> 6000
            10 -> 9000
            else -> 13000 + (profile.level - 11) * 2000
        }

        val progress = (currentXP - currentLevelXP).toFloat() / (nextLevelXP - currentLevelXP)
        return progress.coerceIn(0f, 1f)
    }
}