// ui/screens/ScenariosListScreen.kt
package com.erdemselvi.vicdanmetre.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdemselvi.vicdanmetre.models.*
import com.erdemselvi.vicdanmetre.utils.ScenarioLoader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosListScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scenarios by remember { mutableStateOf<List<Scenario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<ScenarioCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<DifficultyLevel?>(null) }

    // Mock user level - Gerçek uygulamada ViewModel'den gelecek
    val userLevel = 5

    // Load scenarios
    LaunchedEffect(Unit) {
        scope.launch {
            scenarios = ScenarioLoader.loadScenariosFromAssets(context)
            isLoading = false
        }
    }

    // Filter scenarios
    val filteredScenarios = scenarios.filter { scenario ->
        val categoryMatch = selectedCategory == null || scenario.category == selectedCategory
        val difficultyMatch = selectedDifficulty == null || scenario.difficulty == selectedDifficulty
        categoryMatch && difficultyMatch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tüm Senaryolar")
                        Text(
                            "${filteredScenarios.size} senaryo",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Filters
                FilterSection(
                    selectedCategory = selectedCategory,
                    selectedDifficulty = selectedDifficulty,
                    onCategorySelected = { selectedCategory = it },
                    onDifficultySelected = { selectedDifficulty = it }
                )

                // Scenarios List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredScenarios) { scenario ->
                        ScenarioListItem(
                            scenario = scenario,
                            userLevel = userLevel,
                            onClick = {
                                if (userLevel >= scenario.requiredLevel) {
                                    navController.navigate("scenario/${scenario.id}")
                                }
                            }
                        )
                    }

                    if (filteredScenarios.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🔍", fontSize = 64.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Senaryo bulunamadı",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Farklı filtre dene",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedCategory: ScenarioCategory?,
    selectedDifficulty: DifficultyLevel?,
    onCategorySelected: (ScenarioCategory?) -> Unit,
    onDifficultySelected: (DifficultyLevel?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            "Kategoriler",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Kategori chip'leri için yatay kaydırma
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Tümü") }
            )

            ScenarioCategory.values().forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(category.icon, fontSize = 14.sp)
                            Text(
                                category.displayName,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Zorluk",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Zorluk chip'leri için yatay kaydırma
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDifficulty == null,
                onClick = { onDifficultySelected(null) },
                label = { Text("Tümü") }
            )

            DifficultyLevel.values().forEach { difficulty ->
                FilterChip(
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    label = {
                        Text(
                            difficulty.displayName,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(difficulty.color.removePrefix("#").toLong(16) or 0xFF000000).copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
fun ScenarioListItem(
    scenario: Scenario,
    userLevel: Int,
    onClick: () -> Unit
) {
    val isUnlocked = userLevel >= scenario.requiredLevel
    val isCompleted = false // Gerçek uygulamada database'den gelecek

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                )
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.1f)
                                )
                            )
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        scenario.category.icon,
                        fontSize = 28.sp
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                // Completed badge
                if (isCompleted && isUnlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Tamamlandı",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Scenario Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    scenario.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSurface
                    else
                        Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category
                    Text(
                        scenario.category.displayName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text("•", color = Color.Gray, fontSize = 12.sp)

                    // Time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${scenario.estimatedTime}dk",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Difficulty badge
                Surface(
                    color = Color(scenario.difficulty.color.removePrefix("#").toLong(16) or 0xFF000000).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        scenario.difficulty.displayName,
                        fontSize = 11.sp,
                        color = Color(scenario.difficulty.color.removePrefix("#").toLong(16) or 0xFF000000),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Arrow icon
            if (isUnlocked) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}