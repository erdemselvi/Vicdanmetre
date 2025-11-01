package com.erdemselvi.vicdanmetre.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdemselvi.vicdanmetre.data.BadgeDefinitions
import com.erdemselvi.vicdanmetre.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<BadgeCategory?>(null) }
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    var showBadgeDialog by remember { mutableStateOf(false) }

    // Mock earned badges (gerçek uygulamada ViewModel'den gelecek)
    val earnedBadgeIds = remember {
        listOf(
            "badge_honest_1",
            "badge_justice_1",
            "badge_empathy_1",
            "badge_streak_1",
            "badge_scenario_1",
            "badge_journal_1"
        )
    }

    val allBadges = if (selectedCategory != null) {
        BadgeDefinitions.getBadgesByCategory(selectedCategory!!)
    } else {
        BadgeDefinitions.allBadges
    }

    val badgesWithStatus = allBadges.map { badge ->
        badge to earnedBadgeIds.contains(badge.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rozetler")
                        Text(
                            "${earnedBadgeIds.size}/${BadgeDefinitions.allBadges.size}",
                            fontSize = 14.sp,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Filter
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // Progress Bar
            LinearProgressIndicator(
                progress = earnedBadgeIds.size.toFloat() / BadgeDefinitions.allBadges.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Badges Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badgesWithStatus) { (badge, isEarned) ->
                    BadgeItem(
                        badge = badge,
                        isEarned = isEarned,
                        onClick = {
                            selectedBadge = badge
                            showBadgeDialog = true
                        }
                    )
                }
            }
        }
    }

    // Badge Detail Dialog
    if (showBadgeDialog && selectedBadge != null) {
        BadgeDetailDialog(
            badge = selectedBadge!!,
            isEarned = earnedBadgeIds.contains(selectedBadge!!.id),
            onDismiss = { showBadgeDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterRow(
    selectedCategory: BadgeCategory?,
    onCategorySelected: (BadgeCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Tümü") }
        )

        BadgeCategory.values().take(7).forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    isEarned: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isEarned) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    if (isEarned) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000),
                                MaterialTheme.colorScheme.surfaceVariant
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
                    color = if (isEarned)
                        Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000)
                    else
                        Color.Gray.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badge.iconUrl,
                fontSize = if (isEarned) 40.sp else 35.sp,
                modifier = Modifier.graphicsLayer(alpha = if (isEarned) 1f else 0.3f)
            )

            if (!isEarned) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Kilitli",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-4).dp, (-4).dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = badge.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (isEarned)
                MaterialTheme.colorScheme.onSurface
            else
                Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Rarity indicator
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(
                    color = if (isEarned)
                        Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000).copy(alpha = 0.3f)
                    else
                        Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = badge.rarity.displayName,
                fontSize = 9.sp,
                color = if (isEarned)
                    Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000)
                else
                    Color.Gray
            )
        }
    }
}

@Composable
fun BadgeDetailDialog(
    badge: Badge,
    isEarned: Boolean,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    badge.iconUrl,
                    fontSize = 60.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    badge.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column {
                // Rarity Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = badge.rarity.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(badge.rarity.color.removePrefix("#").toLong(16) or 0xFF000000)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    badge.description,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEarned) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Kazanıldı!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    } else {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Henüz Kazanılmadı",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            "Ödüller",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (badge.reward.crystals > 0) {
                            RewardRow("💎", "Kristal", badge.reward.crystals.toString())
                        }

                        if (badge.reward.wisdomPoints > 0) {
                            RewardRow("⭐", "Hikmet Puanı", badge.reward.wisdomPoints.toString())
                        }

                        badge.reward.specialTitle?.let { title ->
                            RewardRow("👑", "Özel Unvan", title)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tamam")
            }
        }
    )
}

@Composable
fun RewardRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 14.sp)
        }
        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}