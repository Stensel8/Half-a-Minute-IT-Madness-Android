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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class GuessingGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = SharedPref(this)
        val diff = Difficulty.fromString(sp.loadDifficulty())
        val words = Words.getInstance(this)?.getWordsByDifficulty(diff) ?: emptyList()
        val ppa = when (diff) { Difficulty.EASY -> 2; Difficulty.MEDIUM -> 3; Difficulty.HARD -> 4 }
        setContent {
            HalfMinuteITMadnessTheme {
                GuessingGameScreen(words, ppa, diff.name) { pts ->
                    startActivity(Intent(this, GameOverActivity::class.java).apply { 
                        putExtra("points", pts)
                        putExtra("chosenGame", "guessing")
                        putExtra("difficulty", diff.name)
                    })
                    finish()
                }
            }
        }
    }
}

@Composable
fun GuessingGameScreen(words: List<Word>, ppa: Int, difficulty: String, onGameOver: (Int) -> Unit) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var time by remember { mutableIntStateOf(30) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var gameEnded by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var sel by remember { mutableStateOf(setOf<Int>()) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var w by remember { mutableStateOf(words.shuffled().take(4)) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var flashIndex by remember { mutableIntStateOf(-1) }
    val loc = Locale.getDefault().language
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Timer blink animation
    val timerAlpha by animateFloatAsState(
        targetValue = if (time <= 5 && time > 0) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerBlink"
    )
    
    LaunchedEffect(Unit) {
        while (time > 0) {
            delay(1000)
            time--
            if (time == 5) {
                SoundManager.playCountdown()
            }
        }
        if (!gameEnded) {
            gameEnded = true
            SoundManager.stopCountdown()
            SoundManager.playApplause()
            onGameOver(sel.size * ppa)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(
                    "${time}s",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (time <= 5) Color.Red.copy(alpha = timerAlpha) else MaterialTheme.colorScheme.onSurface
                )
                Text("${sel.size * ppa} pts", style = MaterialTheme.typography.headlineSmall)
                Text(difficulty, style = MaterialTheme.typography.headlineSmall)
            }
            
            Column(Modifier.weight(1f).fillMaxWidth(), Arrangement.SpaceEvenly) {
                w.forEachIndexed { i, word ->
                    val isSel = sel.contains(i)
                    val isFlashing = flashIndex == i
                    var isProcessing by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isFlashing) 1.05f else 1f,
                        animationSpec = tween(50)
                    )
                    
                    Button(
                        onClick = {
                            if (!gameEnded && !isProcessing) {
                                isProcessing = true
                                val wasSelected = isSel
                                sel = if (isSel) sel - i else sel + i
                                flashIndex = i
                                if (!wasSelected) {
                                    SoundManager.playCorrect()
                                } else {
                                    SoundManager.playIncorrect()
                                }
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    if (!wasSelected) {
                                        launch { snackbarHostState.showSnackbar("✅ +$ppa") }
                                    } else {
                                        launch { snackbarHostState.showSnackbar("❌ -$ppa") }
                                    }
                                    delay(100)
                                    flashIndex = -1
                                    isProcessing = false
                                }
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .scale(scale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isFlashing && !isSel -> Color(0xFF4CAF50)
                                isSel -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = when {
                                isFlashing || isSel -> Color.White
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        enabled = !isProcessing
                    ) {
                        Text(
                            when (loc) {
                                "fr" -> word.frWord
                                "nl" -> word.nlWord
                                "de" -> word.deWord
                                else -> word.enWord
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
