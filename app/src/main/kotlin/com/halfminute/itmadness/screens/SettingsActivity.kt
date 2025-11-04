package com.halfminute.itmadness.screens

import com.halfminute.itmadness.utils.SharedPref
import com.halfminute.itmadness.utils.SoundManager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halfminute.itmadness.BuildConfig
import com.halfminute.itmadness.R
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = SharedPref(this)
        setContent {
            HalfMinuteITMadnessTheme {
                SettingsScreenContent(
                    onBack = { finish() },
                    difficulty = sp.loadDifficulty(),
                    onDifficultyChange = { sp.saveDifficulty(it) },
                    username = sp.getUsername(),
                    onUsernameChange = { sp.saveUsername(it) },
                    soundEnabled = sp.getSound(),
                    onSoundChange = { enabled ->
                        sp.setSound(enabled)
                        SoundManager.setSoundEnabled(enabled)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    onBack: () -> Unit,
    difficulty: String,
    onDifficultyChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    soundEnabled: Boolean,
    onSoundChange: (Boolean) -> Unit
) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showDifficulty by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showUser by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showWelcome by remember { mutableStateOf(false) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var userInput by remember { mutableStateOf(username) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var currentDifficulty by remember { mutableStateOf(difficulty) }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var soundState by remember { mutableStateOf(soundEnabled) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingCard(stringResource(R.string.username), username.ifEmpty { stringResource(R.string.username_placeholder) }) { showUser = true }
            SettingCard(stringResource(R.string.difficulty), currentDifficulty.replaceFirstChar { it.uppercase() }) { showDifficulty = true }
            
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = soundState,
                        onCheckedChange = { 
                            soundState = it
                            onSoundChange(it)
                            scope.launch {
                                snackbarHostState.showSnackbar(if (it) "Sound enabled" else "Sound disabled")
                            }
                        }
                    )
                }
            }
            
            SettingCard("Welcome Screen", "") { showWelcome = true }
            SettingCard(stringResource(R.string.release_notes), "") {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases")))
            }
            SettingCard("View Source Code", "") {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android")))
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Project Notice Card
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "📝 Project Notice",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "This was a school project created by 2 classmates competing against teams of 4. Mobile game development was not our area of expertise, but we took on the challenge for fun and learning.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "The v4.0.1 rewrite was completed with AI assistance (Claude Code). Unexpected bugs may still appear. This project is now End of Life and provided for archival purposes only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Text(
                "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showUser) {
        AlertDialog(
            onDismissRequest = { showUser = false },
            title = { Text(stringResource(R.string.username)) },
            text = {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { if (it.length <= 15) userInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.username_placeholder)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    onUsernameChange(userInput)
                    showUser = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Username: $userInput")
                    }
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showUser = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDifficulty) {
        AlertDialog(
            onDismissRequest = { showDifficulty = false },
            title = { Text(stringResource(R.string.difficulty)) },
            text = {
                Column {
                    listOf("easy", "medium", "hard").forEach { diff ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    currentDifficulty = diff
                                    onDifficultyChange(diff)
                                    showDifficulty = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Difficulty: ${diff.replaceFirstChar { it.uppercase() }}")
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = diff == currentDifficulty,
                                onClick = { 
                                    currentDifficulty = diff
                                    onDifficultyChange(diff)
                                    showDifficulty = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Difficulty: ${diff.replaceFirstChar { it.uppercase() }}")
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(diff.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
    
    if (showWelcome) {
        AlertDialog(
            onDismissRequest = { showWelcome = false },
            title = { Text(stringResource(R.string.welcome)) },
            text = { Text(stringResource(R.string.welcomeText)) },
            confirmButton = {
                TextButton(onClick = {
                    showWelcome = false
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases")))
                }) {
                    Text(stringResource(R.string.release_notes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showWelcome = false }) {
                    Text(stringResource(R.string.closeWindow))
                }
            }
        )
    }
}

@Composable
private fun SettingCard(title: String, value: String, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = if (value.isNotEmpty()) {{ Text(value) }} else null
        )
    }
}
