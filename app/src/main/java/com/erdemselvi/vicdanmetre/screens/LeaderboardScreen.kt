package com.erdemselvi.vicdanmetre.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    // Mock leaderboard data
    val leaderboardData = remember {
        listOf(
            LeaderboardEntry("Ahmet Y.", 5280, 47, "🦁"),
            LeaderboardEntry("Ayşe K.", 4920, 43, "🦉"),
            LeaderboardEntry("Mehmet D.", 4675, 39, "💎"),
            LeaderboardEntry("Zeynep A.", 4230, 35, "❤️"),
            LeaderboardEntry("Can S.", 3890, 31, "⚖️"),
            LeaderboardEntry("Sen", 3450, 28, "🤍"),
            LeaderboardEntry("Elif T.", 3120, 25, "✨"),
            LeaderboardEntry("Burak M.", 2890, 22, "🔥")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lider Tablosu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Haftalık") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Aylık") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Tüm Zamanlar") }
                )
            }

            // Top 3 Podium
            TopThreePodium(leaderboardData.take(3))

            // Rest of the list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(leaderboardData.drop(3)) { index, entry ->
                    LeaderboardCard(entry, index + 4)
                }
            }
        }
    }
}

data class LeaderboardEntry(
    val username: String,
    val score: Int,
    val level: Int,
    val icon: String
)

@Composable
fun TopThreePodium(topThree: List<LeaderboardEntry>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place
        if (topThree.size > 1) {
            PodiumItem(topThree[1], 2, 120.dp, Color(0xFFC0C0C0))
        }

        // 1st place
        if (topThree.isNotEmpty()) {
            PodiumItem(topThree[0], 1, 140.dp, Color(0xFFFFD700))
        }

        // 3rd place
        if (topThree.size > 2) {
            PodiumItem(topThree[2], 3, 100.dp, Color(0xFFCD7F32))
        }
    }
}

@Composable
fun PodiumItem(entry: LeaderboardEntry, rank: Int, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.icon, fontSize = 30.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            entry.username,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Text(
            "${entry.score}",
            fontSize = 12.sp,
            color = color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .width(70.dp)
                .height(height),
            color = color.copy(alpha = 0.3f),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "#$rank",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = color
                )
            }
        }
    }
}

@Composable
fun LeaderboardCard(entry: LeaderboardEntry, rank: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                "#$rank",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            // Icon
            Text(entry.icon, fontSize = 32.sp)

            Spacer(modifier = Modifier.width(16.dp))

            // Username and Level
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    "Seviye ${entry.level}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Score
            Text(
                "${entry.score}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}