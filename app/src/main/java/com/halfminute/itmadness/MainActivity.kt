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
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity of the application, serving as the entry point.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var languageChangeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = SharedPref(this)
        setupThemeAndLocale(sharedPref)
        setContentView(R.layout.activity_main)
        setupLanguageChangeReceiver()

        if (!sharedPref.isFirstStart() && !sharedPref.isUpdateDialogShown()) {
            DialogUtils.showStartDialog(this)
            sharedPref.setUpdateDialogShown()
        }
    }

    /**
     * Sets up the theme and locale based on user preferences.
     * @param sharedPref The shared preferences instance to load settings from.
     */
    private fun setupThemeAndLocale(sharedPref: SharedPref) {
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)
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

    /**
     * Utility object for showing dialogs.
     */
    object DialogUtils {
        /**
         * Shows the start dialog when the app is launched for the first time.
         * @param activity The activity context in which to show the dialog.
         */
        fun showStartDialog(activity: Activity) {
            // Inflate the layout with the appropriate root view.
            // 'activity.window.decorView.rootView' is used to provide a root view while not actually attaching the dialog view to it.
            val view = activity.layoutInflater.inflate(
                R.layout.welcome_message,
                activity.window.decorView.rootView as ViewGroup,
                false
            )
            setupDialogButtons(view, activity)
            logFirstStartPreference(activity)
        }


        /**
         * Sets up the buttons in the dialog.
         * @param view The view containing the dialog's buttons.
         * @param activity The activity context used for launching intents.
         */
        private fun setupDialogButtons(view: View, activity: Activity) {
            val btnViewReleaseNotes = view.findViewById<Button>(R.id.btnViewReleaseNotes)
            btnViewReleaseNotes.setOnClickListener {
                openGitHubPage(activity)
            }

            val dialog = AlertDialog.Builder(activity)
                .setView(view)
                .create()

            view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

        /**
         * Opens the GitHub releases page in a web browser.
         * @param activity The activity context used for launching intents.
         */
        private fun openGitHubPage(activity: Activity) {
            val url = "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        /**
         * Logs the value of the 'firstStart' preference before and after setting it.
         * @param activity The activity context used for accessing shared preferences.
         */
        private fun logFirstStartPreference(activity: Activity) {
            val sharedPrefs = activity.getSharedPreferences("welcomePreferences", MODE_PRIVATE)

            Log.d(
                "MainActivity",
                "Before setting firstStart: ${sharedPrefs.getBoolean("firstStart", true)}"
            )
            sharedPrefs.edit().apply {
                putBoolean("firstStart", false)
                apply()
            }

            Log.d(
                "MainActivity",
                "After setting firstStart: ${sharedPrefs.getBoolean("firstStart", true)}"
            )
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
            val editor = getSharedPreferences("activity", MODE_PRIVATE).edit()
            editor.putString("activity", "main")
            editor.apply()
            startActivity(Intent(this@MainActivity, Settings::class.java))
            finish()
        }
    }
}