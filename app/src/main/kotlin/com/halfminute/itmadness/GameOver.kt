package com.halfminute.itmadness

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity displayed when the game is over.
 */
class GameOver : AppCompatActivity() {

    // Views
    private lateinit var tlScore: TextView
    private lateinit var ivHighScore: ImageView
    private lateinit var tvHighScore: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvChosenGame: TextView
    private lateinit var tvDifficulty: TextView

    // Sound Player
    private var player: MediaPlayer? = null

    // Game Points
    private var points: Int = 0

    // Shared Preferences
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePreferencesAndSetTheme()

        setContentView(R.layout.game_over)
        initializeViews()
        retrieveIntentData()
        checkAndDisplayHighScore()
    }

    /**
     * Initializes shared preferences and sets the theme based on user preferences.
     */
    private fun initializePreferencesAndSetTheme() {
        sharedPref = SharedPref(this)
        val themeId = if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme
        setTheme(themeId)
    }

    /**
     * Initializes the UI views.
     */
    private fun initializeViews() {
        tlScore = findViewById(R.id.tlScore)
        ivHighScore = findViewById(R.id.ivHighScore)
        tvHighScore = findViewById(R.id.tvHighScore)
        tvPoints = findViewById(R.id.tvPoints)
        tvChosenGame = findViewById(R.id.tvChosenGame)
        tvDifficulty = findViewById(R.id.tvDifficultyGOver)
    }

    /**
     * Retrieves data from the intent extras.
     */
    private fun retrieveIntentData() {
        points = intent?.extras?.getInt("points") ?: 0
        tvPoints.text = points.toString()
        tvChosenGame.text = intent?.extras?.getString("chosenGame") ?: ""
        tvDifficulty.text = intent?.extras?.getString("difficulty") ?: ""
    }

    /**
     * Checks and displays the high score if the current score is higher.
     */
    private fun checkAndDisplayHighScore() {
        val highScore = sharedPref.getHighScore()
        if (points > highScore) {
            sharedPref.saveHighScore(points)
            playApplause()
            ivHighScore.visibility = View.VISIBLE
        }
        tvHighScore.text = highScore.toString()
    }

    /**
     * Plays the applause sound if sound is enabled in settings.
     */
    private fun playApplause() {
        if (sharedPref.getSound()) {
            player = MediaPlayer.create(this, R.raw.applause).apply {
                start()
                setOnCompletionListener { releasePlayer() }
            }
        }
    }

    /**
     * Releases the MediaPlayer.
     */
    private fun releasePlayer() {
        player?.release()
        player = null
    }

    /**
     * Navigates back to the main activity.
     */
    fun main(view: View) {
        applyButtonAnimation(view)
        try {
            navigateTo(MainActivity::class.java)
        } catch (e: Exception) {
            val sharedPref = SharedPref(this)
            sharedPref.showCustomSnackbar(this, "Er is een fout opgetreden!")
        }
    }


    /**
     * Restarts the current game.
     */
    fun restart(view: View) {
        applyButtonAnimation(view)
        try {
            val chosenGame = sharedPref.loadChosenGame() // Load the chosen game
            navigateToGame(chosenGame)
        } catch (e: Exception) {
            val sharedPref = SharedPref(this)
            sharedPref.showCustomSnackbar(this, "Er is een fout opgetreden!")
        }

    }

    /**
     * Navigates to a specific game based on the chosen game type.
     */
    private fun navigateToGame(game: String) {
        val intent = when (game) {
            "math" -> Intent(this, MathGame::class.java)
            "guessing" -> Intent(this, GuessingGame::class.java)
            "NlToEn", "EnToNl", "FrToEn", "EnToFr", "EnToDe", "DeToEn" -> Intent(
                this,
                LanguageGame::class.java
            ).apply {
                putExtra("chosenGame", game)
            }
            else -> Intent(this, MainActivity::class.java) // Default to Main if unknown
        }
        intent.let {
            releasePlayer()
            startActivity(it)
            finish()
        }
    }

    /**
     * Navigates to the game selection page.
     */
    fun changeGame(view: View) {
        applyButtonAnimation(view)
        navigateTo(ChooseGame::class.java)
    }

    /**
     * Exits the game and finishes the activity.
     */
    fun exit(view: View) {
        applyButtonAnimation(view)
        finish()
    }

    /**
     * Applies a button animation to the clicked view.
     */
    private fun applyButtonAnimation(view: View) {
        Settings.btnAnimation(view)
    }

    /**
     * Navigates to a specific destination.
     */
    private fun navigateTo(destination: Class<*>) {
        releasePlayer()
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}
