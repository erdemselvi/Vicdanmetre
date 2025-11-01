// ui/screens/HomeScreen.kt
package com.erdemselvi.vicdanmetre.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.erdemselvi.vicdanmetre.models.*
import com.erdemselvi.vicdanmetre.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel()
) {
    val selectedTab = remember { mutableStateOf(0) }

    // ViewModel'den gerçek veriler
    val userProfile by viewModel.userProfile.collectAsState()
    val scenarios by viewModel.scenarios.collectAsState()
    val completedScenarios by viewModel.completedScenarios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Kullanıcı verileri
    val userLevel = userProfile?.level ?: 1
    val userXP = userProfile?.experiencePoints ?: 0
    val progressToNextLevel = viewModel.getProgressToNextLevel()
    val currentStreak = userProfile?.streakData?.currentStreak ?: 0
    val streakMultiplier = userProfile?.streakData?.streakMultiplier ?: 1.0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("VİCDANIM")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Seviye $userLevel",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = progressToNextLevel,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, "Profil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Ana Sayfa") },
                    label = { Text("Ana Sayfa") },
                    selected = selectedTab.value == 0,
                    onClick = { selectedTab.value = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, "Rozetler") },
                    label = { Text("Rozetler") },
                    selected = selectedTab.value == 1,
                    onClick = { navController.navigate("badges") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Leaderboard, "Lider") },
                    label = { Text("Lider") },
                    selected = selectedTab.value == 2,
                    onClick = { navController.navigate("leaderboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, "Günlük") },
                    label = { Text("Günlük") },
                    selected = selectedTab.value == 3,
                    onClick = { navController.navigate("journal") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak Card - Gerçek veri
            item {
                StreakCard(
                    currentStreak = currentStreak,
                    multiplier = streakMultiplier
                )
            }

            // Senaryolar başlığı
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Senaryolar",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate("scenarios_list") }) {
                            Text("Tümü")
                        }
                    }
                }
            }

            // Senaryolar - Gerçek veri
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ScenarioStoriesRow(
                        scenarios = scenarios.take(8),
                        userLevel = userLevel,
                        completedScenarios = completedScenarios,
                        onScenarioClick = { scenario ->
                            if (userLevel >= scenario.requiredLevel) {
                                navController.navigate("scenario/${scenario.id}")
                            }
                        }
                    )
                }
            }

            // Günlük Görevler
            item {
                Text(
                    "Günlük Görevler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                DailyQuestsSection(
                    completedScenarios = completedScenarios.size,
                    totalChoices = userProfile?.statistics?.totalChoicesMade ?: 0
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ScenarioStoriesRow(
    scenarios: List<Scenario>,
    userLevel: Int,
    completedScenarios: Set<String>,
    onScenarioClick: (Scenario) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(scenarios) { scenario ->
            ScenarioStoryItem(
                scenario = scenario,
                userLevel = userLevel,
                isCompleted = completedScenarios.contains(scenario.id),
                onClick = { onScenarioClick(scenario) }
            )
        }
    }
}

@Composable
fun ScenarioStoryItem(
    scenario: Scenario,
    userLevel: Int,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val isUnlocked = userLevel >= scenario.requiredLevel

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .clickable(enabled = isUnlocked) { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Story ring
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            if (isCompleted) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50),
                                        Color(0xFF8BC34A)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B),
                                        Color(0xFFFFD93D),
                                        Color(0xFF6BCF7F)
                                    )
                                )
                            }
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color.Gray, Color.DarkGray)
                            )
                        }
                    )
            )

            // Inner circle
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(scenario.category.icon, fontSize = 40.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Kilitli",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Lv.${scenario.requiredLevel}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Completed checkmark
            if (isCompleted && isUnlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Tamamlandı",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            scenario.title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
        )

        // Difficulty badge
        Text(
            scenario.difficulty.displayName,
            fontSize = 10.sp,
            color = Color(scenario.difficulty.color.removePrefix("#").toLong(16) or 0xFF000000),
            modifier = Modifier
                .background(
                    Color(scenario.difficulty.color.removePrefix("#").toLong(16) or 0xFF000000).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StreakCard(currentStreak: Int, multiplier: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "🔥 Günlük Seri",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$currentStreak Gün",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Çarpan", fontSize = 12.sp)
                Text(
                    "x${multiplier}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun DailyQuestsSection(completedScenarios: Int, totalChoices: Int) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuestCard(
            "Senaryo Tamamla",
            "$completedScenarios/1",
            (completedScenarios / 1f).coerceAtMost(1f),
            completedScenarios >= 1
        )
        QuestCard(
            "Dürüst Seçim Yap",
            "${(totalChoices / 3)}/3",
            ((totalChoices / 3) / 3f).coerceAtMost(1f),
            (totalChoices / 3) >= 3
        )
        QuestCard(
            "Günlük Yaz",
            "0/1",
            0f,
            false
        )
    }
}

@Composable
fun QuestCard(title: String, progress: String, progressValue: Float, isCompleted: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progressValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (isCompleted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Tamamlandı",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    progress,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}