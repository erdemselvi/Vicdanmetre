package com.erdemselvi.vicdanmetre.repository

import com.erdemselvi.vicdanmetre.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val SCENARIOS_PROGRESS = "scenarios_progress"
        private const val JOURNAL_ENTRIES = "journal_entries"
        private const val BADGES_COLLECTION = "user_badges"
    }

    /**
     * Kullanıcı ID'sini al veya anonim kullanıcı oluştur
     */
    suspend fun getCurrentUserId(): String {
        var currentUser = auth.currentUser

        if (currentUser == null) {
            // Anonim giriş yap
            auth.signInAnonymously().await()
            currentUser = auth.currentUser
        }

        return currentUser?.uid ?: throw Exception("Kullanıcı oluşturulamadı")
    }

    /**
     * Kullanıcı profilini Firebase'den al
     */
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val doc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                val data = doc.data ?: return null
                UserProfile(
                    userId = userId,
                    username = data["username"] as? String ?: "Oyuncu",
                    level = (data["level"] as? Long)?.toInt() ?: 1,
                    experiencePoints = (data["experiencePoints"] as? Long)?.toInt() ?: 0,
                    totalScenarios = (data["totalScenarios"] as? Long)?.toInt() ?: 0,
                    conscience = ConscienceProfile(
                        honesty = (data["honesty"] as? Long)?.toInt() ?: 0,
                        justice = (data["justice"] as? Long)?.toInt() ?: 0,
                        empathy = (data["empathy"] as? Long)?.toInt() ?: 0,
                        responsibility = (data["responsibility"] as? Long)?.toInt() ?: 0,
                        patience = (data["patience"] as? Long)?.toInt() ?: 0,
                        courage = (data["courage"] as? Long)?.toInt() ?: 0,
                        wisdom = (data["wisdom"] as? Long)?.toInt() ?: 0
                    ),
                    currency = UserCurrency(
                        crystals = (data["crystals"] as? Long)?.toInt() ?: 100,
                        wisdomPoints = (data["wisdomPoints"] as? Long)?.toInt() ?: 0,
                        virtueMedals = (data["virtueMedals"] as? Long)?.toInt() ?: 0,
                        giftBoxes = (data["giftBoxes"] as? Long)?.toInt() ?: 0
                    ),
                    avatar = Avatar(
                        skinTone = data["skinTone"] as? String ?: "default",
                        hairStyle = data["hairStyle"] as? String ?: "default",
                        outfit = data["outfit"] as? String ?: "default",
                        accessory = data["accessory"] as? String ?: "none",
                        spiritAnimal = SpiritAnimal.NONE,
                        title = data["title"] as? String ?: "Vicdan Öğrencisi"
                    ),
                    statistics = UserStatistics(
                        totalChoicesMade = (data["totalChoicesMade"] as? Long)?.toInt() ?: 0,
                        perfectScenarios = (data["perfectScenarios"] as? Long)?.toInt() ?: 0,
                        regrettedChoices = (data["regrettedChoices"] as? Long)?.toInt() ?: 0,
                        helpedFriends = (data["helpedFriends"] as? Long)?.toInt() ?: 0,
                        longestStreak = (data["longestStreak"] as? Long)?.toInt() ?: 0,
                        favoriteCategory = null,
                        playTimeMinutes = (data["playTimeMinutes"] as? Long)?.toInt() ?: 0
                    ),
                    streakData = StreakData(
                        currentStreak = (data["currentStreak"] as? Long)?.toInt() ?: 0,
                        lastLoginDate = LocalDateTime.now(),
                        streakMultiplier = (data["streakMultiplier"] as? Double)?.toFloat() ?: 1.0f,
                        totalDaysPlayed = (data["totalDaysPlayed"] as? Long)?.toInt() ?: 0
                    ),
                    createdAt = LocalDateTime.now(),
                    lastLoginAt = LocalDateTime.now()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Yeni kullanıcı profili oluştur
     */
    suspend fun createUserProfile(userId: String): UserProfile {
        val newProfile = UserProfile(
            userId = userId,
            username = "Oyuncu${userId.take(6)}",
            level = 1,
            experiencePoints = 0,
            totalScenarios = 0,
            conscience = ConscienceProfile(),
            currency = UserCurrency(crystals = 100),
            avatar = Avatar(),
            statistics = UserStatistics(),
            streakData = StreakData(),
            createdAt = LocalDateTime.now(),
            lastLoginAt = LocalDateTime.now()
        )

        saveUserProfile(newProfile)
        return newProfile
    }

    /**
     * Kullanıcı profilini kaydet
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            val data = hashMapOf(
                "username" to profile.username,
                "level" to profile.level,
                "experiencePoints" to profile.experiencePoints,
                "totalScenarios" to profile.totalScenarios,

                // Conscience
                "honesty" to profile.conscience.honesty,
                "justice" to profile.conscience.justice,
                "empathy" to profile.conscience.empathy,
                "responsibility" to profile.conscience.responsibility,
                "patience" to profile.conscience.patience,
                "courage" to profile.conscience.courage,
                "wisdom" to profile.conscience.wisdom,

                // Currency
                "crystals" to profile.currency.crystals,
                "wisdomPoints" to profile.currency.wisdomPoints,
                "virtueMedals" to profile.currency.virtueMedals,
                "giftBoxes" to profile.currency.giftBoxes,

                // Avatar
                "skinTone" to profile.avatar.skinTone,
                "hairStyle" to profile.avatar.hairStyle,
                "outfit" to profile.avatar.outfit,
                "accessory" to profile.avatar.accessory,
                "title" to profile.avatar.title,

                // Statistics
                "totalChoicesMade" to profile.statistics.totalChoicesMade,
                "perfectScenarios" to profile.statistics.perfectScenarios,
                "regrettedChoices" to profile.statistics.regrettedChoices,
                "helpedFriends" to profile.statistics.helpedFriends,
                "longestStreak" to profile.statistics.longestStreak,
                "playTimeMinutes" to profile.statistics.playTimeMinutes,

                // Streak
                "currentStreak" to profile.streakData.currentStreak,
                "streakMultiplier" to profile.streakData.streakMultiplier,
                "totalDaysPlayed" to profile.streakData.totalDaysPlayed,

                "lastUpdated" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(profile.userId)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * XP ekle
     */
    suspend fun addExperience(userId: String, xp: Int) {
        try {
            val userRef = firestore.collection(USERS_COLLECTION).document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentXP = (snapshot.getLong("experiencePoints") ?: 0).toInt()
                val newXP = currentXP + xp

                // Seviye kontrolü
                val currentLevel = (snapshot.getLong("level") ?: 1).toInt()
                val newLevel = calculateLevel(newXP)

                transaction.update(userRef, mapOf(
                    "experiencePoints" to newXP,
                    "level" to newLevel
                ))
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Vicdan özelliklerini güncelle
     */
    suspend fun updateConscienceProfile(userId: String, impact: ConscienceImpact) {
        try {
            val userRef = firestore.collection(USERS_COLLECTION).document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                val updates = mutableMapOf<String, Any>()

                if (impact.honesty != 0) {
                    val current = (snapshot.getLong("honesty") ?: 0).toInt()
                    updates["honesty"] = (current + impact.honesty).coerceAtLeast(0)
                }
                if (impact.justice != 0) {
                    val current = (snapshot.getLong("justice") ?: 0).toInt()
                    updates["justice"] = (current + impact.justice).coerceAtLeast(0)
                }
                if (impact.empathy != 0) {
                    val current = (snapshot.getLong("empathy") ?: 0).toInt()
                    updates["empathy"] = (current + impact.empathy).coerceAtLeast(0)
                }
                if (impact.responsibility != 0) {
                    val current = (snapshot.getLong("responsibility") ?: 0).toInt()
                    updates["responsibility"] = (current + impact.responsibility).coerceAtLeast(0)
                }
                if (impact.patience != 0) {
                    val current = (snapshot.getLong("patience") ?: 0).toInt()
                    updates["patience"] = (current + impact.patience).coerceAtLeast(0)
                }
                if (impact.courage != 0) {
                    val current = (snapshot.getLong("courage") ?: 0).toInt()
                    updates["courage"] = (current + impact.courage).coerceAtLeast(0)
                }
                if (impact.wisdom != 0) {
                    val current = (snapshot.getLong("wisdom") ?: 0).toInt()
                    updates["wisdom"] = (current + impact.wisdom).coerceAtLeast(0)
                }

                if (updates.isNotEmpty()) {
                    transaction.update(userRef, updates)
                }
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Senaryo ilerleme durumunu kaydet
     */
    suspend fun saveScenarioProgress(userId: String, scenarioId: String, isCompleted: Boolean) {
        try {
            val data = hashMapOf(
                "scenarioId" to scenarioId,
                "isCompleted" to isCompleted,
                "completedAt" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SCENARIOS_PROGRESS)
                .document(scenarioId)
                .set(data)
                .await()

            // Tamamlanan senaryo sayısını güncelle
            if (isCompleted) {
                val userRef = firestore.collection(USERS_COLLECTION).document(userId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val current = (snapshot.getLong("totalScenarios") ?: 0).toInt()
                    transaction.update(userRef, "totalScenarios", current + 1)
                }.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Senaryo tamamlanmış mı kontrol et
     */
    suspend fun isScenarioCompleted(userId: String, scenarioId: String): Boolean {
        return try {
            val doc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SCENARIOS_PROGRESS)
                .document(scenarioId)
                .get()
                .await()

            doc.getBoolean("isCompleted") ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Günlük entry kaydet
     */
    suspend fun saveJournalEntry(userId: String, entry: JournalEntry) {
        try {
            val data = hashMapOf(
                "scenarioId" to entry.scenarioId,
                "scenarioTitle" to entry.scenarioTitle,
                "reflection" to entry.reflection,
                "emotion" to entry.emotion.name,
                "regretLevel" to entry.regretLevel,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(JOURNAL_ENTRIES)
                .document(entry.id)
                .set(data)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Streak güncelle
     */
    suspend fun updateStreak(userId: String) {
        try {
            val userRef = firestore.collection(USERS_COLLECTION).document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentStreak = (snapshot.getLong("currentStreak") ?: 0).toInt()
                val longestStreak = (snapshot.getLong("longestStreak") ?: 0).toInt()

                val newStreak = currentStreak + 1
                val newLongest = maxOf(newStreak, longestStreak)
                val multiplier = calculateStreakMultiplier(newStreak)

                transaction.update(userRef, mapOf(
                    "currentStreak" to newStreak,
                    "longestStreak" to newLongest,
                    "streakMultiplier" to multiplier,
                    "totalDaysPlayed" to (snapshot.getLong("totalDaysPlayed") ?: 0) + 1
                ))
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Helper functions
    private fun calculateLevel(xp: Int): Int {
        return when {
            xp < 100 -> 1
            xp < 250 -> 2
            xp < 500 -> 3
            xp < 1000 -> 4
            xp < 1500 -> 5
            xp < 2500 -> 6
            xp < 4000 -> 7
            xp < 6000 -> 8
            xp < 9000 -> 9
            xp < 13000 -> 10
            else -> 10 + (xp - 13000) / 2000
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
}