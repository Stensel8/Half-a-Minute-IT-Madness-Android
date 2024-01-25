package com.halfminute.itmadness

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class SharedPref(context: Context) {

    private val mySharedPref: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)

    fun setNightMode(state: Boolean) {
        mySharedPref.edit().putBoolean("darkMode", state).apply()
    }

    fun loadNightMode(): Boolean {
        return mySharedPref.getBoolean("darkMode", false)
    }

    fun setSound(state: Boolean) {
        mySharedPref.edit().putBoolean("sound", state).apply()
    }

    fun getSound(): Boolean {
        return mySharedPref.getBoolean("sound", true)
    }

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

    fun loadLocale(context: Context) {
        val pref = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = pref.getString("language", Locale.getDefault().language)
            ?: Locale.getDefault().language
        setLocale(language, context)
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        return prefs.getString("language", Locale.getDefault().language)
            ?: Locale.getDefault().language
    }

    fun saveDifficulty(difficulty: String) {
        mySharedPref.edit().putString("difficulty", difficulty).apply()
    }

    fun loadDifficulty(): String {
        return mySharedPref.getString("difficulty", "easy") ?: "easy"
    }

    fun saveChosenGame(chosenGame: String) {
        mySharedPref.edit().putString("chosenGame", chosenGame).apply()
    }

    fun loadChosenGame(): String {
        return mySharedPref.getString("chosenGame", "") ?: ""
    }

    fun showCustomSnackbar(activity: Activity, message: String) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )

        val snackbarView = snackbar.view
        val backgroundColor = if (loadNightMode()) {
            ContextCompat.getColor(activity, R.color.snackbar_background_dark)
        } else {
            ContextCompat.getColor(activity, R.color.snackbar_background_light)
        }
        snackbarView.setBackgroundColor(backgroundColor)

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

    fun getHighScore(): Int {
        return mySharedPref.getInt("highScore", 0)
    }

    fun saveHighScore(newScore: Int) {
        val currentHighScore = getHighScore()
        if (newScore > currentHighScore) {
            mySharedPref.edit().putInt("highScore", newScore).apply()
        }
    }

    fun isFirstStart(): Boolean {
        return mySharedPref.getBoolean("firstStart", true)
    }

    fun setFirstStartShown() {
        mySharedPref.edit().putBoolean("firstStart", false).apply()
    }

    private val lastThrownDiceValueKey = "last_thrown_dice_value"

    fun saveLastThrownDiceValue(value: Int) {
        mySharedPref.edit().putInt(lastThrownDiceValueKey, value).apply()
    }

    fun loadLastThrownDiceValue(): Int {
        return mySharedPref.getInt(lastThrownDiceValueKey, 0)
    }
}




