// ui/screens/ScenarioScreen.kt - JSON ile Çalışan Versiyon
package com.erdemselvi.vicdanmetre.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdemselvi.vicdanmetre.models.*
import com.erdemselvi.vicdanmetre.utils.ScenarioLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScenarioScreen(
    navController: NavController,
    scenarioId: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scenario by remember { mutableStateOf<Scenario?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var currentChapterIndex by remember { mutableStateOf(0) }
    var showChoiceResult by remember { mutableStateOf(false) }
    var selectedChoice by remember { mutableStateOf<Choice?>(null) }
    var totalConscienceImpact by remember { mutableStateOf(ConscienceImpact()) }
    var choiceHistory by remember { mutableStateOf<List<String>>(emptyList()) }

    // JSON'dan senaryoyu yükle
    LaunchedEffect(scenarioId) {
        isLoading = true
        scenario = scenarioId?.let {
            ScenarioLoader.loadScenarioById(context, it)
        }
        isLoading = false
    }

    val currentChapter = scenario?.chapters?.getOrNull(currentChapterIndex)
    val isLastChapter = currentChapter?.choices?.isEmpty() ?: true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scenario?.title ?: "Yükleniyor...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                actions = {
                    if (scenario != null) {
                        Text(
                            "${currentChapterIndex + 1}/${scenario!!.chapters.size}",
                            modifier = Modifier.padding(end = 16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    // Yükleniyor ekranı
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Senaryo yükleniyor...")
                        }
                    }
                }
                scenario == null -> {
                    // Senaryo bulunamadı
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("❌", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Senaryo bulunamadı",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "ID: $scenarioId",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Geri Dön")
                            }
                        }
                    }
                }
                isLastChapter -> {
                    // Senaryo sonu ekranı
                    ScenarioEndScreen(
                        scenario = scenario!!,
                        totalImpact = totalConscienceImpact,
                        choiceHistory = choiceHistory,
                        onBackToHome = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onPlayAgain = {
                            currentChapterIndex = 0
                            totalConscienceImpact = ConscienceImpact()
                            choiceHistory = emptyList()
                        }
                    )
                }
                currentChapter != null -> {
                    // Normal chapter ekranı
                    AnimatedContent(
                        targetState = currentChapterIndex,
                        transitionSpec = {
                            (slideInHorizontally { it } + fadeIn()) with
                                    (slideOutHorizontally { -it } + fadeOut())
                        }
                    ) { index ->
                        ChapterContent(
                            chapter = scenario!!.chapters[index],
                            onChoiceSelected = { choice ->
                                selectedChoice = choice
                                choiceHistory = choiceHistory + choice.text
                                totalConscienceImpact = totalConscienceImpact.add(choice.conscienceImpact)
                                showChoiceResult = true
                            }
                        )
                    }

                    // Choice Result Dialog
                    if (showChoiceResult && selectedChoice != null && currentChapter != null) {
                        ChoiceResultDialog(
                            choice = selectedChoice!!,
                            onContinue = {
                                showChoiceResult = false

                                // Sonraki bölümü bul (dallanma sistemi)
                                val nextChapterId = currentChapter.nextChapterIds[selectedChoice!!.id]

                                if (nextChapterId != null) {
                                    val nextIndex = scenario!!.chapters.indexOfFirst {
                                        it.id == nextChapterId
                                    }

                                    currentChapterIndex = if (nextIndex != -1) {
                                        nextIndex
                                    } else {
                                        scenario!!.chapters.size - 1
                                    }
                                } else {
                                    // Son bölüme git
                                    currentChapterIndex = scenario!!.chapters.size - 1
                                }

                                selectedChoice = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterContent(
    chapter: Chapter,
    onChoiceSelected: (Choice) -> Unit
) {
    var textVisible by remember { mutableStateOf(false) }

    LaunchedEffect(chapter.id) {
        textVisible = false
        delay(200)
        textVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chapter Title
        AnimatedVisibility(
            visible = textVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Text(
                text = chapter.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chapter visual representation
        AnimatedVisibility(
            visible = textVisible,
            enter = fadeIn() + scaleIn()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    getCategoryIcon(chapter.chapterNumber),
                    fontSize = 80.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Narrative Text
        AnimatedVisibility(
            visible = textVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = chapter.narrativeText,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Choices
        if (chapter.choices.isNotEmpty()) {
            Text(
                "Ne yaparsın?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            chapter.choices.forEachIndexed { index, choice ->
                AnimatedVisibility(
                    visible = textVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 100 * index
                        )
                    ) + slideInHorizontally()
                ) {
                    ChoiceCard(
                        choice = choice,
                        index = index,
                        onClick = { onChoiceSelected(choice) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ChoiceCard(
    choice: Choice,
    index: Int,
    onClick: () -> Unit
) {
    val colors = listOf(
        Color(0xFF6C63FF),
        Color(0xFF03DAC6),
        Color(0xFFFF6584),
        Color(0xFFFFA726)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Choice Letter
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors[index % colors.size]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ('A' + index).toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Choice Text
            Text(
                text = choice.text,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                lineHeight = 22.sp
            )

            // Emotional Tone
            Text(
                text = getEmotionEmoji(choice.emotionalTone),
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun ChoiceResultDialog(
    choice: Choice,
    onContinue: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    getEmotionEmoji(choice.emotionalTone),
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    "Seçiminin Sonucu",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    choice.immediateConsequence,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                if (choice.longTermEffect.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Uzun Vadeli Etki:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        choice.longTermEffect,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Conscience Impact
                Text(
                    "Vicdan Etkisi:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                ConscienceImpactDisplay(choice.conscienceImpact)
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Devam Et", fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun ConscienceImpactDisplay(impact: ConscienceImpact) {
    val impacts = listOf(
        "Dürüstlük" to impact.honesty,
        "Adalet" to impact.justice,
        "Empati" to impact.empathy,
        "Sorumluluk" to impact.responsibility,
        "Sabır" to impact.patience,
        "Cesaret" to impact.courage,
        "Hikmet" to impact.wisdom
    ).filter { it.second != 0 }

    if (impacts.isEmpty()) {
        Text(
            "Bu seçimin vicdan üzerinde önemli bir etkisi yok.",
            fontSize = 13.sp,
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    } else {
        Column {
            impacts.forEach { (trait, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        trait,
                        fontSize = 14.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (value > 0) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "+$value",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "$value",
                                color = Color(0xFFF44336),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScenarioEndScreen(
    scenario: Scenario,
    totalImpact: ConscienceImpact,
    choiceHistory: List<String>,
    onBackToHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Text(
                "🎉",
                fontSize = (100 * scale).sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Senaryo Tamamlandı!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                scenario.title,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Results Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        "📊 Toplam Vicdan Etkisi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ConscienceImpactDisplay(totalImpact)

                    Spacer(modifier = Modifier.height(20.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "📝 Verdiğin Kararlar",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    choiceHistory.forEachIndexed { index, choice ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "${index + 1}.",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(
                                choice,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ana Sayfaya Dön", fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tekrar Oyna", fontSize = 17.sp)
            }
        }
    }
}

// Helper Functions
fun getCategoryIcon(chapterNumber: Int): String {
    val icons = listOf("📖", "🤔", "💭", "⚖️", "❤️", "🎯", "✨", "🌟")
    return icons[chapterNumber % icons.size]
}

fun getEmotionEmoji(tone: EmotionalTone): String {
    return when (tone) {
        EmotionalTone.POSITIVE -> "😊"
        EmotionalTone.NEGATIVE -> "😔"
        EmotionalTone.NEUTRAL -> "😐"
        EmotionalTone.CONFLICTED -> "😕"
    }
}

// Extension function
fun ConscienceImpact.add(other: ConscienceImpact): ConscienceImpact {
    return ConscienceImpact(
        honesty = this.honesty + other.honesty,
        justice = this.justice + other.justice,
        empathy = this.empathy + other.empathy,
        responsibility = this.responsibility + other.responsibility,
        patience = this.patience + other.patience,
        courage = this.courage + other.courage,
        wisdom = this.wisdom + other.wisdom
    )
}