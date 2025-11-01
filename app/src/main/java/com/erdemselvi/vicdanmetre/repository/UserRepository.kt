package com.erdemselvi.vicdanmetre.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.erdemselvi.vicdanmetre.database.*
import com.erdemselvi.vicdanmetre.models.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class UserRepository(
    private val userDao: UserDao,
    private val badgeDao: BadgeDao,
    private val questDao: QuestDao
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getOrCreateUser(userId: String): UserProfile {
        val entity = userDao.getUser(userId) ?: createDefaultUser(userId)
        return entity.toUserProfile()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createDefaultUser(userId: String): UserEntity {
        val newUser = UserEntity(
            userId = userId,
            username = "Oyuncu${userId.take(6)}",
            level = 1,
            experiencePoints = 0,
            totalScenarios = 0,
            honesty = 0, justice = 0, empathy = 0, responsibility = 0,
            patience = 0, courage = 0, wisdom = 0,
            crystals = 100, wisdomPoints = 0, virtueMedals = 0, giftBoxes = 1,
            avatarSkinTone = "default", avatarHairStyle = "default",
            avatarOutfit = "default", avatarAccessory = "none",
            spiritAnimal = "NONE", title = "Vicdan Öğrencisi",
            totalChoicesMade = 0, perfectScenarios = 0, regrettedChoices = 0,
            helpedFriends = 0, longestStreak = 0, playTimeMinutes = 0,
            currentStreak = 0, streakMultiplier = 1.0f, totalDaysPlayed = 0,
            createdAt = LocalDateTime.now(),
            lastLoginAt = LocalDateTime.now()
        )
        userDao.insertUser(newUser)
        return newUser
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userDao.updateUser(profile.toEntity())
    }

    suspend fun addExperience(userId: String, xp: Int) {
        userDao.addExperience(userId, xp)
        checkLevelUp(userId)
    }

    private suspend fun checkLevelUp(userId: String) {
        val user = userDao.getUser(userId) ?: return
        val newLevel = LevelSystem.getLevelFromXP(user.experiencePoints)

        if (newLevel > user.level) {
            val levelData = LevelSystem.levels.find { it.levelNumber == newLevel }
            levelData?.let {
                userDao.addCrystals(userId, it.rewards.crystals)
                // Seviye atlama bildirimi burada tetiklenecek
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun checkAndUpdateStreak(userId: String): StreakResult {
        val user = userDao.getUser(userId) ?: return StreakResult.NO_USER
        val now = LocalDateTime.now()
        val daysBetween = ChronoUnit.DAYS.between(user.lastLoginAt, now)

        return when {
            daysBetween == 0L -> StreakResult.SAME_DAY
            daysBetween == 1L -> {
                val newStreak = user.currentStreak + 1
                val multiplier = calculateStreakMultiplier(newStreak)
                userDao.updateStreak(userId, newStreak, multiplier)

                // Streak başarıları kontrol et
                if (newStreak > user.longestStreak) {
                    // En uzun streak güncelle
                }

                StreakResult.CONTINUED(newStreak, multiplier)
            }
            else -> {
                userDao.updateStreak(userId, 1, 1.0f)
                StreakResult.BROKEN
            }
        }
    }

    private fun calculateStreakMultiplier(streak: Int): Float {
        return when {
            streak >= 30 -> 3.0f
            streak >= 14 -> 2.5f
            streak >= 7 -> 2.0f
            streak >= 3 -> 1.5f
            else -> 1.0f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateConscienceProfile(userId: String, impact: ConscienceImpact) {
        with(impact) {
            if (honesty != 0) userDao.addHonesty(userId, honesty)
            if (justice != 0) userDao.addJustice(userId, justice)
            if (empathy != 0) userDao.addEmpathy(userId, empathy)
            if (responsibility != 0) userDao.addResponsibility(userId, responsibility)
            if (patience != 0) userDao.addPatience(userId, patience)
            if (courage != 0) userDao.addCourage(userId, courage)
            if (wisdom != 0) userDao.addWisdom(userId, wisdom)
        }

        checkBadgeProgress(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun checkBadgeProgress(userId: String) {
        val user = userDao.getUser(userId) ?: return
        val allBadges = badgeDao.getAllBadges()

        allBadges.forEach { badge ->
            val alreadyEarned = badgeDao.checkBadgeEarned(userId, badge.badgeId)
            if (alreadyEarned == null) {
                val earned = when (badge.requirementType) {
                    "CONSCIENCE_POINTS" -> {
                        val total = user.honesty + user.justice + user.empathy +
                                user.responsibility + user.patience + user.courage + user.wisdom
                        total >= badge.requirementThreshold
                    }
                    "HONESTY" -> user.honesty >= badge.requirementThreshold
                    "JUSTICE" -> user.justice >= badge.requirementThreshold
                    "EMPATHY" -> user.empathy >= badge.requirementThreshold
                    else -> false
                }

                if (earned) {
                    val userBadge = UserBadgeEntity(
                        userId = userId,
                        badgeId = badge.badgeId,
                        earnedAt = LocalDateTime.now(),
                        progress = 100
                    )
                    badgeDao.awardBadge(userBadge)

                    // Ödülleri ver
                    userDao.addCrystals(userId, badge.rewardCrystals)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateDailyQuests(userId: String): List<DailyQuest> {
        // Eski görevleri temizle
        questDao.deleteExpiredQuests(LocalDateTime.now())

        val quests = listOf(
            createDailyQuest(userId, "Senaryoyu Tamamla",
                "Bugün bir senaryo tamamla", QuestAction.COMPLETE_SCENARIO, 1, 50, 10, 20),
            createDailyQuest(userId, "Dürüst Ol",
                "3 dürüst seçim yap", QuestAction.MAKE_HONEST_CHOICE, 3, 30, 5, 15),
            createDailyQuest(userId, "Günlük Yaz",
                "Vicdanını bir günlük yazısıyla paylaş", QuestAction.WRITE_JOURNAL_ENTRY, 1, 40, 8, 10)
        )

        quests.forEach { questDao.insertQuest(it.toEntity(userId)) }
        return quests
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDailyQuest(
        userId: String, title: String, description: String,
        action: QuestAction, count: Int, crystals: Int, wisdom: Int, xp: Int
    ): DailyQuest {
        return DailyQuest(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            type = QuestType.DAILY,
            requirement = QuestRequirement(action, count),
            reward = QuestReward(crystals, wisdom, xp),
            expiresAt = LocalDateTime.now().plusDays(1),
            isCompleted = false,
            progress = 0
        )
    }
}

sealed class StreakResult {
    object NO_USER : StreakResult()
    object SAME_DAY : StreakResult()
    object BROKEN : StreakResult()
    data class CONTINUED(val streak: Int, val multiplier: Float) : StreakResult()
}

// Extension functions
fun UserEntity.toUserProfile(): UserProfile {
    return UserProfile(
        userId = userId,
        username = username,
        level = level,
        experiencePoints = experiencePoints,
        totalScenarios = totalScenarios,
        conscience = ConscienceProfile(honesty, justice, empathy, responsibility, patience, courage, wisdom),
        currency = UserCurrency(crystals, wisdomPoints, virtueMedals, giftBoxes),
        avatar = Avatar(avatarSkinTone, avatarHairStyle, avatarOutfit, avatarAccessory,
            SpiritAnimal.valueOf(spiritAnimal), title),
        statistics = UserStatistics(totalChoicesMade, perfectScenarios, regrettedChoices,
            helpedFriends, longestStreak, null, playTimeMinutes),
        streakData = StreakData(currentStreak, lastLoginAt, streakMultiplier, totalDaysPlayed),
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        username = username,
        level = level,
        experiencePoints = experiencePoints,
        totalScenarios = totalScenarios,
        honesty = conscience.honesty,
        justice = conscience.justice,
        empathy = conscience.empathy,
        responsibility = conscience.responsibility,
        patience = conscience.patience,
        courage = conscience.courage,
        wisdom = conscience.wisdom,
        crystals = currency.crystals,
        wisdomPoints = currency.wisdomPoints,
        virtueMedals = currency.virtueMedals,
        giftBoxes = currency.giftBoxes,
        avatarSkinTone = avatar.skinTone,
        avatarHairStyle = avatar.hairStyle,
        avatarOutfit = avatar.outfit,
        avatarAccessory = avatar.accessory,
        spiritAnimal = avatar.spiritAnimal.name,
        title = avatar.title,
        totalChoicesMade = statistics.totalChoicesMade,
        perfectScenarios = statistics.perfectScenarios,
        regrettedChoices = statistics.regrettedChoices,
        helpedFriends = statistics.helpedFriends,
        longestStreak = statistics.longestStreak,
        playTimeMinutes = statistics.playTimeMinutes,
        currentStreak = streakData.currentStreak,
        streakMultiplier = streakData.streakMultiplier,
        totalDaysPlayed = streakData.totalDaysPlayed,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
}

fun DailyQuest.toEntity(userId: String): DailyQuestEntity {
    return DailyQuestEntity(
        questId = id,
        userId = userId,
        title = title,
        description = description,
        type = type.name,
        action = requirement.action.name,
        requiredCount = requirement.count,
        currentProgress = progress,
        rewardCrystals = reward.crystals,
        rewardWisdomPoints = reward.wisdomPoints,
        rewardXP = reward.experiencePoints,
        expiresAt = expiresAt,
        isCompleted = isCompleted
    )
}