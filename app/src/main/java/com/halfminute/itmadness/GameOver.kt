package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {

    private lateinit var tlScore: TableLayout
    private lateinit var ivHighScore: ImageView
    private lateinit var tvHighScore: TextView
    private lateinit var tvTop2: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvChosenGame: TextView
    private lateinit var tvDifficulty: TextView

    private var player: MediaPlayer? = null
    private var points: Int = 0
    private lateinit var difficulty: String
    private lateinit var chosenGame: String

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        tlScore = findViewById(R.id.tlScore)
        ivHighScore = findViewById(R.id.ivHighScore)
        tvHighScore = findViewById(R.id.tvHighScore)
        tvTop2 = findViewById(R.id.tvTop2)
        tvPoints = findViewById(R.id.tvPoints)
        tvChosenGame = findViewById(R.id.tvChosenGame)
        tvDifficulty = findViewById(R.id.tvDifficultyGOver)

        points = intent.extras!!.getInt("points")
        difficulty = intent.extras!!.getString("difficulty")!!
        chosenGame = intent.extras!!.getString("chosenGame")!!

        sharedPreferences = getSharedPreferences("pref", 0)
        sessionManager = SessionManager(this)
        sharedPref = SharedPref(this)

        val pointsHC = sessionManager.highScore

        if (points > pointsHC) {
            sessionManager.saveHighScore(points)
            applause()
            ivHighScore.visibility = View.VISIBLE
        }

        tvPoints.text = points.toString()
        tvHighScore.text = sessionManager.highScore.toString()
    }

    private fun applause() {
        if (sharedPref.sound) {
            player = MediaPlayer.create(this, R.raw.applause)
            player?.start()
            player?.setOnCompletionListener { stopPlayer() }
        }
    }

    fun main(view: View) {
        Settings.btnAnimation(view)

        val intent = Intent(this@GameOver, MainActivity::class.java)
        stopPlayer()
        startActivity(intent)
        finish()
    }

    private fun stopPlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    fun restart(view: View) {
        Settings.btnAnimation(view)

        val intent: Intent
        val sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val actual = sharedPreferences.getString("actualGame", "") ?: ""
        stopPlayer()

        when (actual) {
            "math" -> {
                intent = Intent(this, MathGame::class.java)
                startActivity(intent)
            }

            "NlToEn" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "NlToEn")
                startActivity(intent)
            }

            "EnToNl" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "EnToNl")
                startActivity(intent)
            }

            "FrToEn" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "FrToEn")
                startActivity(intent)
            }

            "EnToFr" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "EnToFr")
                startActivity(intent)
            }

            "EnToDe" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "EnToDe")
                startActivity(intent)
            }

            "DeToEn" -> {
                intent = Intent(this, LanguageGame::class.java)
                intent.putExtra("chosenGame", "DeToEn")
                startActivity(intent)
            }
        }
        finish()
    }

    fun changeGame(view: View) {
        Settings.btnAnimation(view)

        val intent = Intent(this@GameOver, ChooseGame::class.java)
        stopPlayer()
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        Settings.btnAnimation(view)

        finish()
    }
}
