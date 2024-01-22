package com.halfminute.itmadness

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

/**
 * A class for managing shared preferences in the Android application.
 * @param context The [Context] of the application.
 */
class SharedPref(context: Context) {

    private val mySharedPref: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)

    /**
     * Set the night mode state in shared preferences.
     * @param state A boolean indicating whether night mode is enabled or disabled.
     */
    fun setNightMode(state: Boolean) {
        mySharedPref.edit().putBoolean("darkMode", state).apply()
    }

    /**
     * Load the night mode state from shared preferences.
     * @return A boolean indicating whether night mode is enabled or disabled.
     */
    fun loadNightMode(): Boolean {
        return mySharedPref.getBoolean("darkMode", false)
    }

    /**
     * Set the sound state in shared preferences.
     * @param state A boolean indicating whether sound is enabled or disabled.
     */
    fun setSound(state: Boolean) {
        mySharedPref.edit().putBoolean("sound", state).apply()
    }

    /**
     * Get the sound state from shared preferences.
     * @return A boolean indicating whether sound is enabled or disabled.
     */
    fun getSound(): Boolean {
        return mySharedPref.getBoolean("sound", true)
    }

    /**
     * Set the locale language in the application.
     * @param lang The language code (e.g., "en" for English).
     * @param context The [Context] of the application.
     */
    @Suppress("DEPRECATION")
    fun setLocale(lang: String, context: Context) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val prefs = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        prefs.edit().putString("language", lang).apply()
    }

    /**
     * Load the selected locale language from shared preferences.
     * @param context The [Context] of the application.
     */
    fun loadLocale(context: Context) {
        val pref = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language =
            pref.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language
        setLocale(language, context)
    }

    /**
     * Get the saved language from shared preferences.
     * @param context The [Context] of the application.
     * @return The selected language code.
     */
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        return prefs.getString("language", Locale.getDefault().language)
            ?: Locale.getDefault().language
    }

    /**
     * Save the difficulty level in shared preferences.
     * @param difficulty The selected difficulty level.
     */
    fun saveDifficulty(difficulty: String) {
        mySharedPref.edit().putString("difficulty", difficulty).apply()
    }

    /**
     * Load the saved difficulty level from shared preferences.
     * @return The saved difficulty level.
     */
    fun loadDifficulty(): String {
        return mySharedPref.getString("difficulty", "easy") ?: "easy"
    }

    /**
     * Save the chosen game in shared preferences.
     * @param chosenGame The name of the chosen game.
     */
    fun saveChosenGame(chosenGame: String) {
        mySharedPref.edit().putString("chosenGame", chosenGame).apply()
    }

    /**
     * Load the saved chosen game from shared preferences.
     * @return The name of the chosen game.
     */
    fun loadChosenGame(): String {
        return mySharedPref.getString("chosenGame", "") ?: ""
    }

    /**
     * Show a custom Snackbar with a specified message.
     * @param activity The [Activity] in which the Snackbar should be displayed.
     * @param message The message to be displayed in the Snackbar.
     */
    fun showCustomSnackbar(activity: Activity, message: String) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )

        // Customize the Snackbar's background color based on the theme
        val snackbarView = snackbar.view
        val backgroundColor = if (loadNightMode()) {
            ContextCompat.getColor(activity, R.color.snackbar_background_dark)
        } else {
            ContextCompat.getColor(activity, R.color.snackbar_background_light)
        }
        snackbarView.setBackgroundColor(backgroundColor)

        // Customize the text color of the Snackbar
        val textView = snackbarView.findViewById<TextView>(
            com.google.android.material.R.id.snackbar_text
        )
        val textColor = if (loadNightMode()) {
            ContextCompat.getColor(activity, R.color.snackbar_text_dark)
        } else {
            ContextCompat.getColor(activity, R.color.snackbar_text_light)
        }
        textView.setTextColor(textColor)

        snackbar.show()
    }

    /**
     * Get the high score from shared preferences.
     * @return The high score value.
     */
    fun getHighScore(): Int {
        return mySharedPref.getInt("highScore", 0)
    }

    /**
     * Save the high score in shared preferences.
     * @param newScore The new high score to be saved.
     */
    fun saveHighScore(newScore: Int) {
        val currentHighScore = getHighScore()
        if (newScore > currentHighScore) {
            mySharedPref.edit().putInt("highScore", newScore).apply()
        }
    }

    // Additional functions for first start and update dialogs
    /**
     * Check if it's the first start of the app.
     * @return A boolean indicating whether it's the first start.
     */
    fun isFirstStart(): Boolean {
        return mySharedPref.getBoolean("firstStart", true)
    }

    /**
     * Set the flag to indicate that the first start dialog has been shown.
     */
    fun setFirstStartShown() {
        mySharedPref.edit().putBoolean("firstStart", false).apply()
    }
}
