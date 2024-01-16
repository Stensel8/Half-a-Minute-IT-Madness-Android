package com.halfminute.itmadness

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {

    // Views
    private lateinit var tlScore: TableLayout
    private lateinit var ivHighScore: ImageView
    private lateinit var tvHighScore: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvChosenGame: TextView
    private lateinit var tvDifficulty: TextView

    // Sound Player
    private var player: MediaPlayer? = null

    // Game Points
    private var points: Int = 0

    // Managers
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPref and set the theme based on the dark mode setting
        initializePreferencesAndSetTheme()

        // Set the content view of the activity
        setContentView(R.layout.game_over)

        // Initialize views and perform the rest of the setup
        initializeViews()
        initializeManagers()
        retrieveIntentData()
        checkAndDisplayHighScore()
    }

    // Initialize Shared Preferences and Set Theme
    private fun initializePreferencesAndSetTheme() {
        sharedPref = SharedPref(this)
        val themeId = if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme
        setTheme(themeId)
    }

    // Initialize Views
    private fun initializeViews() {
        tlScore = findViewById(R.id.tlScore)
        ivHighScore = findViewById(R.id.ivHighScore)
        tvHighScore = findViewById(R.id.tvHighScore)
        tvPoints = findViewById(R.id.tvPoints)
        tvChosenGame = findViewById(R.id.tvChosenGame)
        tvDifficulty = findViewById(R.id.tvDifficultyGOver)
    }

    // Initialize Managers
    private fun initializeManagers() {
        sessionManager = SessionManager(this)
        sharedPref = SharedPref(this)
    }

    // Retrieve Intent Data
    private fun retrieveIntentData() {
        points = intent?.extras?.getInt("points") ?: 0
        tvPoints.text = points.toString()
        tvChosenGame.text = intent?.extras?.getString("chosenGame") ?: ""
        tvDifficulty.text = intent?.extras?.getString("difficulty") ?: ""
    }

    // Check and Display High Score
    private fun checkAndDisplayHighScore() {
        val highScore = sessionManager.highScore
        if (points > highScore) {
            sessionManager.saveHighScore(points)
            playApplause()
            ivHighScore.visibility = View.VISIBLE
        }
        tvHighScore.text = highScore.toString()
    }

    // Play Applause Sound
    private fun playApplause() {
        if (sharedPref.getSound()) {
            player = MediaPlayer.create(this, R.raw.applause).apply {
                start()
                setOnCompletionListener { releasePlayer() }
            }
        }
    }

    // Release MediaPlayer
    private fun releasePlayer() {
        player?.release()
        player = null
    }

    // Navigate to Main Activity
    fun main(view: View) {
        applyButtonAnimation(view)
        navigateTo(MainActivity::class.java)
    }

    // Restart the Game
    fun restart(view: View) {
        applyButtonAnimation(view)
        val chosenGame = sharedPref.loadChosenGame() // Load the chosen game
        navigateToGame(chosenGame)
    }

    // Navigate to a Specific Game
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

    // Change the Game
    fun changeGame(view: View) {
        applyButtonAnimation(view)
        navigateTo(ChooseGame::class.java)
    }

    // Exit the Game
    fun exit(view: View) {
        applyButtonAnimation(view)
        finish()
    }

    // Apply Button Animation
    private fun applyButtonAnimation(view: View) {
        Settings.btnAnimation(view)
    }

    // Navigate to a Specific Destination
    private fun navigateTo(destination: Class<*>) {
        releasePlayer()
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

}
