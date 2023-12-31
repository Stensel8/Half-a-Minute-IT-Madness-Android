package com.halfminute.itmadness

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class ChooseGame : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref // Declare a SharedPref object to access the shared preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the SharedPref object with the current context
        sharedPref = SharedPref(this)

        // Set the theme according to the shared preference value
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)

        // Load the saved language
        sharedPref.loadLocale(this)

        setContentView(R.layout.choose_game) // Set the layout for this activity

        // Callback to handle the back button press
        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Create an intent to go back to the main activity
                val intent = Intent(this@ChooseGame, MainActivity::class.java)
                startActivity(intent) // Start the main activity
                finish() // Finish this activity
            }
        }
        onBackPressedDispatcher.addCallback(this, callback) // Add the callback
    }

    fun startMathGame(view: View) {
        Settings.btnAnimation(view) // Apply the button animation to the view
        val intent =
            Intent(this, MathGame::class.java) // Create an intent to start the math game activity
        startActivity(intent) // Start the math game activity
        finish() // Finish this activity
    }

    fun startLanguageGame(view: View) {
        Settings.btnAnimation(view) // Apply the button animation to the view
        val intent = Intent(
            this,
            ChooseLanguageGame::class.java
        ) // Create an intent to start the choose language game activity
        startActivity(intent) // Start the choose language game activity
        finish() // Finish this activity
    }

    // Start the guessing game
    fun startGuessingGame(view: View) {
        Settings.btnAnimation(view) // Apply the button animation to the view
        val intent = Intent(
            this,
            GuessingGame::class.java
        ) // Create an intent to start the guessing game activity
        startActivity(intent) // Start the guessing game activity
        finish() // Finish this activity
    }
}
