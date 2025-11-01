package com.erdemselvi.vicdanmetre.database

import android.content.Context
import androidx.room.*
import java.time.LocalDateTime
import com.erdemselvi.vicdanmetre.models.*

@Database(
    entities = [
        UserEntity::class,
        ScenarioEntity::class,
        UserScenarioProgressEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class,
        JournalEntryEntity::class,
        DailyQuestEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ConscienceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scenarioDao(): ScenarioDao
    abstract fun badgeDao(): BadgeDao
    abstract fun journalDao(): JournalDao
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: ConscienceDatabase? = null

        fun getDatabase(context: Context): ConscienceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConscienceDatabase::class.java,
                    "conscience_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
// Type Converters
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, java.time.ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(java.time.ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}

// Entity Definitions

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val level: Int,
    val experiencePoints: Int,
    val totalScenarios: Int,

    // Conscience Profile
    val honesty: Int,
    val justice: Int,
    val empathy: Int,
    val responsibility: Int,
    val patience: Int,
    val courage: Int,
    val wisdom: Int,

    // Currency
    val crystals: Int,
    val wisdomPoints: Int,
    val virtueMedals: Int,
    val giftBoxes: Int,

    // Avatar
    val avatarSkinTone: String,
    val avatarHairStyle: String,
    val avatarOutfit: String,
    val avatarAccessory: String,
    val spiritAnimal: String,
    val title: String,

    // Statistics
    val totalChoicesMade: Int,
    val perfectScenarios: Int,
    val regrettedChoices: Int,
    val helpedFriends: Int,
    val longestStreak: Int,
    val playTimeMinutes: Int,

    // Streak
    val currentStreak: Int,
    val streakMultiplier: Float,
    val totalDaysPlayed: Int,

    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime
)

@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey val scenarioId: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val requiredLevel: Int,
    val estimatedTime: Int,
    val totalChoices: Int,
    val unlockCost: Int,
    val thumbnailUrl: String,
    val chaptersJson: String // JSON string of chapters
)

@Entity(
    tableName = "user_scenario_progress",
    primaryKeys = ["userId", "scenarioId"]
)
data class UserScenarioProgressEntity(
    val userId: String,
    val scenarioId: String,
    val isCompleted: Boolean,
    val currentChapter: Int,
    val choicesMade: String, // comma-separated choice IDs
    val conscienceImpact: String, // JSON of ConscienceImpact
    val completedAt: LocalDateTime?,
    val playCount: Int,
    val bestScore: Int
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val badgeId: String,
    val name: String,
    val description: String,
    val category: String,
    val rarity: String,
    val iconUrl: String,
    val requirementType: String,
    val requirementThreshold: Int,
    val rewardCrystals: Int,
    val rewardWisdomPoints: Int,
    val specialTitle: String?
)

@Entity(
    tableName = "user_badges",
    primaryKeys = ["userId", "badgeId"]
)
data class UserBadgeEntity(
    val userId: String,
    val badgeId: String,
    val earnedAt: LocalDateTime,
    val progress: Int
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val entryId: String,
    val userId: String,
    val scenarioId: String,
    val scenarioTitle: String,
    val choicesMade: String, // comma-separated
    val reflection: String,
    val emotion: String,
    val regretLevel: Int,
    val createdAt: LocalDateTime,
    val isPrivate: Boolean
)

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val questId: String,
    val userId: String,
    val title: String,
    val description: String,
    val type: String,
    val action: String,
    val requiredCount: Int,
    val currentProgress: Int,
    val rewardCrystals: Int,
    val rewardWisdomPoints: Int,
    val rewardXP: Int,
    val expiresAt: LocalDateTime,
    val isCompleted: Boolean
)

// DAOs

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET experiencePoints = experiencePoints + :xp WHERE userId = :userId")
    suspend fun addExperience(userId: String, xp: Int)

    @Query("UPDATE users SET crystals = crystals + :amount WHERE userId = :userId")
    suspend fun addCrystals(userId: String, amount: Int)

    @Query("UPDATE users SET currentStreak = :streak, streakMultiplier = :multiplier WHERE userId = :userId")
    suspend fun updateStreak(userId: String, streak: Int, multiplier: Float)

    @Query("UPDATE users SET honesty = honesty + :value WHERE userId = :userId")
    suspend fun addHonesty(userId: String, value: Int)

    @Query("UPDATE users SET justice = justice + :value WHERE userId = :userId")
    suspend fun addJustice(userId: String, value: Int)

    @Query("UPDATE users SET empathy = empathy + :value WHERE userId = :userId")
    suspend fun addEmpathy(userId: String, value: Int)

    @Query("UPDATE users SET responsibility = responsibility + :value WHERE userId = :userId")
    suspend fun addResponsibility(userId: String, value: Int)

    @Query("UPDATE users SET patience = patience + :value WHERE userId = :userId")
    suspend fun addPatience(userId: String, value: Int)

    @Query("UPDATE users SET courage = courage + :value WHERE userId = :userId")
    suspend fun addCourage(userId: String, value: Int)

    @Query("UPDATE users SET wisdom = wisdom + :value WHERE userId = :userId")
    suspend fun addWisdom(userId: String, value: Int)
}

@Dao
interface ScenarioDao {
    @Query("SELECT * FROM scenarios")
    suspend fun getAllScenarios(): List<ScenarioEntity>

    @Query("SELECT * FROM scenarios WHERE scenarioId = :scenarioId")
    suspend fun getScenario(scenarioId: String): ScenarioEntity?

    @Query("SELECT * FROM scenarios WHERE category = :category")
    suspend fun getScenariosByCategory(category: String): List<ScenarioEntity>

    @Query("SELECT * FROM scenarios WHERE requiredLevel <= :level")
    suspend fun getUnlockedScenarios(level: Int): List<ScenarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenario: ScenarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<ScenarioEntity>)

    @Query("SELECT * FROM user_scenario_progress WHERE userId = :userId AND scenarioId = :scenarioId")
    suspend fun getUserProgress(userId: String, scenarioId: String): UserScenarioProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserScenarioProgressEntity)

    @Query("SELECT COUNT(*) FROM user_scenario_progress WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedCount(userId: String): Int
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    suspend fun getAllBadges(): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE badgeId = :badgeId")
    suspend fun getBadge(badgeId: String): BadgeEntity?

    @Query("SELECT b.* FROM badges b INNER JOIN user_badges ub ON b.badgeId = ub.badgeId WHERE ub.userId = :userId")
    suspend fun getUserBadges(userId: String): List<BadgeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun awardBadge(userBadge: UserBadgeEntity)

    @Query("SELECT * FROM user_badges WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun checkBadgeEarned(userId: String, badgeId: String): UserBadgeEntity?
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserJournalEntries(userId: String): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND scenarioId = :scenarioId")
    suspend fun getEntriesForScenario(userId: String, scenarioId: String): List<JournalEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE entryId = :entryId")
    suspend fun deleteEntry(entryId: String)

    @Query("SELECT COUNT(*) FROM journal_entries WHERE userId = :userId")
    suspend fun getJournalCount(userId: String): Int
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM daily_quests WHERE userId = :userId AND isCompleted = 0")
    suspend fun getActiveQuests(userId: String): List<DailyQuestEntity>

    @Query("SELECT * FROM daily_quests WHERE userId = :userId AND type = 'DAILY' AND isCompleted = 0")
    suspend fun getDailyQuests(userId: String): List<DailyQuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: DailyQuestEntity)

    @Update
    suspend fun updateQuest(quest: DailyQuestEntity)

    @Query("DELETE FROM daily_quests WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredQuests(currentTime: LocalDateTime)

    @Query("UPDATE daily_quests SET currentProgress = currentProgress + 1 WHERE questId = :questId")
    suspend fun incrementProgress(questId: String)
}