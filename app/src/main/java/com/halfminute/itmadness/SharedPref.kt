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

    fun setLocale(lang: String, context: Context) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // This one is deprecated, but I couldn't get the newer method working.
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val prefs = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        prefs.edit().putString("language", lang).apply()
    }


    fun loadLocale(context: Context) {
        val pref = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language =
            pref.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language
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

    // Toegevoegde functies voor eerste start en update dialogen
    fun isFirstStart(): Boolean {
        return mySharedPref.getBoolean("firstStart", true)
    }

    fun setFirstStartShown() {
        mySharedPref.edit().putBoolean("firstStart", false).apply()
    }

    fun isUpdateDialogShown(): Boolean {
        return mySharedPref.getBoolean("updateDialogShown", false)
    }

    fun setUpdateDialogShown() {
        mySharedPref.edit().putBoolean("updateDialogShown", true).apply()
    }

    fun showCustomSnackbar(activity: Activity, message: String) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )

        // Pas de achtergrondkleur van de Snackbar aan op basis van het thema
        val snackbarView = snackbar.view
        val backgroundColor = if (loadNightMode()) {
            ContextCompat.getColor(activity, R.color.snackbar_background_dark)
        } else {
            ContextCompat.getColor(activity, R.color.snackbar_background_light)
        }
        snackbarView.setBackgroundColor(backgroundColor)

        // Pas de tekstkleur van de Snackbar aan
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


}
