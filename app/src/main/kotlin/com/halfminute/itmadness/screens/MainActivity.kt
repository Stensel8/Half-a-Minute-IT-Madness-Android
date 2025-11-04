package com.halfminute.itmadness.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.halfminute.itmadness.R
import com.halfminute.itmadness.ui.theme.HalfMinuteITMadnessTheme
import com.halfminute.itmadness.utils.SharedPref
import com.halfminute.itmadness.utils.SoundManager

class MainActivity : ComponentActivity() {

    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sharedPref = SharedPref(this)
        applyLanguage(sharedPref.getLanguage())
        
        // Initialize sound manager
        SoundManager.initialize(this)
        SoundManager.setSoundEnabled(sharedPref.getSound())

        setContent {
            HalfMinuteITMadnessTheme {
                val isFirstStart = remember { sharedPref.isFirstStart() }
                val username = sharedPref.getUsername()
                
                MainScreen(
                    username = username,
                    showWelcomeMessage = !isFirstStart && username.isNotEmpty(),
                    onPlayClick = { startActivity(Intent(this, GameSelectionActivity::class.java)) },
                    onSettingsClick = { startActivity(Intent(this, SettingsActivity::class.java)) },
                    showWelcome = isFirstStart,
                    onWelcomeShown = { sharedPref.setFirstStartShown() }
                )
            }
        }
    }
    
    private fun applyLanguage(languageCode: String) {
        val newLocales = if (languageCode == "System") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageCode)
        }
        if (AppCompatDelegate.getApplicationLocales() != newLocales) {
            AppCompatDelegate.setApplicationLocales(newLocales)
        }
    }
}

@Composable
fun MainScreen(
    username: String,
    showWelcomeMessage: Boolean,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var showWelcomeDialog by remember { mutableStateOf(showWelcome) }
    
    // Show welcome back message after composition
    LaunchedEffect(showWelcomeMessage) {
        if (showWelcomeMessage) {
            snackbarHostState.showSnackbar("Welcome back, $username!")
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.halfminute_itmadness_applogo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.chooseGameTitle), style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.settingsTitle), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
    
    if (showWelcomeDialog) {
        WelcomeDialog(onDismiss = { 
            showWelcomeDialog = false
            onWelcomeShown()
        })
    }
}

@Composable
fun WelcomeDialog(onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.welcome)) },
        text = { Text(stringResource(R.string.welcomeText)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
                    ))
                )
            }) {
                Text(stringResource(R.string.release_notes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.closeWindow))
            }
        }
    )
}



