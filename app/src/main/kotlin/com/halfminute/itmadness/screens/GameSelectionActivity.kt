package com.halfminute.itmadness.screens

import com.halfminute.itmadness.R
import com.halfminute.itmadness.utils.SharedPref

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class GameSelectionActivity : ComponentActivity() {
    
    private lateinit var sharedPref: SharedPref
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sharedPref = SharedPref(this)
        
        setContent {
            HalfMinuteITMadnessTheme {
                ChooseGameScreen(
                    onMathGame = { 
                        sharedPref.saveChosenGame("math")
                        startActivity(Intent(this, MathGameActivity::class.java)) 
                    },
                    onLanguageGame = { 
                        sharedPref.saveChosenGame("language")
                        // Navigate to language selection (source/target)
                        startActivity(Intent(this, LanguageSelectionActivity::class.java)) 
                    },
                    onGuessingGame = { 
                        sharedPref.saveChosenGame("guessing")
                        startActivity(Intent(this, GuessingGameActivity::class.java)) 
                    },
                    onBack = { finish() },
                    sharedPref = sharedPref
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseGameScreen(
    onMathGame: () -> Unit,
    onLanguageGame: () -> Unit,
    onGuessingGame: () -> Unit,
    onBack: () -> Unit,
    sharedPref: SharedPref
) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var diceValue by remember { mutableStateOf(sharedPref.loadLastThrownDiceValue().takeIf { it > 0 }) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var diceReady by remember { mutableStateOf(true) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var remainingTime by remember { mutableIntStateOf(0) }
    
    // Check if dice was rolled recently
    LaunchedEffect(Unit) {
        val rollTimestamp = sharedPref.loadDiceRollTimestamp()
        if (rollTimestamp > 0) {
            val elapsed = System.currentTimeMillis() - rollTimestamp
            if (elapsed < 10000) {
                diceReady = false
                remainingTime = ((10000 - elapsed) / 1000).toInt()
                diceValue = sharedPref.loadLastThrownDiceValue()
                
                // Start countdown
                while (remainingTime > 0) {
                    delay(1000)
                    remainingTime--
                }
                diceReady = true
                sharedPref.clearDiceRollTimestamp()
            } else {
                sharedPref.clearDiceRollTimestamp()
            }
        }
    }
    
    fun rollDice() {
        val newValue = Random.nextInt(1, 7)
        diceValue = newValue
        diceReady = false
        remainingTime = 10
        sharedPref.saveLastThrownDiceValue(newValue)
        sharedPref.saveDiceRollTimestamp(System.currentTimeMillis())
    }
    
    LaunchedEffect(diceReady, remainingTime) {
        if (!diceReady && remainingTime > 0) {
            delay(1000)
            remainingTime--
            if (remainingTime == 0) {
                diceReady = true
                sharedPref.clearDiceRollTimestamp()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chooseGameTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Game selection buttons
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onMathGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        stringResource(R.string.mathGameButton),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onLanguageGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        stringResource(R.string.languageGameButton),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onGuessingGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        stringResource(R.string.guessingGameButton),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            // Dice roller section at bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (diceValue != null) {
                    Text(
                        text = "Last roll: $diceValue",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                Button(
                    onClick = { if (diceReady) rollDice() },
                    enabled = diceReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (diceReady) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = if (diceReady) {
                            "🎲 ${stringResource(R.string.throw_dice)}"
                        } else {
                            "🎲 Wait ${remainingTime}s (Rolled: $diceValue)"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
