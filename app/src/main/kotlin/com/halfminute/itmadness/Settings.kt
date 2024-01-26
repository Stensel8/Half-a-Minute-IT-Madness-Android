package com.halfminute.itmadness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

/**
 * Settings activity for the application, handling various user preferences.
 */
class Settings : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var darkModeIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPreferences()
        setContentView(R.layout.settings)
        initializeUI()
        handleBackPress()

        // Get the version and build numbers from the package info
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.longVersionCode

        // Format the version and build numbers
        val formattedVersion = getString(R.string.version_label) + " $versionName"
        val formattedBuild = getString(R.string.build_label) + " $versionCode"

        // Find the TextViews in the layout
        val tvBuildInfo: TextView = findViewById(R.id.tvBuildInfo)
        val tvVersionInfo: TextView = findViewById(R.id.tvVersionInfo)

        // Set the formatted version and build numbers to the TextViews
        tvBuildInfo.text = formattedBuild
        tvVersionInfo.text = formattedVersion

    }


    /**
     * Initializes user preferences and sets the app theme.
     */
    private fun initPreferences() {
        sharedPref = SharedPref(this)
        setAppTheme()
        sharedPref.loadLocale(this)
    }

    /**
     * Sets the application theme based on the saved night mode setting.
     */
    private fun setAppTheme() {
        val themeId = if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme
        setTheme(themeId)
    }

    /**
     * Initializes UI elements including dark mode icon and settings switches.
     */
    private fun initializeUI() {
        darkModeIcon = findViewById(R.id.darkModeIcon)
        updateDarkModeIcon(sharedPref.loadNightMode())
        setupUIElements()
    }

    /**
     * Sets up UI elements related to user settings.
     */
    private fun setupUIElements() {
        setupSwitches()
        setupLanguageButton()
        setupWelcomeAndUpdatesButton()
    }

    /**
     * Configures the back press action to navigate to the main activity.
     */
    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMainActivity()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    /**
     * Navigates back to the main activity.
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Sets up toggle switches for dark mode and sound preferences.
     */
    private fun setupSwitches() {
        setupDarkModeToggle()
        setupSoundToggle()
    }

    /**
     * Configures the dark mode switch, updating the preference and UI when changed.
     */
    private fun setupDarkModeToggle() {
        val darkModeToggle: SwitchCompat = findViewById(R.id.darkModeToggle)
        darkModeToggle.isChecked = sharedPref.loadNightMode()
        darkModeToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.setNightMode(isChecked)
            updateDarkModeIcon(isChecked)
            recreate()
        }
    }

    /**
     * Configures the sound switch, saving the preference when changed.
     */
    private fun setupSoundToggle() {
        val soundToggle: SwitchCompat = findViewById(R.id.soundToggle)
        soundToggle.isChecked = sharedPref.getSound()
        soundToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.setSound(isChecked)
        }
    }

    /**
     * Handler for selecting the difficulty level.
     * @param view The view that triggered the difficulty selection.
     */
    fun chooseDifficulty(view: View) {
        val difficultyKey = getDifficultyKey(view.id)
        // Call the animation function before performing other actions
        btnAnimation(view)
        sharedPref.saveDifficulty(difficultyKey)
        displayDifficultyChange(difficultyKey)
    }



    /**
     * Determines the difficulty key based on the selected button's ID.
     * @param viewId The ID of the view that triggered the difficulty selection.
     * @return The difficulty key as a string.
     */
    private fun getDifficultyKey(viewId: Int): String = when (viewId) {
        R.id.difficultyEasyBtn -> "EASY"
        R.id.difficultyMediumBtn -> "MEDIUM"
        R.id.difficultyHardBtn -> "HARD"
        else -> "EASY"
    }

    /**
     * Displays a snackbar message indicating the selected difficulty level.
     * @param difficultyKey The key representing the selected difficulty.
     */
    private fun displayDifficultyChange(difficultyKey: String) {
        val difficultyString = getString(
            when (difficultyKey) {
                "EASY" -> R.string.difficultyEasy
                "MEDIUM" -> R.string.difficultyMedium
                "HARD" -> R.string.difficultyHard
                else -> R.string.difficultyEasy
            }
        )
        sharedPref.showCustomSnackbar(
            this,
            "${getString(R.string.changeDifficultyTo)} $difficultyString"
        )
    }

    /**
     * Sets up the language change button to open a language selection dialog.
     */
    private fun setupLanguageButton() {
        val changeLang: Button = findViewById(R.id.btnChangeLanguage)
        changeLang.setOnClickListener {
            // Call the animation function before performing other actions
            btnAnimation(changeLang)

            // Add your existing logic for language selection here
            showChangeLanguageDialog()
        }
    }


    /**
     * Sets up the welcome and updates button to show relevant information.
     */
    private fun setupWelcomeAndUpdatesButton() {
        val btnShowWelcomeAndUpdates: Button = findViewById(R.id.btnShowWelcomeAndUpdates)
        btnShowWelcomeAndUpdates.setOnClickListener {
            // Call the animation function before performing other actions
            btnAnimation(btnShowWelcomeAndUpdates)
            showWelcomeAndUpdates()
        }
    }


    /**
     * Shows a dialog allowing the user to choose the application language.
     */
    private fun showChangeLanguageDialog() {
        val listOfLanguages = arrayOf("German", "Français", "Nederlands", "English")
        val dialogTheme =
            if (sharedPref.loadNightMode()) R.style.darkTheme_Dialog else R.style.lightTheme_Dialog
        AlertDialog.Builder(this, dialogTheme)
            .setSingleChoiceItems(listOfLanguages, -1) { dialog, which ->
                handleLanguageSelection(which)
                dialog.dismiss()
            }.create().show()
    }

    /**
     * Handles the language selection from the dialog.
     * @param index The index of the selected language in the dialog.
     */
    private fun handleLanguageSelection(index: Int) {
        val languageCode = when (index) {
            0 -> "de"
            1 -> "fr"
            2 -> "nl"
            else -> "en"
        }
        sharedPref.setLocale(languageCode, this)
        applyLanguageChange()
    }

    /**
     * Applies the language change and restarts the current activity to reflect the change.
     */
    @Suppress("DEPRECATION")
    private fun applyLanguageChange() {
        startActivity(Intent(this, Settings::class.java))
        finish()
        overridePendingTransition(R.anim.dialog_fade_in, R.anim.dialog_fade_out)
    }

    /**
     * Shows an AlertDialog with the welcome message and options to view updates.
     */
    private fun showWelcomeAndUpdates() {
        val welcomeText = getString(R.string.welcomeText)
        val dialogTheme =
            if (sharedPref.loadNightMode()) R.style.darkTheme_Dialog else R.style.lightTheme_Dialog
        AlertDialog.Builder(this, dialogTheme)
            .setTitle(getString(R.string.welcome))
            .setMessage(welcomeText)
            .setPositiveButton(getString(R.string.show_releasenotes)) { dialog, _ ->
                dialog.dismiss()
                openGitHubRepository()
            }
            .setNegativeButton(getString(R.string.closeWindow)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Opens the GitHub repository in a web browser.
     */
    private fun openGitHubRepository() {
        val gitHubRepoUrl = "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(gitHubRepoUrl)))
    }

    /**
     * Updates the dark mode icon based on the current mode.
     * @param isNightMode Boolean indicating if night mode is enabled.
     */
    private fun updateDarkModeIcon(isNightMode: Boolean) {
        darkModeIcon.setImageResource(
            if (isNightMode) R.drawable.dark_mode else R.drawable.light_mode
        )
    }

    companion object {
        /**
         * Provides an animation effect for buttons when pressed.
         * @param view The view (button) to apply the animation to.
         */
        fun btnAnimation(view: View) {
            val animation = AnimationUtils.loadAnimation(view.context, R.anim.button_click_animation)
            view.startAnimation(animation)
        }
    }

}
