@file:Suppress("UNCHECKED_CAST")

package com.erdemselvi.vicdanmetre.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdemselvi.vicdanmetre.models.JournalEmotion
import com.erdemselvi.vicdanmetre.models.JournalEntry
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(navController: NavController) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    // Mock journal entries (gerçek uygulamada ViewModel'den gelecek)
    val journalEntries = remember {
        listOf(
            JournalEntry(
                id = "j1",
                userId = "user1",
                scenarioId = "scenario_001",
                scenarioTitle = "Kopya Kağıdı",
                choicesMade = listOf("Hayır dedim"),
                reflection = "Çok zordu ama dürüst olmayı seçtim. İçim rahat, arkadaşım da sonunda anlayış gösterdi.",
                emotion = JournalEmotion.PROUD,
                regretLevel = 0,
                createdAt = LocalDateTime.now().minusDays(1)
            ),
            JournalEntry(
                id = "j2",
                userId = "user1",
                scenarioId = "scenario_002",
                scenarioTitle = "Kayıp Cüzdan",
                choicesMade = listOf("Sahibini buldum"),
                reflection = "Yaşlı amcanın mutluluğunu görünce ne kadar doğru bir karar aldığımı anladım.",
                emotion = JournalEmotion.SATISFIED,
                regretLevel = 0,
                createdAt = LocalDateTime.now().minusDays(3)
            ),
            JournalEntry(
                id = "j3",
                userId = "user1",
                scenarioId = "scenario_003",
                scenarioTitle = "Siber Zorbalık",
                choicesMade = listOf("Sessiz kaldım"),
                reflection = "Keşke müdahale etseydim. Arkadaşım çok üzüldü ve ben hiçbir şey yapmadım.",
                emotion = JournalEmotion.REGRETFUL,
                regretLevel = 4,
                createdAt = LocalDateTime.now().minusDays(7)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Vicdan Günlüğüm")
                        Text(
                            "${journalEntries.size} kayıt",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Yeni Yazı")
                    }
                }
            )
        }
    ) { padding ->
        if (journalEntries.isEmpty()) {
            EmptyJournalState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onAddClick = { showAddDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    JournalStatsCard(
                        totalEntries = journalEntries.size,
                        regretfulEntries = journalEntries.count { it.regretLevel > 2 },
                        proudEntries = journalEntries.count { it.emotion == JournalEmotion.PROUD }
                    )
                }

                item {
                    Text(
                        "Tüm Kayıtlar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(journalEntries) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onClick = {
                            selectedEntry = entry
                            showDetailDialog = true
                        }
                    )
                }
            }
        }
    }

    // Add Journal Dialog
    if (showAddDialog) {
        AddJournalDialog(
            onDismiss = { showAddDialog = false },
            onSave = { /* Save logic */ } as (String, JournalEmotion, Int) -> Unit
        )
    }

    // Detail Dialog
    if (showDetailDialog && selectedEntry != null) {
        JournalDetailDialog(
            entry = selectedEntry!!,
            onDismiss = { showDetailDialog = false },
            onDelete = {
                // Delete logic
                showDetailDialog = false
            }
        )
    }
}

@Composable
fun EmptyJournalState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📔", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Henüz Günlük Kaydın Yok",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Senaryolardan sonra düşüncelerini paylaş",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("İlk Yazını Ekle")
        }
    }
}

@Composable
fun JournalStatsCard(
    totalEntries: Int,
    regretfulEntries: Int,
    proudEntries: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("📖", totalEntries.toString(), "Toplam")
            StatItem("😌", proudEntries.toString(), "Gurur")
            StatItem("😔", regretfulEntries.toString(), "Pişmanlık")
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    entry.scenarioTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    entry.emotion.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                entry.reflection,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    entry.createdAt.format(dateFormatter),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (entry.regretLevel > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Pişmanlık: ${entry.regretLevel}/5",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JournalDetailDialog(
    entry: JournalEntry,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    entry.scenarioTitle,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    entry.createdAt.format(dateFormatter),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        text = {
            Column {
                // Emotion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(entry.emotion.emoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        getEmotionText(entry.emotion),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Choices Made
                Text(
                    "Verdiğin Kararlar:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                entry.choicesMade.forEach { choice ->
                    Text("• $choice", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reflection
                Text(
                    "Düşüncelerim:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    entry.reflection,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Regret Level
                if (entry.regretLevel > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RegretIndicator(entry.regretLevel)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showDeleteConfirm = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sil")
            }
        }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Günlüğü Sil?") },
            text = { Text("Bu kaydı silmek istediğinden emin misin?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun AddJournalDialog(
    onDismiss: () -> Unit,
    onSave: (String, JournalEmotion, Int) -> Unit
) {
    var reflection by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf(JournalEmotion.SATISFIED) }
    var regretLevel by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Günlük Kaydı") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Emotion Selector
                Text("Nasıl Hissediyorsun?", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    JournalEmotion.values().forEach { emotion ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedEmotion = emotion }
                                .padding(8.dp)
                        ) {
                            Text(
                                emotion.emoji,
                                fontSize = if (selectedEmotion == emotion) 32.sp else 24.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reflection Text
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    label = { Text("Düşüncelerini Yaz") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Bu kararı neden aldım? Tekrar karşılaşsam ne yapardım?") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Regret Level
                Text("Pişmanlık Seviyesi: $regretLevel/5", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = regretLevel.toFloat(),
                    onValueChange = { regretLevel = it.toInt() },
                    valueRange = 0f..5f,
                    steps = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reflection.isNotBlank()) {
                        onSave(reflection, selectedEmotion, regretLevel)
                    }
                    onDismiss()
                },
                enabled = reflection.isNotBlank()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}



@Composable
fun RegretIndicator(level: Int) {
    Column {
        Text(
            "Pişmanlık Seviyesi",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index < level) Color(0xFFFF9800)
                            else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

fun getEmotionText(emotion: JournalEmotion): String {
    return when (emotion) {
        JournalEmotion.SATISFIED -> "Memnun"
        JournalEmotion.PROUD -> "Gururlu"
        JournalEmotion.CONFLICTED -> "Kararsız"
        JournalEmotion.REGRETFUL -> "Pişman"
        JournalEmotion.PEACEFUL -> "Huzurlu"
        JournalEmotion.THOUGHTFUL -> "Düşünceli"
    }
}
