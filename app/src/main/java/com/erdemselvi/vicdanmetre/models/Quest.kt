package com.erdemselvi.vicdanmetre.models

import java.time.LocalDateTime

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val requirement: QuestRequirement,
    val reward: QuestReward,
    val expiresAt: LocalDateTime,
    val isCompleted: Boolean = false,
    val progress: Int = 0
)

enum class QuestType {
    DAILY,
    WEEKLY,
    SPECIAL_EVENT,
    ACHIEVEMENT
}

data class QuestRequirement(
    val action: QuestAction,
    val count: Int,
    val specificTarget: String? = null
)

enum class QuestAction {
    COMPLETE_SCENARIO,
    MAKE_HONEST_CHOICE,
    WRITE_JOURNAL_ENTRY,
    HELP_FRIEND,
    LOGIN_CONSECUTIVE_DAYS,
    VOTE_IN_COUNCIL,
    EARN_BADGE
}

data class QuestReward(
    val crystals: Int = 0,
    val wisdomPoints: Int = 0,
    val experiencePoints: Int = 0,
    val giftBox: Boolean = false
)