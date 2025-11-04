package com.halfminute.itmadness.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun HalfMinuteITMadnessTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val colors = if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val ic = WindowCompat.getInsetsController((view.context as Activity).window, view)
            ic.isAppearanceLightStatusBars = !darkTheme
            ic.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colors, content = content)
}
