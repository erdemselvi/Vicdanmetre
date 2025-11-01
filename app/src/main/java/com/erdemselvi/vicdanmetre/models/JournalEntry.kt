package com.erdemselvi.vicdanmetre.models

import java.time.LocalDateTime

data class JournalEntry(
    val id: String,
    val userId: String,
    val scenarioId: String,
    val scenarioTitle: String,
    val choicesMade: List<String>,
    val reflection: String,
    val emotion: JournalEmotion,
    val regretLevel: Int = 0, // 0-5
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isPrivate: Boolean = true
)

enum class JournalEmotion(val emoji: String) {
    SATISFIED("😊"),
    PROUD("😌"),
    CONFLICTED("😕"),
    REGRETFUL("😔"),
    PEACEFUL("☮️"),
    THOUGHTFUL("🤔")
}
