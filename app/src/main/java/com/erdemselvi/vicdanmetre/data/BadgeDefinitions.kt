package com.erdemselvi.vicdanmetre.data

import com.erdemselvi.vicdanmetre.models.*

object BadgeDefinitions {

    val allBadges = listOf(
        // HONESTY BADGES (Dürüstlük)
        Badge(
            id = "badge_honest_1",
            name = "İlk Adım",
            description = "İlk dürüst seçimini yaptın",
            category = BadgeCategory.HONESTY,
            rarity = BadgeRarity.COMMON,
            iconUrl = "🤝",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 10),
            reward = BadgeReward(crystals = 50, wisdomPoints = 10)
        ),
        Badge(
            id = "badge_honest_2",
            name = "Doğrucu",
            description = "50 dürüstlük puanına ulaştın",
            category = BadgeCategory.HONESTY,
            rarity = BadgeRarity.RARE,
            iconUrl = "💎",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_honest_3",
            name = "Gerçeğin Sesi",
            description = "100 dürüstlük puanına ulaştın",
            category = BadgeCategory.HONESTY,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🔮",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Gerçeğin Sesi")
        ),
        Badge(
            id = "badge_honest_4",
            name = "Yalan Yok",
            description = "Hiç yalan seçimi yapmadan 10 senaryo tamamladın",
            category = BadgeCategory.HONESTY,
            rarity = BadgeRarity.LEGENDARY,
            iconUrl = "👑",
            requirement = BadgeRequirement(RequirementType.PERFECT_CHOICES, 10),
            reward = BadgeReward(crystals = 500, wisdomPoints = 100, specialTitle = "Yalansız Yürek")
        ),

        // JUSTICE BADGES (Adalet)
        Badge(
            id = "badge_justice_1",
            name = "Adaletin Kılıcı",
            description = "İlk adil kararını verdin",
            category = BadgeCategory.JUSTICE,
            rarity = BadgeRarity.COMMON,
            iconUrl = "⚖️",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 10),
            reward = BadgeReward(crystals = 50, wisdomPoints = 10)
        ),
        Badge(
            id = "badge_justice_2",
            name = "Hakkaniyet Savunucusu",
            description = "50 adalet puanına ulaştın",
            category = BadgeCategory.JUSTICE,
            rarity = BadgeRarity.RARE,
            iconUrl = "🛡️",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_justice_3",
            name = "Adalet Bekçisi",
            description = "100 adalet puanına ulaştın",
            category = BadgeCategory.JUSTICE,
            rarity = BadgeRarity.EPIC,
            iconUrl = "⚔️",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Adalet Bekçisi")
        ),

        // EMPATHY BADGES (Empati)
        Badge(
            id = "badge_empathy_1",
            name = "Kalp Gözü",
            description = "İlk empatik kararını verdin",
            category = BadgeCategory.EMPATHY,
            rarity = BadgeRarity.COMMON,
            iconUrl = "❤️",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 10),
            reward = BadgeReward(crystals = 50, wisdomPoints = 10)
        ),
        Badge(
            id = "badge_empathy_2",
            name = "Merhamet Eli",
            description = "50 empati puanına ulaştın",
            category = BadgeCategory.EMPATHY,
            rarity = BadgeRarity.RARE,
            iconUrl = "🤲",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_empathy_3",
            name = "Altın Kalp",
            description = "100 empati puanına ulaştın",
            category = BadgeCategory.EMPATHY,
            rarity = BadgeRarity.EPIC,
            iconUrl = "💛",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Altın Kalp")
        ),
        Badge(
            id = "badge_empathy_4",
            name = "İnsanlık Işığı",
            description = "200 empati puanına ulaştın",
            category = BadgeCategory.EMPATHY,
            rarity = BadgeRarity.LEGENDARY,
            iconUrl = "✨",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 200),
            reward = BadgeReward(crystals = 500, wisdomPoints = 100, specialTitle = "İnsanlık Işığı")
        ),

        // COURAGE BADGES (Cesaret)
        Badge(
            id = "badge_courage_1",
            name = "Cesur Yürek",
            description = "İlk cesur kararını verdin",
            category = BadgeCategory.COURAGE,
            rarity = BadgeRarity.COMMON,
            iconUrl = "🦁",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 10),
            reward = BadgeReward(crystals = 50, wisdomPoints = 10)
        ),
        Badge(
            id = "badge_courage_2",
            name = "Korku Yok",
            description = "50 cesaret puanına ulaştın",
            category = BadgeCategory.COURAGE,
            rarity = BadgeRarity.RARE,
            iconUrl = "🔥",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_courage_3",
            name = "Aslan Pençesi",
            description = "Zor bir senaryoda en cesur kararı verdin",
            category = BadgeCategory.COURAGE,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🦅",
            requirement = BadgeRequirement(RequirementType.SPECIFIC_SCENARIO, 1, "hard_courage_scenario"),
            reward = BadgeReward(crystals = 300, wisdomPoints = 75)
        ),

        // STREAK BADGES (Günlük Giriş)
        Badge(
            id = "badge_streak_1",
            name = "Başlangıç",
            description = "3 gün üst üste giriş yaptın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.COMMON,
            iconUrl = "🔥",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 3),
            reward = BadgeReward(crystals = 75, wisdomPoints = 15)
        ),
        Badge(
            id = "badge_streak_2",
            name = "Sadık Oyuncu",
            description = "7 gün üst üste giriş yaptın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.RARE,
            iconUrl = "🔥🔥",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 7),
            reward = BadgeReward(crystals = 150, wisdomPoints = 30)
        ),
        Badge(
            id = "badge_streak_3",
            name = "İki Hafta",
            description = "14 gün üst üste giriş yaptın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.EPIC,
            iconUrl = "💫",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 14),
            reward = BadgeReward(crystals = 300, wisdomPoints = 60)
        ),
        Badge(
            id = "badge_streak_4",
            name = "Bir Ay",
            description = "30 gün üst üste giriş yaptın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.LEGENDARY,
            iconUrl = "🌟",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 30),
            reward = BadgeReward(crystals = 1000, wisdomPoints = 200, specialTitle = "Vicdan Ustası")
        ),
        Badge(
            id = "badge_streak_5",
            name = "100 Gün",
            description = "100 gün üst üste giriş yaptın!",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.MYTHIC,
            iconUrl = "👑",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 100),
            reward = BadgeReward(crystals = 5000, wisdomPoints = 1000, specialTitle = "Efsane Vicdan")
        ),

        // SCENARIO COMPLETION BADGES
        Badge(
            id = "badge_scenario_1",
            name = "İlk Senaryo",
            description = "İlk senaryonu tamamladın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.COMMON,
            iconUrl = "📖",
            requirement = BadgeRequirement(RequirementType.SCENARIOS_COMPLETED, 1),
            reward = BadgeReward(crystals = 50, wisdomPoints = 10)
        ),
        Badge(
            id = "badge_scenario_2",
            name = "Hikaye Okuyucusu",
            description = "10 senaryo tamamladın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.RARE,
            iconUrl = "📚",
            requirement = BadgeRequirement(RequirementType.SCENARIOS_COMPLETED, 10),
            reward = BadgeReward(crystals = 200, wisdomPoints = 40)
        ),
        Badge(
            id = "badge_scenario_3",
            name = "Tecrübeli",
            description = "25 senaryo tamamladın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🎭",
            requirement = BadgeRequirement(RequirementType.SCENARIOS_COMPLETED, 25),
            reward = BadgeReward(crystals = 500, wisdomPoints = 100)
        ),
        Badge(
            id = "badge_scenario_4",
            name = "Vicdan Kitaplığı",
            description = "50 senaryo tamamladın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.LEGENDARY,
            iconUrl = "📜",
            requirement = BadgeRequirement(RequirementType.SCENARIOS_COMPLETED, 50),
            reward = BadgeReward(crystals = 1500, wisdomPoints = 300, specialTitle = "Vicdan Kütüphanecisi")
        ),

        // JOURNAL BADGES
        Badge(
            id = "badge_journal_1",
            name = "İlk Günlük",
            description = "İlk günlük yazını yazdın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.COMMON,
            iconUrl = "✍️",
            requirement = BadgeRequirement(RequirementType.JOURNAL_ENTRIES, 1),
            reward = BadgeReward(crystals = 50, wisdomPoints = 20)
        ),
        Badge(
            id = "badge_journal_2",
            name = "Günlük Yazarı",
            description = "10 günlük yazısı yazdın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.RARE,
            iconUrl = "📝",
            requirement = BadgeRequirement(RequirementType.JOURNAL_ENTRIES, 10),
            reward = BadgeReward(crystals = 150, wisdomPoints = 50)
        ),
        Badge(
            id = "badge_journal_3",
            name = "Filozof",
            description = "30 günlük yazısı yazdın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🧠",
            requirement = BadgeRequirement(RequirementType.JOURNAL_ENTRIES, 30),
            reward = BadgeReward(crystals = 400, wisdomPoints = 150, specialTitle = "Düşünür")
        ),

        // SPECIAL/HIDDEN BADGES
        Badge(
            id = "badge_night_owl",
            name = "Gece Kuşu",
            description = "Gece 00:00-05:00 arası oynadın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.RARE,
            iconUrl = "🦉",
            requirement = BadgeRequirement(RequirementType.NIGHT_PLAY, 1),
            reward = BadgeReward(crystals = 200, wisdomPoints = 50)
        ),
        Badge(
            id = "badge_perfectionist",
            name = "Mükemmeliyetçi",
            description = "Bir senaryoyu 5 farklı şekilde tamamladın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🎯",
            requirement = BadgeRequirement(RequirementType.SPECIFIC_SCENARIO, 5, "replay_scenario"),
            reward = BadgeReward(crystals = 350, wisdomPoints = 80)
        ),
        Badge(
            id = "badge_community_1",
            name = "Topluluk Üyesi",
            description = "Vicdan Konseyinde ilk oyunu verdin",
            category = BadgeCategory.COMMUNITY,
            rarity = BadgeRarity.COMMON,
            iconUrl = "🗳️",
            requirement = BadgeRequirement(RequirementType.COMMUNITY_VOTES, 1),
            reward = BadgeReward(crystals = 75, wisdomPoints = 20)
        ),
        Badge(
            id = "badge_community_2",
            name = "Aktif Seçmen",
            description = "20 topluluk oylamasına katıldın",
            category = BadgeCategory.COMMUNITY,
            rarity = BadgeRarity.RARE,
            iconUrl = "🎖️",
            requirement = BadgeRequirement(RequirementType.COMMUNITY_VOTES, 20),
            reward = BadgeReward(crystals = 250, wisdomPoints = 60)
        ),

        // SEASONAL BADGES
        Badge(
            id = "badge_ramadan",
            name = "Ramazan Bereketı",
            description = "Ramazan ayında özel görevleri tamamladın",
            category = BadgeCategory.SEASONAL,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🌙",
            requirement = BadgeRequirement(RequirementType.SPECIFIC_SCENARIO, 1, "ramadan_special"),
            reward = BadgeReward(crystals = 500, wisdomPoints = 100)
        ),
        Badge(
            id = "badge_new_year",
            name = "Yeni Yıl Azmi",
            description = "Yeni yılda ilk hafta her gün giriş yaptın",
            category = BadgeCategory.SEASONAL,
            rarity = BadgeRarity.RARE,
            iconUrl = "🎊",
            requirement = BadgeRequirement(RequirementType.DAILY_STREAK, 7, consecutiveDays = 7),
            reward = BadgeReward(crystals = 300, wisdomPoints = 75)
        ),

        // RESPONSIBILITY BADGES
        Badge(
            id = "badge_responsibility_1",
            name = "Sorumlu Birey",
            description = "50 sorumluluk puanına ulaştın",
            category = BadgeCategory.RESPONSIBILITY,
            rarity = BadgeRarity.RARE,
            iconUrl = "🎓",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_responsibility_2",
            name = "Güvenilir",
            description = "100 sorumluluk puanına ulaştın",
            category = BadgeCategory.RESPONSIBILITY,
            rarity = BadgeRarity.EPIC,
            iconUrl = "⭐",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Güvenilir Kişi")
        ),

        // PATIENCE BADGES
        Badge(
            id = "badge_patience_1",
            name = "Sabırlı Yürek",
            description = "50 sabır puanına ulaştın",
            category = BadgeCategory.PATIENCE,
            rarity = BadgeRarity.RARE,
            iconUrl = "🕊️",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_patience_2",
            name = "Dingin Ruh",
            description = "100 sabır puanına ulaştın",
            category = BadgeCategory.PATIENCE,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🧘",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Sabır Taşı")
        ),

        // WISDOM BADGES
        Badge(
            id = "badge_wisdom_1",
            name = "Bilge Aday",
            description = "50 hikmet puanına ulaştın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.RARE,
            iconUrl = "🦉",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 50),
            reward = BadgeReward(crystals = 100, wisdomPoints = 25)
        ),
        Badge(
            id = "badge_wisdom_2",
            name = "Hikmet Sahibi",
            description = "100 hikmet puanına ulaştın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.EPIC,
            iconUrl = "🔮",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 100),
            reward = BadgeReward(crystals = 250, wisdomPoints = 50, specialTitle = "Hikmet Sahibi")
        ),
        Badge(
            id = "badge_wisdom_3",
            name = "Büyük Üstad",
            description = "200 hikmet puanına ulaştın",
            category = BadgeCategory.WISDOM,
            rarity = BadgeRarity.LEGENDARY,
            iconUrl = "🎓",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 200),
            reward = BadgeReward(crystals = 1000, wisdomPoints = 200, specialTitle = "Büyük Üstad")
        ),

        // ULTIMATE BADGES
        Badge(
            id = "badge_ultimate_1",
            name = "Vicdan Rehberi",
            description = "Tüm vicdan özelliklerinde 100+ puan",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.MYTHIC,
            iconUrl = "🌟",
            requirement = BadgeRequirement(RequirementType.CONSCIENCE_POINTS, 700),
            reward = BadgeReward(crystals = 2000, wisdomPoints = 500, specialTitle = "Vicdan Rehberi")
        ),
        Badge(
            id = "badge_ultimate_2",
            name = "Efsane",
            description = "50 rozet kazandın",
            category = BadgeCategory.SPECIAL,
            rarity = BadgeRarity.MYTHIC,
            iconUrl = "👑",
            requirement = BadgeRequirement(RequirementType.SPECIFIC_SCENARIO, 50),
            reward = BadgeReward(crystals = 5000, wisdomPoints = 1000, specialTitle = "Efsane Oyuncu")
        )
    )

    fun getBadgesByCategory(category: BadgeCategory): List<Badge> {
        return allBadges.filter { it.category == category }
    }

    fun getBadgesByRarity(rarity: BadgeRarity): List<Badge> {
        return allBadges.filter { it.rarity == rarity }
    }

    fun getUnlockedBadges(userBadgeIds: List<String>): List<Badge> {
        return allBadges.filter { userBadgeIds.contains(it.id) }
    }
}