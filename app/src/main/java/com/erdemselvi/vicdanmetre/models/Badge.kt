package com.erdemselvi.vicdanmetre.models

import java.time.LocalDateTime

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val category: BadgeCategory,
    val rarity: BadgeRarity,
    val iconUrl: String,
    val requirement: BadgeRequirement,
    val reward: BadgeReward
)

enum class BadgeCategory(val displayName: String) {
    HONESTY("Dürüstlük"),
    JUSTICE("Adalet"),
    EMPATHY("Empati"),
    RESPONSIBILITY("Sorumluluk"),
    PATIENCE("Sabır"),
    COURAGE("Cesaret"),
    WISDOM("Hikmet"),
    SPECIAL("Özel"),
    SEASONAL("Sezonluk"),
    COMMUNITY("Topluluk")
}

enum class BadgeRarity(val displayName: String, val color: String) {
    COMMON("Sıradan", "#9E9E9E"),
    RARE("Nadir", "#2196F3"),
    EPIC("Epik", "#9C27B0"),
    LEGENDARY("Efsanevi", "#FF9800"),
    MYTHIC("Mitik", "#F44336")
}

data class BadgeRequirement(
    val type: RequirementType,
    val threshold: Int,
    val specificScenarioId: String? = null,
    val consecutiveDays: Int? = null
)

enum class RequirementType {
    CONSCIENCE_POINTS,      // Toplam vicdan puanı
    SCENARIOS_COMPLETED,    // Tamamlanan senaryo sayısı
    PERFECT_CHOICES,        // Mükemmel seçimler
    DAILY_STREAK,          // Günlük giriş serisi
    SPECIFIC_SCENARIO,     // Belirli bir senaryo
    COMMUNITY_VOTES,       // Topluluk oyları
    JOURNAL_ENTRIES,       // Günlük yazıları
    NIGHT_PLAY            // Gece oyunu
}

data class BadgeReward(
    val crystals: Int = 0,
    val wisdomPoints: Int = 0,
    val unlockedContent: List<String> = emptyList(),
    val specialTitle: String? = null
)

data class UserBadge(
    val badgeId: String,
    val earnedAt: LocalDateTime,
    val progress: Int = 100 // %
)