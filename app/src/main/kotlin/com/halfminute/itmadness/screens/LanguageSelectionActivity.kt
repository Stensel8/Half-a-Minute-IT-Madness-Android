package com.halfminute.itmadness.screens

import com.halfminute.itmadness.R

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme

class LanguageSelectionActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            HalfMinuteITMadnessTheme {
                LanguageSelectionScreen(
                    onStartGame = { sourceLanguage, targetLanguage ->
                        val intent = Intent(this, LanguageGameActivity::class.java).apply {
                            putExtra("sourceLanguage", sourceLanguage)
                            putExtra("targetLanguage", targetLanguage)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onStartGame: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val languages = mapOf(
        "en" to "English",
        "nl" to "Nederlands",
        "de" to "Deutsch",
        "fr" to "Français"
    )
    
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var sourceLanguage by remember { mutableStateOf("nl") }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var targetLanguage by remember { mutableStateOf("en") }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showSourceDialog by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showTargetDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Languages") },
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choose language pair",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Source language selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                onClick = { showSourceDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "From",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = languages[sourceLanguage] ?: "English",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Arrow text
            Text(
                text = "→",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Target language selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                onClick = { showTargetDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "To",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = languages[targetLanguage] ?: "Nederlands",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Start button
            Button(
                onClick = { onStartGame(sourceLanguage, targetLanguage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = sourceLanguage != targetLanguage
            ) {
                Text(
                    text = "Start Game",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            if (sourceLanguage == targetLanguage) {
                Text(
                    text = "Please select different languages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
    
    // Source language dialog
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Select source language") },
            text = {
                Column {
                    languages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = code == sourceLanguage,
                                onClick = {
                                    sourceLanguage = code
                                    showSourceDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Target language dialog
    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Select target language") },
            text = {
                Column {
                    languages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = code == targetLanguage,
                                onClick = {
                                    targetLanguage = code
                                    showTargetDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
