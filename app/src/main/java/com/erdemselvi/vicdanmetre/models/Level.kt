package com.erdemselvi.vicdanmetre.models

data class Level(
    val levelNumber: Int,
    val title: String,
    val requiredXP: Int,
    val rewards: LevelReward,
    val unlockedFeatures: List<String>,
    val unlockedScenarios: List<String>
)

data class LevelReward(
    val crystals: Int,
    val wisdomPoints: Int,
    val badges: List<String> = emptyList(),
    val avatarItems: List<String> = emptyList()
)

// Seviye tablosu örneği
object LevelSystem {
    val levels = listOf(
        Level(1, "Vicdan Öğrencisi", 0,
            LevelReward(50, 10),
            listOf("Temel Senaryolar"),
            listOf("scenario_001", "scenario_002", "scenario_003")),
        Level(2, "İlk Adım", 100,
            LevelReward(75, 15),
            listOf("Günlük Görevler"),
            listOf("scenario_004", "scenario_005")),
        Level(5, "Adalet Arayıcısı", 500,
            LevelReward(150, 50, listOf("justice_seeker")),
            listOf("Arkadaş Sistemi", "Vicdan Konseyi"),
            listOf("scenario_011", "scenario_012", "scenario_013")),
        Level(10, "Hikmet Yolcusu", 1500,
            LevelReward(300, 100, listOf("wisdom_walker"), listOf("philosopher_outfit")),
            listOf("Günlük Yazma", "Özel Senaryolar"),
            listOf("scenario_025", "scenario_026")),
        Level(20, "Vicdan Rehberi", 5000,
            LevelReward(1000, 500, listOf("conscience_guide"), listOf("guide_title", "premium_avatar")),
            listOf("Senaryo Oluşturma", "Mentorluk"),
            listOf("scenario_050"))
    )

    fun getXPForNextLevel(currentLevel: Int): Int {
        return levels.find { it.levelNumber == currentLevel + 1 }?.requiredXP ?: Int.MAX_VALUE
    }

    fun getLevelFromXP(xp: Int): Int {
        return levels.lastOrNull { it.requiredXP <= xp }?.levelNumber ?: 1
    }
}