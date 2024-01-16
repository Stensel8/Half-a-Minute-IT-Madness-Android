package com.halfminute.itmadness

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity of the application, serving as the entry point.
 */
class MainActivity : AppCompatActivity() {

    // Broadcast receiver to handle language changes
    private lateinit var languageChangeReceiver: BroadcastReceiver

    // Shared preferences instance
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        sharedPref = SharedPref(this)

        // Setup theme and locale based on user preferences
        setupThemeAndLocale()

        // Set the layout for this activity
        setContentView(R.layout.activity_main)

        // Setup language change receiver
        setupLanguageChangeReceiver()

        // Show welcome message if it's the first start of the app
        showWelcomeMessageIfFirstStart()
    }

    /**
     * Sets up the theme and locale based on user preferences.
     */
    private fun setupThemeAndLocale() {
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)
    }

    /**
     * Shows the welcome message dialog if it's the first start of the app.
     */
    private fun showWelcomeMessageIfFirstStart() {
        if (sharedPref.isFirstStart()) {
            showWelcomeAndUpdates(this, sharedPref)
            sharedPref.setFirstStartShown()
        }
    }

    /**
     * Sets up a receiver to handle language changes.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupLanguageChangeReceiver() {
        languageChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "LANGUAGE_CHANGED") {
                    recreate() // Restart the activity to apply the language change
                }
            }
        }
        val intentFilter = IntentFilter("LANGUAGE_CHANGED")
        registerReceiverBasedOnApiVersion(intentFilter)
    }

    /**
     * Registers the language change receiver based on the API version.
     * @param intentFilter The intent filter for the receiver.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiverBasedOnApiVersion(intentFilter: IntentFilter) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(languageChangeReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(languageChangeReceiver, intentFilter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(languageChangeReceiver)
    }

    companion object {
        /**
         * Shows the welcome and updates dialog.
         * @param activity The activity context in which to show the dialog.
         * @param sharedPref The shared preferences instance to load settings from.
         */
        fun showWelcomeAndUpdates(activity: Activity, sharedPref: SharedPref) {
            val welcomeText = activity.getString(R.string.welcomeText)
            val dialogTheme =
                if (sharedPref.loadNightMode()) R.style.darkTheme_Dialog else R.style.lightTheme_Dialog
            AlertDialog.Builder(activity, dialogTheme)
                .setTitle(activity.getString(R.string.welcome))
                .setMessage(welcomeText)
                .setPositiveButton(activity.getString(R.string.show_releasenotes)) { dialog, _ ->
                    dialog.dismiss()
                    openGitHubRepository(activity)
                }
                .setNegativeButton(activity.getString(R.string.closeWindow)) { dialog, _ -> dialog.dismiss()
                }
                .show()
        }

        /**
         * Opens the GitHub repository page in a web browser.
         * @param activity The activity context used for launching intents.
         */
        private fun openGitHubRepository(activity: Activity) {
            val gitHubRepoUrl = "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(gitHubRepoUrl)))
        }
    }

    /**
     * Navigates to the game selection page.
     * @param view The view that triggers this action.
     */
    fun chooseGamePage(view: View?) {
        view?.let {
            Settings.btnAnimation(it)
            startActivity(Intent(this@MainActivity, ChooseGame::class.java))
            finish()
        }
    }

    /**
     * Navigates to the settings page.
     * @param view The view that triggers this action.
     */
    fun goSettings(view: View?) {
        view?.let {
            Settings.btnAnimation(it)
            startActivity(Intent(this@MainActivity, Settings::class.java))
            finish()
        }
    }
}
