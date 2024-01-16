package com.halfminute.itmadness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class ChooseLanguageGame : AppCompatActivity() {

    companion object {
        private const val TAG = "ChooseLanguageGame"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load user preferences for dark mode and set the theme accordingly
        val sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this) // Load the saved language preference

        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_language_game)

        // Handle back button press to navigate to the ChooseGame activity
        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back button pressed. Navigating to ChooseGame")
                val intent = Intent(this@ChooseLanguageGame, ChooseGame::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // Method to start the language test based on the button clicked
    fun startLanguageTest(view: View) {
        Settings.btnAnimation(view)

        val intent = Intent(this, LanguageGame::class.java)

        // Determine which game is selected based on the button's ID
        val chosenGame = when (view.id) {
            R.id.btnNlToEn -> "NlToEn"
            R.id.btnEnToNl -> "EnToNl"
            R.id.btnDeToEn -> "DeToEn"
            R.id.btnEnToDe -> "EnToDe"
            R.id.btnFrToEn -> "FrToEn"
            R.id.btnEnToFr -> "EnToFr"
            else -> {
                Log.w(TAG, "Unknown button clicked with ID: " + view.id)
                return // Return early if an unknown button is clicked
            }
        }

        Log.d(TAG, "Starting language test: $chosenGame")
        intent.putExtra("chosenGame", chosenGame)

        // Save the chosen LanguageGame mode to SharedPreferences
        val sharedPref = SharedPref(this)
        sharedPref.saveChosenGame(chosenGame)

        startActivity(intent)
        finish()
    }

}
