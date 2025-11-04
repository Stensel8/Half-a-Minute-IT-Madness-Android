package com.halfminute.itmadness.screens

import com.halfminute.itmadness.R
import com.halfminute.itmadness.utils.SharedPref

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import com.halfminute.itmadness.ui.screens.LeaderboardScreen

class GameOverActivity : ComponentActivity() {

    private lateinit var sharedPref: SharedPref
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sharedPref = SharedPref(this)
        
        val points = intent?.extras?.getInt("points") ?: 0
        val chosenGame = intent?.extras?.getString("chosenGame") ?: ""
        val difficulty = intent?.extras?.getString("difficulty") ?: ""
        
        val highScore = sharedPref.getHighScore()
        val isNewHighScore = points > highScore
        if (isNewHighScore) {
            sharedPref.saveHighScore(points)
            if (sharedPref.getSound()) {
                player = MediaPlayer.create(this, R.raw.applause).apply { start() }
            }
        }
        
        val username = sharedPref.getUsername()
        if (username.isNotEmpty() && points > 0) {
            val normalizedGame = when {
                chosenGame.contains("To", ignoreCase = true) -> "language"
                chosenGame.contains("math", ignoreCase = true) -> "math"
                chosenGame.contains("guessing", ignoreCase = true) -> "guessing"
                else -> chosenGame.lowercase()
            }
            sharedPref.saveScore(username, points, normalizedGame, difficulty.uppercase())
        }

        setContent {
            HalfMinuteITMadnessTheme {
                @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
                var showLeaderboard by remember { mutableStateOf(false) }
                
                if (showLeaderboard) {
                    LeaderboardScreen(
                        onNavigateBack = { showLeaderboard = false },
                        leaderboardEntries = sharedPref.getOverallTopScores(100),
                        currentUsername = sharedPref.getUsername()
                    )
                } else {
                    GameOverScreen(
                        points = points,
                        highScore = highScore,
                        gameType = chosenGame,
                        difficulty = difficulty,
                        isNewHighScore = isNewHighScore,
                        onHomeClick = { navigate(MainActivity::class.java) },
                        onRestartClick = { restart(chosenGame) },
                        onChangeGameClick = { navigate(GameSelectionActivity::class.java) },
                        onLeaderboardClick = { showLeaderboard = true }
                    )
                }
            }
        }
    }
    
    private fun restart(gameType: String) {
        val intent = when {
            gameType == "math" -> Intent(this, MathGameActivity::class.java)
            gameType == "guessing" -> Intent(this, GuessingGameActivity::class.java)
            gameType.contains("To", ignoreCase = true) -> {
                // Language game - parse source and target
                val parts = gameType.split("To", ignoreCase = true, limit = 2)
                if (parts.size == 2) {
                    Intent(this, LanguageGameActivity::class.java).apply {
                        putExtra("sourceLanguage", parts[0].lowercase())
                        putExtra("targetLanguage", parts[1].lowercase())
                    }
                } else {
                    Intent(this, LanguageSelectionActivity::class.java)
                }
            }
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
    
    private fun navigate(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverScreen(
    points: Int,
    highScore: Int,
    gameType: String,
    difficulty: String,
    isNewHighScore: Boolean,
    onHomeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onChangeGameClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Over") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isNewHighScore) {
                Text(
                    "New High Score!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Text(
                "Score: $points",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                "High Score: $highScore",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "$gameType - $difficulty",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            FilledTonalButton(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Home")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onChangeGameClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Game")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onLeaderboardClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leaderboard")
            }
        }
    }
}
