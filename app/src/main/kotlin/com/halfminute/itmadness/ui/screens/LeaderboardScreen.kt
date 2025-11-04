package com.halfminute.itmadness.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halfminute.itmadness.utils.SharedPref
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import java.text.SimpleDateFormat
import java.util.*

// Data model
data class LeaderboardEntry(
    val username: String,
    val score: Int,
    val gameType: String,
    val difficulty: String,
    val timestamp: Long
)

// Activity
class LeaderboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sharedPref = SharedPref(this)
        
        setContent {
            HalfMinuteITMadnessTheme {
                LeaderboardScreen(
                    onNavigateBack = { finish() },
                    leaderboardEntries = sharedPref.getOverallTopScores(100),
                    currentUsername = sharedPref.getUsername()
                )
            }
        }
    }
}

// Screen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit,
    leaderboardEntries: List<LeaderboardEntry>,
    currentUsername: String
) {
    BackHandler { onNavigateBack() }
    
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var selectedGameType by remember { mutableStateOf("all") }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var selectedDifficulty by remember { mutableStateOf("all") }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Filter entries
    val filteredEntries = remember(leaderboardEntries, selectedGameType, selectedDifficulty) {
        leaderboardEntries.filter { entry ->
            val gameMatch = selectedGameType == "all" || entry.gameType == selectedGameType
            val difficultyMatch = selectedDifficulty == "all" || entry.difficulty == selectedDifficulty
            gameMatch && difficultyMatch
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                actions = {
                    TextButton(onClick = { showFilterDialog = true }) {
                        Text("Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGameType != "all",
                    onClick = { showFilterDialog = true },
                    label = { 
                        Text(
                            when (selectedGameType) {
                                "all" -> "All Games"
                                "math" -> "Math"
                                "guessing" -> "Guessing"
                                "language" -> "Language"
                                else -> "All Games"
                            }
                        )
                    }
                )
                FilterChip(
                    selected = selectedDifficulty != "all",
                    onClick = { showFilterDialog = true },
                    label = { 
                        Text(
                            when (selectedDifficulty) {
                                "all" -> "All Difficulties"
                                "EASY" -> "Easy"
                                "MEDIUM" -> "Medium"
                                "HARD" -> "Hard"
                                else -> "All Difficulties"
                            }
                        )
                    }
                )
            }
            
            if (filteredEntries.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No scores yet!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Play some games to see scores here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Leaderboard list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(filteredEntries) { index, entry ->
                        LeaderboardCard(
                            position = index + 1,
                            entry = entry,
                            isCurrentUser = entry.username == currentUsername
                        )
                    }
                }
            }
        }
        
        // Filter dialog
        if (showFilterDialog) {
            FilterDialog(
                currentGameType = selectedGameType,
                currentDifficulty = selectedDifficulty,
                onDismiss = { showFilterDialog = false },
                onApplyFilter = { gameType, difficulty ->
                    selectedGameType = gameType
                    selectedDifficulty = difficulty
                    showFilterDialog = false
                }
            )
        }
    }
}

@Composable
private fun LeaderboardCard(
    position: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (position <= 3) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position badge with medal emoji for top 3
            Box(
                modifier = Modifier
                    .size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = when (position) {
                        1 -> Color(0xFFFFD700) // Gold
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> MaterialTheme.colorScheme.surface
                    },
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (position <= 3) {
                            Text(
                                text = "#$position",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        } else {
                            Text(
                                text = "#$position",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User info and score
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = entry.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrentUser) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    if (isCurrentUser) {
                        Text(
                            text = "(You)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = entry.gameType.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = when (entry.difficulty) {
                            "EASY" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            "MEDIUM" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            "HARD" -> Color(0xFFF44336).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        },
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = entry.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = when (entry.difficulty) {
                                "EASY" -> Color(0xFF2E7D32)
                                "MEDIUM" -> Color(0xFFE65100)
                                "HARD" -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.onTertiaryContainer
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text = dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Score with trophy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = entry.score.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun FilterDialog(
    currentGameType: String,
    currentDifficulty: String,
    onDismiss: () -> Unit,
    onApplyFilter: (String, String) -> Unit
) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var selectedGameType by remember { mutableStateOf(currentGameType) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var selectedDifficulty by remember { mutableStateOf(currentDifficulty) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Leaderboard") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Game Type",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                listOf(
                    "all" to "All Games",
                    "math" to "Math",
                    "guessing" to "Guessing",
                    "language" to "Language"
                ).forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGameType == value,
                            onClick = { selectedGameType = value }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Difficulty",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                listOf(
                    "all" to "All Difficulties",
                    "EASY" to "Easy",
                    "MEDIUM" to "Medium",
                    "HARD" to "Hard"
                ).forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDifficulty == value,
                            onClick = { selectedDifficulty = value }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onApplyFilter(selectedGameType, selectedDifficulty) }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
