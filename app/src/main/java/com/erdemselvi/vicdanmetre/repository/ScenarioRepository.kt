package com.erdemselvi.vicdanmetre.repository

import android.content.Context
import com.erdemselvi.vicdanmetre.database.*
import com.erdemselvi.vicdanmetre.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScenarioRepository(
    private val scenarioDao: ScenarioDao,
    private val context: Context
) {

    data class ScenarioList(val scenarios: List<Scenario>)

    /**
     * JSON'dan senaryoları yükle ve database'e kaydet
     */
    suspend fun loadScenariosFromAssets() {
        withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("scenarios.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val type = object : TypeToken<ScenarioList>() {}.type
                val scenarioList: ScenarioList = gson.fromJson(json, type)

                // Database'e kaydet
                val entities = scenarioList.scenarios.map { it.toEntity() }
                scenarioDao.insertScenarios(entities)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Tüm senaryoları getir
     */
    suspend fun getAllScenarios(): List<Scenario> {
        return withContext(Dispatchers.IO) {
            scenarioDao.getAllScenarios().map { it.toScenario() }
        }
    }

    /**
     * Belirli bir senaryoyu getir
     */
    suspend fun getScenario(scenarioId: String): Scenario? {
        return withContext(Dispatchers.IO) {
            scenarioDao.getScenario(scenarioId)?.toScenario()
        }
    }

    /**
     * Kullanıcının seviyesine göre açık senaryoları getir
     */
    suspend fun getUnlockedScenarios(userLevel: Int): List<Scenario> {
        return withContext(Dispatchers.IO) {
            scenarioDao.getUnlockedScenarios(userLevel).map { it.toScenario() }
        }
    }

    /**
     * Kategoriye göre senaryoları getir
     */
    suspend fun getScenariosByCategory(category: String): List<Scenario> {
        return withContext(Dispatchers.IO) {
            scenarioDao.getScenariosByCategory(category).map { it.toScenario() }
        }
    }

    /**
     * Kullanıcının bir senaryodaki ilerlemesini kaydet
     */
    suspend fun saveProgress(userId: String, scenarioId: String, progress: UserScenarioProgressEntity) {
        withContext(Dispatchers.IO) {
            scenarioDao.saveProgress(progress)
        }
    }

    /**
     * Kullanıcının tamamladığı senaryo sayısını getir
     */
    suspend fun getCompletedCount(userId: String): Int {
        return withContext(Dispatchers.IO) {
            scenarioDao.getCompletedCount(userId)
        }
    }

    /**
     * Kullanıcının bir senaryoyu tamamlayıp tamamlamadığını kontrol et
     */
    suspend fun isScenarioCompleted(userId: String, scenarioId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val progress = scenarioDao.getUserProgress(userId, scenarioId)
            progress?.isCompleted ?: false
        }
    }
}

// Extension functions
fun Scenario.toEntity(): ScenarioEntity {
    val gson = Gson()
    return ScenarioEntity(
        scenarioId = id,
        title = title,
        category = category.name,
        difficulty = difficulty.name,
        requiredLevel = requiredLevel,
        estimatedTime = estimatedTime,
        totalChoices = totalChoices,
        unlockCost = unlockCost,
        thumbnailUrl = thumbnailUrl,
        chaptersJson = gson.toJson(chapters)
    )
}

fun ScenarioEntity.toScenario(): Scenario {
    val gson = Gson()
    val chapterType = object : TypeToken<List<Chapter>>() {}.type
    val chapters: List<Chapter> = gson.fromJson(chaptersJson, chapterType)

    return Scenario(
        id = scenarioId,
        title = title,
        category = ScenarioCategory.valueOf(category),
        difficulty = DifficultyLevel.valueOf(difficulty),
        requiredLevel = requiredLevel,
        estimatedTime = estimatedTime,
        chapters = chapters,
        totalChoices = totalChoices,
        unlockCost = unlockCost,
        isLocked = false,
        thumbnailUrl = thumbnailUrl
    )
}