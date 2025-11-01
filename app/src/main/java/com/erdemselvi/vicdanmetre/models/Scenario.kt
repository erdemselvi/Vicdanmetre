package com.erdemselvi.vicdanmetre.models

data class Scenario(
    val id: String,
    val title: String,
    val category: ScenarioCategory,
    val difficulty: DifficultyLevel,
    val requiredLevel: Int,
    val estimatedTime: Int, // dakika
    val chapters: List<Chapter>,
    val totalChoices: Int,
    val unlockCost: Int = 0,
    val isLocked: Boolean = false,
    val thumbnailUrl: String = ""
)

data class Chapter(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val description: String,
    val narrativeText: String,
    val imageUrl: String = "",
    val choices: List<Choice>,
    val nextChapterIds: Map<String, String> // choiceId -> nextChapterId
)

data class Choice(
    val id: String,
    val text: String,
    val conscienceImpact: ConscienceImpact,
    val immediateConsequence: String,
    val longTermEffect: String = "",
    val emotionalTone: EmotionalTone
)

data class ConscienceImpact(
    val honesty: Int = 0,        // Dürüstlük
    val justice: Int = 0,        // Adalet
    val empathy: Int = 0,        // Empati
    val responsibility: Int = 0, // Sorumluluk
    val patience: Int = 0,       // Sabır
    val courage: Int = 0,        // Cesaret
    val wisdom: Int = 0          // Hikmet
)

enum class ScenarioCategory(val displayName: String, val icon: String) {
    SCHOOL("Okul Hayatı", "🎓"),
    FAMILY("Aile", "👨‍👩‍👧"),
    FRIENDSHIP("Arkadaşlık", "🤝"),
    SOCIAL_MEDIA("Sosyal Medya", "📱"),
    MONEY("Para ve Sorumluluk", "💰"),
    SOCIETY("Toplum", "🏙️"),
    WORK("İş Hayatı", "💼"),
    ENVIRONMENT("Çevre", "🌍")
}

enum class DifficultyLevel(val displayName: String, val color: String) {
    EASY("Kolay", "#4CAF50"),
    MEDIUM("Orta", "#FF9800"),
    HARD("Zor", "#F44336"),
    EXPERT("Uzman", "#9C27B0")
}

enum class EmotionalTone {
    POSITIVE, NEGATIVE, NEUTRAL, CONFLICTED
}