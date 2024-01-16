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
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

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

    private fun setupThemeAndLocale(sharedPref: SharedPref) {
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)
    }

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

        // Check the API level and register the receiver with appropriate flag
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For API level 33 and above, use RECEIVER_NOT_EXPORTED
            registerReceiver(languageChangeReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // For API level 31 and 32, RECEIVER_NOT_EXPORTED is not available, so use the old way
            registerReceiver(languageChangeReceiver, intentFilter)
        } else {
            // For API level 30 and below, no need to specify
            registerReceiver(languageChangeReceiver, intentFilter)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(languageChangeReceiver)
    }

    object DialogUtils {
        fun showStartDialog(activity: Activity) {
            val builder = AlertDialog.Builder(activity)
            val view = activity.layoutInflater.inflate(R.layout.welcome_message, null)

            // Vind de 'View Release Notes' knop en stel een click listener in
            val btnViewReleaseNotes = view.findViewById<Button>(R.id.btnViewReleaseNotes)
            btnViewReleaseNotes.setOnClickListener {
                openGitHubPage(activity)
            }

            val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            btnClose.setOnClickListener { dialog.dismiss() }

            // Log de waarde voor het instellen van firstStart
            Log.d(
                "MainActivity",
                "Before setting firstStart: " + activity.getSharedPreferences(
                    "welcomePreferences",
                    MODE_PRIVATE
                ).getBoolean("firstStart", true)
            )

            // Stel de firstStart voorkeur in op false
            val editor = activity.getSharedPreferences("welcomePreferences", MODE_PRIVATE).edit()
            editor.putBoolean("firstStart", false)
            editor.apply()

            // Log de waarde na het instellen van firstStart
            Log.d(
                "MainActivity",
                "After setting firstStart: " + activity.getSharedPreferences(
                    "welcomePreferences",
                    MODE_PRIVATE
                ).getBoolean("firstStart", true)
            )
        }

        private fun openGitHubPage(activity: Activity) {
            val url = "https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            activity.startActivity(intent)
        }
    }

    fun chooseGamePage(view: View?) {
        Settings.btnAnimation(view!!)
        startActivity(Intent(this@MainActivity, ChooseGame::class.java))
        finish()
    }

    fun goSettings(view: View?) {
        Settings.btnAnimation(view!!)
        val editor = getSharedPreferences("activity", MODE_PRIVATE).edit()
        editor.putString("activity", "main")
        editor.apply()
        startActivity(Intent(this@MainActivity, Settings::class.java))
        finish()
    }
}
