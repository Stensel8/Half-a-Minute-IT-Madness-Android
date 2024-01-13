package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import java.util.Locale
import java.util.Random


//TODO: Fix the logic inside the LanguageGame.kt. Currently, it's just using wrong languages.
// I think this is because of the buttons that i accidentally "swapped" for the wrong functions.

//TODO: Also, I think I forgot something,
// because the German language is crashing when on medium or hard mode. Easy one is working just fine.
class LanguageGame : AppCompatActivity() {
    private var sharedPref: SharedPref? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pref: SharedPreferences? = null
    private var random: Random? = null
    private var correctAnswer: String? = null
    private var savedLanguage: String? = null
    var chosenGame: String? = null
    private var difficulty: String? = null
    private var tvQuestion: TextView? = null
    var tvTimer: TextView? = null
    private var tvPoints: TextView? = null
    private var tvResult: TextView? = null
    private var tvLives: TextView? = null
    private var tvDifficulty: TextView? = null
    private var btn0: Button? = null
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var btn3: Button? = null
    private var clickedBtn: Button? = null
    var countDownTimer: CountDownTimer? = null
    var millisUntilFinished: Long = 0
    private var points = 0
    private var wrong = 0
    private var maxWrongAnswers = 0
    private var numberOfQuestions = 0
    private var randomId = 0
    private var previousRandom = 0
    private lateinit var btnIds: IntArray
    private var correctAnswerPosition = 0
    private var incorrectAnswers: ArrayList<String>? = null
    private var gson: Gson? = null
    private var wordsList: Words? = null
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        // Check dark mode
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref!!.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref!!.loadLocale(this) // Loads the saved language
        pref = getSharedPreferences("Settings", MODE_PRIVATE)
        savedLanguage =
            pref?.getString("My lang", Locale.getDefault().language) // Current language of the game
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_test)

        // Initialize variables
        correctAnswer = "" // The answer of the question
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvResult = findViewById(R.id.tvResult2)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        tvLives = findViewById(R.id.tvLives)
        tvDifficulty = findViewById(R.id.tvDifficulty2)
        millisUntilFinished = 30100 // 30 seconds used for the timer
        points = 0
        wrong = 0 // The wrong answers from the player
        maxWrongAnswers = 2 // The maximum wrong answers allowed
        numberOfQuestions = 0
        random = Random()
        randomId = 0 // Random used to get the question
        previousRandom =
            Int.MAX_VALUE // Random from the previous question // It is initialized to max Int to be sure that the first random will not be the same as this random
        btnIds = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3) // The id's of the 4 buttons
        correctAnswerPosition = 0
        incorrectAnswers = ArrayList() // ArrayList with all the incorrect answers
        gson = Gson() // A Google library used to facilitate the use of JSON files
        wordsList = gson!!.fromJson(
            WordsJson.myWords, Words::class.java
        ) // We get the JSON string in the WordsJson class
        chosenGame = intent.getStringExtra("chosenGame")
        sharedPreferences = getSharedPreferences("gameDifficulty", MODE_PRIVATE)
        difficulty = sharedPreferences?.getString("difficulty", "easy") ?: "easy"


        // Create a callback for onBackPressed
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    countDownTimer?.cancel()
                    val intent = Intent(this@LanguageGame, ChooseLanguageGame::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        onBackPressedDispatcher.addCallback(
            this, callback
        ) // Add the callback to the onBackPressedDispatcher
        startGame()

    }

    fun pauseGame(view: View) {
        // Saves the current game we're playing
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.putString("actualGame", chosenGame)
        editor.apply()
        countDownTimer!!.cancel()
        val intent = Intent(this@LanguageGame, PauseMenu::class.java)
        releasePlayer()
        startActivity(intent)
        finish()
    }

    private fun startGame() {
        tvTimer!!.text = getString(R.string.timer_seconds, millisUntilFinished / 1000)
        tvPoints!!.text = getString(R.string.score_format, points, numberOfQuestions)
        generateQuestion()
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val initialColor = tvTimer!!.currentTextColor
                tvTimer!!.text = getString(R.string.timer_seconds, seconds)
                if (seconds <= 5) {
                    if (seconds == 5L) {
                        timerPlayer =
                            MediaPlayer.create(this@LanguageGame, R.raw.five_sec_countdown)
                        playTimerSound()
                    }
                    tvTimer!!.setTextColor(resources.getColor(R.color.wrong, theme))
                    tvTimer!!.textSize = 26f
                    Handler(Looper.getMainLooper()).postDelayed({

                        // set it initial color and size
                        tvTimer!!.textSize = 24f
                        tvTimer!!.setTextColor(initialColor)
                    }, 300)
                }
            }

            override fun onFinish() {
                gameOver()
            }
        }.start()
    }

    private fun playTimerSound() {
        if (sharedPref!!.sound) {
            startPlayer(timerPlayer)
        }
    }

    // Define a data class to represent the game difficulty
    private fun generateQuestion() {
        numberOfQuestions++

        Log.d("Language", "Saved Language: $savedLanguage")
/////////////////////// easy mode /////////////////////////
        if (difficulty.equals("easy", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyEasy)
            randomId = random!!.nextInt(wordsList!!.easyWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(wordsList!!.easyWords.size)
            }
            maxWrongAnswers = 5
            when (savedLanguage) {
                "fr" -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.easyWords[randomId].frWord
                        }
                    }
                }
            }

///////////////////// medium mode /////////////////////////
        } else if (difficulty.equals("medium", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyMedium)
            randomId = random!!.nextInt(wordsList!!.mediumWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(wordsList!!.mediumWords.size)
            }
            var question: String
            if (savedLanguage == "de") {
                when (chosenGame) {
                    "NlToEn" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumNlWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                    }

                    "EnToNl" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumNlWord()
                    }

                    "DeToEn" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumDeWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                    }

                    "EnToDe" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumDeWord()
                    }

                    "FrToEn" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumFrWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                    }

                    "EnToFr" -> {
                        question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].getMediumFrWord()
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumFrWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumNlWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumFrWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumNlWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumFrWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumNlWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumNlWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].getMediumEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].getMediumFrWord()
                        }
                    }
                }
            }

/////////////////////// hard mode /////////////////////////
        } else {
            tvDifficulty!!.text = resources.getString(R.string.difficultyHard)
            randomId = random!!.nextInt(wordsList!!.hardWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(wordsList!!.hardWords.size)
            }
            var question: String
            if (savedLanguage == "de") {
                when (chosenGame) {
                    "NlToEn" -> {
                        question = wordsList!!.hardWords[randomId].getHardNlWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                    }

                    "EnToNl" -> {
                        question = wordsList!!.hardWords[randomId].getHardEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardNlWord()
                    }

                    "DeToEn" -> {
                        question = wordsList!!.hardWords[randomId].getHardDeWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                    }

                    "EnToDe" -> {
                        question = wordsList!!.hardWords[randomId].getHardEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardDeWord()
                    }

                    "FrToEn" -> {
                        question = wordsList!!.hardWords[randomId].getHardFrWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                    }

                    "EnToFr" -> {
                        question = wordsList!!.hardWords[randomId].getHardEnWord()
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].getHardFrWord()
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardFrWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].getHardFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardNlWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardFrWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].getHardFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardNlWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].getHardNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardFrWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardNlWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].getHardNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardNlWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardNlWord()
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardDeWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardDeWord()
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].getHardFrWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardEnWord()
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].getHardEnWord()
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].getHardFrWord()
                        }
                    }
                }
            }
        }

        //we store the random to check it on the next question
        previousRandom = randomId

        //update the live of the player on every question
        tvLives!!.text = getString(R.string.lives, maxWrongAnswers + 1 - wrong)

        //little bonus when you're at the 30th question
        if (numberOfQuestions == 31) {
            correctAnswer = "30 seconds game"
            tvQuestion!!.text = getString(R.string.game_time)
        }

        //get a random position between the 4 buttons
        correctAnswerPosition = random!!.nextInt(4)
        (findViewById<View>(btnIds[correctAnswerPosition]) as Button).text = correctAnswer
        setIncorrectAnswers()
    }

    //assign the incorrect answers to the other buttons
    private fun setIncorrectAnswers() {
        while (true) {

            ///////////////////// easy mode //////////////////////////
            var incorrectAnswer = ""
            if (difficulty.equals("easy", ignoreCase = true) && incorrectAnswers!!.size <= 3) {
                randomId = random!!.nextInt(wordsList!!.easyWords.size)

                when (savedLanguage) {
                    "de" -> {
                        incorrectAnswer = when (chosenGame) {
                            "NlToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToNl" -> wordsList!!.easyWords[randomId].getEasyNlWord()
                            "DeToEn" -> wordsList!!.easyWords[randomId].getEasyEnWord()
                            "EnToDe" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "FrToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToFr" -> wordsList!!.easyWords[randomId].getEasyEnWord()

                            else -> ""
                        }
                    }

                    "fr" -> {
                        incorrectAnswer = when (chosenGame) {
                            "NlToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToNl" -> wordsList!!.easyWords[randomId].getEasyNlWord()
                            "DeToEn" -> wordsList!!.easyWords[randomId].getEasyEnWord()
                            "EnToDe" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "FrToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToFr" -> wordsList!!.easyWords[randomId].getEasyEnWord()

                            else -> ""
                        }
                    }

                    "nl" -> {
                        incorrectAnswer = when (chosenGame) {
                            "NlToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToNl" -> wordsList!!.easyWords[randomId].getEasyNlWord()
                            "DeToEn" -> wordsList!!.easyWords[randomId].getEasyEnWord()
                            "EnToDe" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "FrToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToFr" -> wordsList!!.easyWords[randomId].getEasyEnWord()

                            else -> ""
                        }
                    }

                    else -> {
                        incorrectAnswer = when (chosenGame) {
                            "NlToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToNl" -> wordsList!!.easyWords[randomId].getEasyNlWord()
                            "DeToEn" -> wordsList!!.easyWords[randomId].getEasyEnWord()
                            "EnToDe" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "FrToEn" -> wordsList!!.easyWords[randomId].getEasyDeWord()
                            "EnToFr" -> wordsList!!.easyWords[randomId].getEasyEnWord()

                            else -> ""
                        }
                    }
                }


                // To make sure we never get multiple correct answers
                if (incorrectAnswer == correctAnswer) continue
                //to make sure we never get the same incorrect answer on the other buttons
                if (incorrectAnswers!!.contains(incorrectAnswer)) continue
                incorrectAnswers!!.add(incorrectAnswer)

                ///////////////////// medium mode //////////////////////////
            } else if (difficulty.equals("medium", ignoreCase = true)) {
                if (incorrectAnswers!!.size > 3) {
                    break
                }
                randomId = random!!.nextInt(wordsList!!.mediumWords.size)
                when (savedLanguage) {
                    "fr" -> {
                        when (chosenGame) {
                            "NlToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToNl" -> wordsList!!.mediumWords[randomId].getMediumNlWord()
                            "DeToEn" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                            "EnToDe" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "FrToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToFr" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }

                    "nl" -> {
                        when (chosenGame) {
                            "NlToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToNl" -> wordsList!!.mediumWords[randomId].getMediumNlWord()
                            "DeToEn" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                            "EnToDe" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "FrToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToFr" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }

                    "de" -> {
                        when (chosenGame) {
                            "NlToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToNl" -> wordsList!!.mediumWords[randomId].getMediumNlWord()
                            "DeToEn" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                            "EnToDe" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "FrToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToFr" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }

                    "en" -> {
                        when (chosenGame) {
                            "NlToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToNl" -> wordsList!!.mediumWords[randomId].getMediumNlWord()
                            "DeToEn" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                            "EnToDe" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "FrToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToFr" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }

                    else -> {
                        when (chosenGame) {
                            "NlToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToNl" -> wordsList!!.mediumWords[randomId].getMediumNlWord()
                            "DeToEn" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                            "EnToDe" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "FrToEn" -> wordsList!!.mediumWords[randomId].getMediumDeWord()
                            "EnToFr" -> wordsList!!.mediumWords[randomId].getMediumEnWord()
                        }
                    }
                }
                if (incorrectAnswer == correctAnswer) continue
                if (incorrectAnswers!!.contains(incorrectAnswer)) continue
                incorrectAnswers!!.add(incorrectAnswer)

                ///////////////////// hard mode //////////////////////////
            } else {
                if (incorrectAnswers!!.size > 3) {
                    break
                }
                randomId = random!!.nextInt(wordsList!!.hardWords.size)
                when (savedLanguage) {
                    "fr" -> {
                        when (chosenGame) {
                            "NlToEn", "FrToEn", "EnToNl" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardFrWord()

                            "EnToFr" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardEnWord()
                        }
                    }

                    "nl" -> {
                        when (chosenGame) {
                            "NlToEn", "FrToEn" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardNlWord()

                            "EnToNl" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardFrWord()

                            "EnToFr" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardEnWord()
                        }
                    }

                    else -> {
                        when (chosenGame) {
                            "NlToEn", "FrToEn" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardEnWord()

                            "EnToNl" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardNlWord()

                            "EnToFr" -> incorrectAnswer =
                                wordsList!!.hardWords[randomId].getHardFrWord()
                        }
                    }
                }
                if (incorrectAnswer == correctAnswer) continue
                if (incorrectAnswers!!.contains(incorrectAnswer)) continue
                incorrectAnswers!!.add(incorrectAnswer)
            }
        }

        //get all the incorrect answers and assign them to a button one by one
        for (i in 0..3) {
            //doesn't put an incorrect answer at the correct answer's button
            if (i == correctAnswerPosition) continue
            (findViewById<View>(btnIds[i]) as Button).text = incorrectAnswers!![i]
        }

        //now that all the buttons are assigned, we have to clear the arrayList for the next question
        incorrectAnswers!!.clear()
    }

    private fun gameOver() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        btn0!!.isClickable = false
        btn1!!.isClickable = false
        btn2!!.isClickable = false
        btn3!!.isClickable = false
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.putString("actualGame", chosenGame)
        editor.apply()
        val intent = Intent(this@LanguageGame, GameOver::class.java)
        intent.putExtra("points", points)
        intent.putExtra("difficulty", difficulty)
        intent.putExtra("chosenGame", "languageGame")
        releasePlayer()
        startActivity(intent)
        finish()
    }

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref!!.sound) {
            timerPlayer!!.release()
        }
    }

    fun chooseAnswer(view: View?) {

        //Log.i("GSon", "size: " + wordsList.getHardWords().size() + " | random: " + randomId + " | GSon: " + wordsList.getHardWords().get(randomId).getFrWord());
        clickedBtn = view as Button?

        //to make sure we don't verify the answer when clicking on the pause button
        val answer = clickedBtn!!.text.toString() //we get the answer of the player
        val strCorrect = resources.getString(R.string.correct)
        val strWrong = resources.getString(R.string.wrong)
        var isCorrect = false
        if (answer.equals(correctAnswer, ignoreCase = true)) {
            isCorrect = true
            playSound(isCorrect)
            points++

            //change the color of the clicked button to green
            clickedBtn!!.background =
                ResourcesCompat.getDrawable(resources, R.drawable.rounded_green, null)
            Handler(Looper.getMainLooper()).postDelayed({
                //set its initial color
                clickedBtn!!.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_btn, null)
            }, 500)
            tvResult!!.text = strCorrect
        } else {
            if (wrong < maxWrongAnswers) {
                playSound(isCorrect)

                //change the color of the clicked button to red
                clickedBtn!!.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_red, null)
                Handler(Looper.getMainLooper()).postDelayed({
                    //set its initial color
                    clickedBtn!!.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.rounded_btn, null)
                }, 500)
                tvResult!!.text = strWrong
                wrong++
            } else {
                gameOver()
            }
        }
        tvPoints!!.text = getString(R.string.points, points, numberOfQuestions)
        Handler(Looper.getMainLooper()).postDelayed({
            //make the text disappear after 1s
            tvResult!!.text = ""
        }, 1000)
        generateQuestion()
    }

    private fun playSound(isCorrect: Boolean) {
        if (sharedPref!!.sound) {
            if (isCorrect) {

                //stops the previous sound
                stopPlayer()
                player = MediaPlayer.create(this, R.raw.correct_fav)
                startPlayer(player)
            } else {
                //stops the previous sound
                stopPlayer()
                player = MediaPlayer.create(this, R.raw.wrong)
                startPlayer(player)
            }
        }
    }

    private fun startPlayer(myPlayer: MediaPlayer?) {
        myPlayer!!.start()
        myPlayer.setOnCompletionListener { stopPlayer() }
    }

    private fun stopPlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }
}