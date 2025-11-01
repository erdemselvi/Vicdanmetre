package com.erdemselvi.vicdanmetre.utils

import android.content.Context
import com.erdemselvi.vicdanmetre.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ScenarioLoader {

    data class ScenarioListWrapper(val scenarios: List<ScenarioJson>)

    data class ScenarioJson(
        val id: String,
        val title: String,
        val category: String,
        val difficulty: String,
        val requiredLevel: Int,
        val estimatedTime: Int,
        val totalChoices: Int,
        val chapters: List<ChapterJson>
    )

    data class ChapterJson(
        val id: String,
        val chapterNumber: Int,
        val title: String,
        val description: String,
        val narrativeText: String,
        val choices: List<ChoiceJson>,
        val nextChapterIds: Map<String, String>
    )

    data class ChoiceJson(
        val id: String,
        val text: String,
        val conscienceImpact: ConscienceImpactJson,
        val immediateConsequence: String,
        val longTermEffect: String = "",
        val emotionalTone: String
    )

    data class ConscienceImpactJson(
        val honesty: Int = 0,
        val justice: Int = 0,
        val empathy: Int = 0,
        val responsibility: Int = 0,
        val patience: Int = 0,
        val courage: Int = 0,
        val wisdom: Int = 0
    )

    /**
     * JSON'dan tüm senaryoları yükle
     */
    suspend fun loadScenariosFromAssets(context: Context): List<Scenario> {
        return withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("scenarios.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val type = object : TypeToken<ScenarioListWrapper>() {}.type
                val wrapper: ScenarioListWrapper = gson.fromJson(json, type)

                // JSON'dan model'e dönüştür
                wrapper.scenarios.map { it.toScenario() }

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * Belirli bir senaryoyu ID'ye göre yükle
     */
    suspend fun loadScenarioById(context: Context, scenarioId: String): Scenario? {
        val scenarios = loadScenariosFromAssets(context)
        return scenarios.find { it.id == scenarioId }
    }

    // Extension functions - JSON'dan Model'e dönüşüm
    private fun ScenarioJson.toScenario(): Scenario {
        return Scenario(
            id = id,
            title = title,
            category = ScenarioCategory.valueOf(category),
            difficulty = DifficultyLevel.valueOf(difficulty),
            requiredLevel = requiredLevel,
            estimatedTime = estimatedTime,
            chapters = chapters.map { it.toChapter() },
            totalChoices = totalChoices,
            unlockCost = 0,
            isLocked = false,
            thumbnailUrl = ""
        )
    }

    private fun ChapterJson.toChapter(): Chapter {
        return Chapter(
            id = id,
            chapterNumber = chapterNumber,
            title = title,
            description = description,
            narrativeText = narrativeText,
            imageUrl = "",
            choices = choices.map { it.toChoice() },
            nextChapterIds = nextChapterIds
        )
    }

    private fun ChoiceJson.toChoice(): Choice {
        return Choice(
            id = id,
            text = text,
            conscienceImpact = conscienceImpact.toConscienceImpact(),
            immediateConsequence = immediateConsequence,
            longTermEffect = longTermEffect,
            emotionalTone = EmotionalTone.valueOf(emotionalTone)
        )
    }

    private fun ConscienceImpactJson.toConscienceImpact(): ConscienceImpact {
        return ConscienceImpact(
            honesty = honesty,
            justice = justice,
            empathy = empathy,
            responsibility = responsibility,
            patience = patience,
            courage = courage,
            wisdom = wisdom
        )
    }
}

// ScenarioScreen.kt içinde kullanım için yardımcı fonksiyon
suspend fun loadScenarioFromJson(context: Context, scenarioId: String): Scenario? {
    return ScenarioLoader.loadScenarioById(context, scenarioId)
}