package com.halfminute.itmadness.screens

import com.halfminute.itmadness.R
import com.halfminute.itmadness.utils.SharedPref
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
import kotlin.random.Random

class MathGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = SharedPref(this)
        val diff = try { Difficulty.valueOf(sp.loadDifficulty().uppercase()) } catch (e: Exception) { Difficulty.EASY }
        val lives = when (diff) { Difficulty.EASY -> 5; Difficulty.MEDIUM -> 3; Difficulty.HARD -> 1 }
        setContent {
            HalfMinuteITMadnessTheme {
                MathGameScreen(diff.name, lives) { pts ->
                    startActivity(Intent(this, GameOverActivity::class.java).apply { 
                        putExtra("points", pts)
                        putExtra("chosenGame", "math")
                        putExtra("difficulty", diff.name)
                    })
                    finish()
                }
            }
        }
    }
}

@Composable
fun MathGameScreen(difficulty: String, maxLives: Int, onGameOver: (Int) -> Unit) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var time by remember { mutableIntStateOf(30) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var pts by remember { mutableIntStateOf(0) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var lives by remember { mutableIntStateOf(maxLives) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var gameEnded by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var q by remember { mutableStateOf("") }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var ans by remember { mutableIntStateOf(0) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var opts by remember { mutableStateOf(listOf<Int>()) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var shake by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var correctFlash by remember { mutableIntStateOf(-1) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var isProcessing by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val diff = Difficulty.fromString(difficulty)
    
    // Points per correct answer based on difficulty
    val pointsPerAnswer = when (diff) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 10
        Difficulty.HARD -> 20
    }
    
    fun gen() {
        val range = when (diff) {
            Difficulty.EASY -> 1..20
            Difficulty.MEDIUM -> 10..75
            Difficulty.HARD -> 20..150
        }
        val a = Random.nextInt(range.first, range.last)
        val b = Random.nextInt(range.first, range.last)
        val ops = when (diff) {
            Difficulty.EASY -> listOf("+", "-")
            Difficulty.MEDIUM -> listOf("+", "-", "*")
            Difficulty.HARD -> listOf("+", "-", "*", "/")
        }
        val op = ops.random()
        ans = when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0 && a % b == 0) a / b else a + b
            else -> a + b
        }
        q = "$a $op $b = ?"
        val wrongRange = when (diff) {
            Difficulty.EASY -> -3..3
            Difficulty.MEDIUM -> -15..15
            Difficulty.HARD -> -30..30
        }
        val wrong = List(3) { 
            var wrongAns = ans + Random.nextInt(wrongRange.first, wrongRange.last)
            while (wrongAns == ans || wrongAns == 0) {
                wrongAns = ans + Random.nextInt(wrongRange.first, wrongRange.last)
            }
            wrongAns
        }
        opts = (wrong + ans).shuffled()
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
    
    LaunchedEffect(Unit) {
        gen()
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
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(
                    "${time}s",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (time <= 5) Color.Red.copy(alpha = timerAlpha) else MaterialTheme.colorScheme.onSurface
                )
                Text("$pts pts", style = MaterialTheme.typography.headlineSmall)
                Text("❤️".repeat(lives), style = MaterialTheme.typography.headlineSmall)
                Text(difficulty, style = MaterialTheme.typography.headlineSmall)
            }
            
            Card(Modifier.padding(16.dp).fillMaxWidth()) {
                Text(
                    q,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(32.dp).align(Alignment.CenterHorizontally)
                )
            }
            
            Column(Modifier.weight(1f).fillMaxWidth(), Arrangement.SpaceEvenly) {
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
                                if (opt == ans) {
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
                                    gen()
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
                        Text(opt.toString(), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
