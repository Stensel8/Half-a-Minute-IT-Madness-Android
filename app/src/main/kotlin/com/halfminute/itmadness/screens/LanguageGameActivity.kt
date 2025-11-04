package com.halfminute.itmadness.screens

import com.halfminute.itmadness.R
import com.halfminute.itmadness.utils.SharedPref
import com.halfminute.itmadness.utils.Words
import com.halfminute.itmadness.utils.Word
import com.halfminute.itmadness.utils.Difficulty
import com.halfminute.itmadness.utils.SoundManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import kotlinx.coroutines.*

class LanguageGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = SharedPref(this)
        val diff = Difficulty.fromString(sp.loadDifficulty())
        val words = Words.getInstance(this)?.getWordsByDifficulty(diff) ?: emptyList()
        val lives = when (diff) { Difficulty.EASY -> 5; Difficulty.MEDIUM -> 3; Difficulty.HARD -> 1 }
        
        // Get source and target languages from intent
        val sourceLanguage = intent.getStringExtra("sourceLanguage") ?: "nl"
        val targetLanguage = intent.getStringExtra("targetLanguage") ?: "en"
        
        setContent {
            HalfMinuteITMadnessTheme {
                LanguageGameScreen(
                    words = words,
                    difficulty = diff.name,
                    maxLives = lives,
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    onGameOver = { pts ->
                        startActivity(Intent(this, GameOverActivity::class.java).apply {
                            putExtra("points", pts)
                            putExtra("chosenGame", "${sourceLanguage}To${targetLanguage.replaceFirstChar { it.uppercase() }}")
                            putExtra("difficulty", diff.name)
                        })
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageGameScreen(
    words: List<Word>,
    difficulty: String,
    maxLives: Int,
    sourceLanguage: String,
    targetLanguage: String,
    onGameOver: (Int) -> Unit
) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var time by remember { mutableIntStateOf(30) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var pts by remember { mutableIntStateOf(0) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var lives by remember { mutableIntStateOf(maxLives) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var gameEnded by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var word by remember { mutableStateOf(words.random()) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var opts by remember { mutableStateOf(listOf<String>()) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var shake by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var correctFlash by remember { mutableIntStateOf(-1) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var isProcessing by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val diff = Difficulty.fromString(difficulty)
    val pointsPerAnswer = when (diff) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 10
        Difficulty.HARD -> 20
    }
    
    // Get the word in the source language
    val sourceWord = remember(word, sourceLanguage) {
        when (sourceLanguage) {
            "fr" -> word.frWord
            "nl" -> word.nlWord
            "de" -> word.deWord
            else -> word.enWord
        }
    }
    
    // Generate options in target language
    val correctAnswer = remember(word, targetLanguage) {
        when (targetLanguage) {
            "fr" -> word.frWord
            "nl" -> word.nlWord
            "de" -> word.deWord
            else -> word.enWord
        }
    }
    
    LaunchedEffect(word, targetLanguage) {
        val wrongOptions = words.filter { it != word }.shuffled().take(3).map { wrongWord ->
            when (targetLanguage) {
                "fr" -> wrongWord.frWord
                "nl" -> wrongWord.nlWord
                "de" -> wrongWord.deWord
                else -> wrongWord.enWord
            }
        }
        
        opts = (wrongOptions + correctAnswer).shuffled()
    }
    
    // Shake animation
    val shakeOffset by animateFloatAsState(
        targetValue = if (shake) 10f else 0f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        finishedListener = { shake = false }
    )
    
    // Timer blink animation
    val timerAlpha by animateFloatAsState(
        targetValue = if (time <= 5 && time > 0) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerBlink"
    )
    
    // Timer
    LaunchedEffect(Unit) {
        while (time > 0 && lives > 0) {
            delay(1000)
            time--
            if (time == 5) {
                SoundManager.playCountdown()
            }
        }
        if (!gameEnded && (lives == 0 || time == 0)) {
            gameEnded = true
            SoundManager.stopCountdown()
            SoundManager.playApplause()
            onGameOver(pts)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .offset { IntOffset(shakeOffset.toInt(), 0) },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with stats
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    "${time}s",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (time <= 5) Color.Red.copy(alpha = timerAlpha) else MaterialTheme.colorScheme.onSurface
                )
                Text("$pts pts", style = MaterialTheme.typography.headlineSmall)
                Text("❤️".repeat(lives), style = MaterialTheme.typography.headlineSmall)
                Text(difficulty, style = MaterialTheme.typography.headlineSmall)
            }
            
            // Word to translate
            Card(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getLanguageName(sourceLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sourceWord,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "↓",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getLanguageName(targetLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Options
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                Arrangement.SpaceEvenly
            ) {
                opts.forEachIndexed { index, opt ->
                    val isCorrectFlashing = correctFlash == index
                    val scale by animateFloatAsState(
                        targetValue = if (isCorrectFlashing) 1.05f else 1f,
                        animationSpec = tween(50)
                    )
                    
                    Button(
                        onClick = {
                            if (!gameEnded && !isProcessing) {
                                isProcessing = true
                                if (opt == correctAnswer) {
                                    pts += pointsPerAnswer
                                    correctFlash = index
                                    SoundManager.playCorrect()
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        launch { snackbarHostState.showSnackbar("✅ +$pointsPerAnswer") }
                                        delay(100)
                                        correctFlash = -1
                                        isProcessing = false
                                    }
                                    word = words.random()
                                } else {
                                    lives--
                                    shake = true
                                    SoundManager.playIncorrect()
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        launch { snackbarHostState.showSnackbar("❌ $lives") }
                                        delay(150)
                                        isProcessing = false
                                    }
                                    if (lives == 0) {
                                        gameEnded = true
                                        SoundManager.playApplause()
                                        onGameOver(pts)
                                    }
                                }
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .scale(scale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrectFlashing) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isCorrectFlashing) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        enabled = !isProcessing
                    ) {
                        Text(opt, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

private fun getLanguageName(code: String): String {
    return when (code) {
        "en" -> "English"
        "nl" -> "Nederlands"
        "de" -> "Deutsch"
        "fr" -> "Français"
        else -> "English"
    }
}
