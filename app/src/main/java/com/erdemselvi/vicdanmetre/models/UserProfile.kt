package com.erdemselvi.vicdanmetre.models

import java.time.LocalDateTime

data class UserProfile(
    val userId: String,
    val username: String,
    val level: Int = 1,
    val experiencePoints: Int = 0,
    val totalScenarios: Int = 0,
    val conscience: ConscienceProfile,
    val currency: UserCurrency,
    val avatar: Avatar,
    val statistics: UserStatistics,
    val streakData: StreakData,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime = LocalDateTime.now()
)

data class ConscienceProfile(
    val honesty: Int = 0,
    val justice: Int = 0,
    val empathy: Int = 0,
    val responsibility: Int = 0,
    val patience: Int = 0,
    val courage: Int = 0,
    val wisdom: Int = 0
) {
    fun getTotalScore(): Int = honesty + justice + empathy + responsibility + patience + courage + wisdom

    fun getDominantTrait(): String {
        val traits = mapOf(
            "Dürüstlük" to honesty,
            "Adalet" to justice,
            "Empati" to empathy,
            "Sorumluluk" to responsibility,
            "Sabır" to patience,
            "Cesaret" to courage,
            "Hikmet" to wisdom
        )
        return traits.maxByOrNull { it.value }?.key ?: "Dengeli"
    }
}

data class UserCurrency(
    val crystals: Int = 100,        // Vicdan Kristali
    val wisdomPoints: Int = 0,      // Hikmet Puanı
    val virtueMedals: Int = 0,      // Erdem Madalyası
    val giftBoxes: Int = 0          // Hediye Kutusu
)

data class Avatar(
    val skinTone: String = "default",
    val hairStyle: String = "default",
    val outfit: String = "default",
    val accessory: String = "none",
    val spiritAnimal: SpiritAnimal = SpiritAnimal.NONE,
    val title: String = "Vicdan Öğrencisi"
)

enum class SpiritAnimal(val displayName: String, val trait: String) {
    NONE("Henüz Yok", ""),
    OWL("Baykuş", "Hikmet Arayıcısı"),
    LION("Aslan", "Cesaret Simgesi"),
    DOVE("Güvercin", "Barış Elçisi"),
    ELEPHANT("Fil", "Sadakat ve Hafıza"),
    WOLF("Kurt", "Adalet Koruyucusu"),
    DOLPHIN("Yunus", "Empati Ustası")
}

data class UserStatistics(
    val totalChoicesMade: Int = 0,
    val perfectScenarios: Int = 0,        // Tüm doğru seçimler
    val regrettedChoices: Int = 0,        // Pişman olunan kararlar
    val helpedFriends: Int = 0,
    val longestStreak: Int = 0,
    val favoriteCategory: ScenarioCategory? = null,
    val playTimeMinutes: Int = 0
)

data class StreakData(
    val currentStreak: Int = 0,
    val lastLoginDate: LocalDateTime = LocalDateTime.now(),
    val streakMultiplier: Float = 1.0f,
    val totalDaysPlayed: Int = 0
)
