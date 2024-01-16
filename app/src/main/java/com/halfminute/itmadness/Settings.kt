package com.halfminute.itmadness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class Settings : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var darkModeIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)

        setContentView(R.layout.settings)

        initializeUI()

        // Implement a way to handle the OnBackPressed callback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Start the MainActivity
                val intent = Intent(this@Settings, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initializeUI() {
        darkModeIcon = findViewById(R.id.darkModeIcon)
        updateDarkModeIcon(sharedPref.loadNightMode())
        setupSwitches()
        setupLanguageButton()
        setupWelcomeAndUpdatesButton()
    }

    private fun setupSwitches() {
        val darkModeToggle: SwitchCompat = findViewById(R.id.darkModeToggle)
        darkModeToggle.isChecked = sharedPref.loadNightMode()
        darkModeToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.setNightMode(isChecked)
            updateDarkModeIcon(isChecked)
            recreate()
        }

        val soundToggle: SwitchCompat = findViewById(R.id.soundToggle)
        soundToggle.isChecked = sharedPref.getSound()
        soundToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.setSound(isChecked)
        }
    }

    // Method to handle difficulty button clicks
    fun chooseDifficulty(view: View) {
        val difficultyKey = when (view.id) {
            R.id.difficultyEasyBtn -> "EASY"
            R.id.difficultyMediumBtn -> "MEDIUM"
            R.id.difficultyHardBtn -> "HARD"
            else -> "EASY" // Standaardwaarde
        }
        sharedPref.saveDifficulty(difficultyKey)

        // Haal de gelokaliseerde string op voor weergave
        val difficultyString = when (difficultyKey) {
            "EASY" -> getString(R.string.difficultyEasy)
            "MEDIUM" -> getString(R.string.difficultyMedium)
            "HARD" -> getString(R.string.difficultyHard)
            else -> getString(R.string.difficultyEasy)
        }

        // Toon de gelokaliseerde string in de Snackbar
        sharedPref.showCustomSnackbar(
            this,
            "${getString(R.string.changeDfficultyTo)} $difficultyString"
        )
    }


    private fun setupLanguageButton() {
        val changeLang: Button = findViewById(R.id.btnChangeLanguage)
        changeLang.setOnClickListener { showChangeLanguageDialog() }
    }

    private fun setupWelcomeAndUpdatesButton() {
        val btnShowWelcomeAndUpdates: Button = findViewById(R.id.btnShowWelcomeAndUpdates)
        btnShowWelcomeAndUpdates.setOnClickListener { showWelcomeAndUpdates() }
    }

    private fun showChangeLanguageDialog() {
        val listOfLang = arrayOf("German", "Français", "Nederlands", "English")
        val dialogTheme =
            if (sharedPref.loadNightMode()) R.style.darkTheme_Dialog else R.style.lightTheme_Dialog
        val builder = AlertDialog.Builder(this, dialogTheme)
        builder.setSingleChoiceItems(listOfLang, -1) { dialog, i ->
            val languageCode = when (i) {
                0 -> "de"
                1 -> "fr"
                2 -> "nl"
                else -> "en"
            }
            sharedPref.setLocale(languageCode, this)
            applyLanguageChange()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun applyLanguageChange() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun showWelcomeAndUpdates() {
        val welcomeText = getString(R.string.welcomeText)
        val dialogTheme =
            if (sharedPref.loadNightMode()) R.style.darkTheme_Dialog else R.style.lightTheme_Dialog

        AlertDialog.Builder(this, dialogTheme)
            .setTitle(getString(R.string.welcome))
            .setMessage(welcomeText)
            .setPositiveButton(getString(R.string.show_releasenotes)) { dialog, _ ->
                dialog.dismiss()
                // Hier kun je de knop toevoegen om naar de GitHub-repository te gaan
                openGitHubRepository()
            }
            .setNegativeButton(getString(R.string.closeWindow)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun openGitHubRepository() {
        val gitHubRepoUrl = "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gitHubRepoUrl))
        startActivity(intent)
    }


    private fun updateDarkModeIcon(isNightMode: Boolean) {
        darkModeIcon.setImageResource(if (isNightMode) R.drawable.dark_mode else R.drawable.light_mode)
    }

    companion object {
        fun btnAnimation(view: View) {
            val animation = AlphaAnimation(0.2f, 1.0f).apply { duration = 500 }
            view.startAnimation(animation)
        }
    }
}
