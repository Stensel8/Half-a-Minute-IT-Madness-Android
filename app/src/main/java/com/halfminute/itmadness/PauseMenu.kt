package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class PauseMenu : AppCompatActivity() {
    private var sharedPref: SharedPref? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // check dark mode
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref!!.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        sharedPref!!.loadLocale(this) // loads the saved language
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pause_menu)

        // Create a callback for onBackPressed
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBack()
            }
        }

        // Add the callback to the onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun goBack() {
        val intent: Intent = when (sharedPreferences!!.getString("actualGame", "")) {
            "math" -> Intent(this, MathGame::class.java)
            "NlToEn" -> Intent(this, LanguageGame::class.java).putExtra("chosenGame", "NlToEn")
            "EnToNl" -> Intent(this, LanguageGame::class.java).putExtra("chosenGame", "EnToNl")
            "FrToEn" -> Intent(this, LanguageGame::class.java).putExtra("chosenGame", "FrToEn")
            "EnToFr" -> Intent(this, LanguageGame::class.java).putExtra("chosenGame", "EnToFr")
            else -> throw IllegalStateException("Unknown game type")
        }
        startActivity(intent)
        finish()
    }

    fun resumeGame(view: View?) {
        Settings.btnAnimation(view)
        goBack()
    }

    fun goSettings(view: View?) {
        Settings.btnAnimation(view)
        val sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("activity", "pause")
        editor.apply()
        val intent = Intent(this@PauseMenu, Settings::class.java)
        startActivity(intent)
        finish()
    }

    fun goMain(view: View?) {
        Settings.btnAnimation(view)
        val intent = Intent(this@PauseMenu, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun changeGame(view: View?) {
        Settings.btnAnimation(view)
        val intent = Intent(this@PauseMenu, ChooseGame::class.java)
        startActivity(intent)
        finish()
    }
}
