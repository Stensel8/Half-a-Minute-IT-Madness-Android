package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class PauseMenu : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences and set theme based on user settings
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        sharedPref.loadLocale(this)

        // Set the content view to the pause menu layout
        setContentView(R.layout.pause_menu)

        // Handle back button press with OnBackPressedCallback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                resumeCurrentGame()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    /**
     * Resumes the currently paused game by navigating to the appropriate activity.
     */
    private fun resumeCurrentGame() {
        val intent = when (val chosenGame = sharedPref.loadChosenGame()) { // Load the chosen game
            "math" -> Intent(this, MathGame::class.java)
            "guessing" -> Intent(this, GuessingGame::class.java)
            "NlToEn", "EnToNl", "FrToEn", "EnToFr", "EnToDe", "DeToEn" -> Intent(
                this,
                LanguageGame::class.java
            ).apply {
                putExtra("chosenGame", chosenGame)
            }
            else -> Intent(this, MainActivity::class.java) // Default to Main if unknown
        }
        startActivity(intent)
        finish()
    }

    /**
     * Function to resume the game when the "Resume" button is clicked.
     */
    fun resumeGame(view: View?) {
        if (view != null) {
            Settings.btnAnimation(view)
        }
        resumeCurrentGame()
    }

    /**
     * Function to navigate to the settings when the "Settings" button is clicked.
     */
    fun goSettings(view: View?) {
        if (view != null) {
            Settings.btnAnimation(view)
        }
        val sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("activity", "pause")
        editor.apply()
        val intent = Intent(this@PauseMenu, Settings::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Function to navigate to the main menu when the "Main Menu" button is clicked.
     */
    fun goMain(view: View?) {
        if (view != null) {
            Settings.btnAnimation(view)
        }
        val intent = Intent(this@PauseMenu, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Function to navigate to the choose game screen when the "Change Game" button is clicked.
     */
    fun changeGame(view: View?) {
        if (view != null) {
            Settings.btnAnimation(view)
        }
        val intent = Intent(this@PauseMenu, ChooseGame::class.java)
        startActivity(intent)
        finish()
    }
}
