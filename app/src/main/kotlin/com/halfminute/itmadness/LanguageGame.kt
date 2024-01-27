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
import java.util.Random


class LanguageGame : AppCompatActivity() {
    private lateinit var sharedPref: SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private var random: Random? = null
    private var correctAnswer: String? = null
    private var savedLanguage: String? = null
    private var chosenGame: String? = null
    private var difficulty: String? = null
    private var tvQuestion: TextView? = null
    private var tvTimer: TextView? = null
    private var tvPoints: TextView? = null
    private var tvResult: TextView? = null
    private var tvLives: TextView? = null
    private var tvDifficulty: TextView? = null
    private var btn0: Button? = null
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var btn3: Button? = null
    private var clickedBtn: Button? = null
    private var countDownTimer: CountDownTimer? = null
    private var millisUntilFinished: Long = 0
    private var points = 0
    private var wrong = 0
    private var maxWrongAnswers = 0
    private var numberOfQuestions = 0
    private var randomId = 0
    private var previousRandom = 0
    private lateinit var btnIds: IntArray
    private var correctAnswerPosition = 0
    private var incorrectAnswers: ArrayList<String>? = null
    private var languageWordsList: LanguageWords? = null
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null
    private var isBackButtonPressed = false

    /**
     * Initializes the activity and sets up the game environment.
     * @param savedInstanceState Bundle object to recover the previous state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSharedPref()
        setAppTheme()
        loadLocaleAndSettings()
        setContentView(R.layout.language_test)
        initializeGameComponents()
        setupOnBackPressedCallback()
        startGame()
    }

    /**
     * Initializes shared preferences to store and retrieve game settings and states.
     */
    private fun initializeSharedPref() {
        sharedPref = SharedPref(this)
    }

    /**
     * Sets the application theme based on user preferences.
     */
    private fun setAppTheme() {
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
    }

    /**
     * Loads locale settings and other game settings.
     */
    private fun loadLocaleAndSettings() {
        // Locale and settings are now loaded through the SharedPref class
        sharedPref.loadLocale(this)
        savedLanguage = sharedPref.getSavedLanguage(this)
        difficulty = sharedPref.loadDifficulty()
        chosenGame = intent.getStringExtra("chosenGame") ?: ""
    }

    /**
     * Initializes game components like buttons, text views, timers, etc.
     */
    private fun initializeGameComponents() {
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
        millisUntilFinished = 30100
        points = 0
        wrong = 0
        maxWrongAnswers = 2
        numberOfQuestions = 0
        random = Random()
        randomId = 0
        previousRandom = Int.MAX_VALUE
        btnIds = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3)
        correctAnswerPosition = 0
        incorrectAnswers = ArrayList()
        languageWordsList = LanguageWords.getInstance(this)
    }

    /**
     * Sets up the callback to handle the back button press during the game.
     */
    private fun setupOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                if (!isBackButtonPressed) {
                    // First time back button (or swipe)
                    isBackButtonPressed = true
                    sharedPref.showCustomSnackbar(
                        this@LanguageGame,
                        "Back button pressed. Press again to navigate back."
                    )

                    // Reset the isBackButtonPressed flag after 3 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        isBackButtonPressed = false
                    }, 3000)
                } else {
                    // Second time back button (or swipe)
                    navigateToPreviousActivity()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }


    /**
     * Navigates back to the previous activity.
     */
    private fun navigateToPreviousActivity() {
        val intent = Intent(this, ChooseLanguageGame::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Pauses the game, saves the current state, and navigates to the pause menu.
     * @param ignoredView The current view context.
     */
    @Suppress("UNUSED_PARAMETER")
    fun pauseGame(ignoredView: View) {
        saveCurrentGame()
        countDownTimer?.cancel()
        releasePlayer()
        navigateToPauseMenu()
    }

    /**
     * Saves the current game state to shared preferences.
     */
    private fun saveCurrentGame() {
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        sharedPreferences.edit()?.apply {
            putString("actualGame", chosenGame)
            apply()
        }
    }

    /**
     * Navigates to the pause menu.
     */
    private fun navigateToPauseMenu() {
        Intent(this, PauseMenu::class.java).also {
            startActivity(it)
            finish()
        }
    }


    /**
     * Starts the game, sets up questions, and starts the timer.
     */
    private fun startGame() {
        tvTimer!!.text = (millisUntilFinished / 1000).toString()
        tvPoints!!.text = getString(R.string.score_format, points, numberOfQuestions)
        generateQuestion()
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val initialColor = tvTimer!!.currentTextColor
                tvTimer!!.text = "$seconds" + "s"
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

    /**
     * Plays a sound to indicate the timer is running out.
     */
    private fun playTimerSound() {
        if (sharedPref.getSound()) {
            startPlayer(timerPlayer)
        }
    }

    /**
     * Generates a new question for the game.
     */
    private fun generateQuestion() {
        numberOfQuestions++

        Log.d("Language", "Saved Language: $savedLanguage")
/////////////////////// easy mode /////////////////////////
        if (difficulty.equals("easy", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyEasy)
            randomId = random!!.nextInt(languageWordsList!!.easyWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(languageWordsList!!.easyWords.size)
            }
            maxWrongAnswers = 5
            when (savedLanguage) {
                "fr" -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    val question: String
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.easyWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.easyWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.easyWords[randomId].frWord
                        }
                    }
                }
            }

///////////////////// medium mode /////////////////////////
        } else if (difficulty.equals("medium", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyMedium)
            randomId = random!!.nextInt(languageWordsList!!.mediumWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(languageWordsList!!.mediumWords.size)
            }
            var question: String
            if (savedLanguage == "de") {
                when (chosenGame) {
                    "NlToEn" -> {
                        question = languageWordsList!!.mediumWords[randomId].nlWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToNl" -> {
                        question = languageWordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].nlWord
                    }

                    "DeToEn" -> {
                        question = languageWordsList!!.mediumWords[randomId].deWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToDe" -> {
                        question = languageWordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].deWord
                    }

                    "FrToEn" -> {
                        question = languageWordsList!!.mediumWords[randomId].frWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToFr" -> {
                        question = languageWordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.mediumWords[randomId].frWord
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.mediumWords[randomId].frWord
                        }
                    }
                }
            }

/////////////////////// hard mode /////////////////////////
        } else {
            tvDifficulty!!.text = resources.getString(R.string.difficultyHard)
            randomId = random!!.nextInt(languageWordsList!!.hardWords.size)
            while (randomId == previousRandom) {
                randomId = random!!.nextInt(languageWordsList!!.hardWords.size)
            }
            var question: String
            if (savedLanguage == "de") {
                when (chosenGame) {
                    "NlToEn" -> {
                        question = languageWordsList!!.hardWords[randomId].nlWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                    }

                    "EnToNl" -> {
                        question = languageWordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].nlWord
                    }

                    "DeToEn" -> {
                        question = languageWordsList!!.hardWords[randomId].deWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                    }

                    "EnToDe" -> {
                        question = languageWordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].deWord
                    }

                    "FrToEn" -> {
                        question = languageWordsList!!.hardWords[randomId].frWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                    }

                    "EnToFr" -> {
                        question = languageWordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = languageWordsList!!.hardWords[randomId].frWord
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = languageWordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = languageWordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = languageWordsList!!.hardWords[randomId].frWord
                        }
                    }
                }
            }
        }

        // We store the random to check it on the next question
        previousRandom = randomId

        // Update the live of the player on every question
        tvLives!!.text = getString(R.string.lives, maxWrongAnswers + 1 - wrong)

        // Little bonus when you're at the 30th question
        if (numberOfQuestions == 31) {
            correctAnswer = "30 seconds game"
            tvQuestion!!.text = getString(R.string.game_time)
        }

        //get a random position between the 4 buttons
        correctAnswerPosition = random!!.nextInt(4)
        (findViewById<View>(btnIds[correctAnswerPosition]) as Button).text = correctAnswer
        setIncorrectAnswers()
    }

    /**
     * Sets incorrect answers for the game's multiple-choice questions.
     */
    private fun setIncorrectAnswers() {
        incorrectAnswers!!.clear()

        while (incorrectAnswers!!.size < 3) {
            val randomWord = getRandomWord()

            val incorrectAnswer = when (savedLanguage) {
                "fr" -> getWordBasedOnGame(randomWord, "fr")
                "nl" -> getWordBasedOnGame(randomWord, "nl")
                "de" -> getWordBasedOnGame(randomWord, "de")
                "en" -> getWordBasedOnGame(randomWord, "en")
                else -> getWordBasedOnGame(randomWord, "default")
            }

            // Zorg ervoor dat het incorrecte antwoord niet gelijk is aan het correcte antwoord
            // en dat het niet al in de lijst van incorrecte antwoorden staat
            if (incorrectAnswer != correctAnswer && !incorrectAnswers!!.contains(incorrectAnswer)) {
                incorrectAnswers!!.add(incorrectAnswer)
            }
        }

        assignIncorrectAnswersToButtons()
    }

    /**
     * Gets a random word from the word list based on the current difficulty.
     * @return A random Word object.
     */
    private fun getRandomWord(): LanguageWord {
        return when (difficulty) {
            "easy" -> languageWordsList!!.easyWords.random()
            "medium" -> languageWordsList!!.mediumWords.random()
            "hard" -> languageWordsList!!.hardWords.random()
            else -> languageWordsList!!.easyWords.random()
        }
    }

    /**
     * Retrieves a word based on the game type and language.
     * @param word The word object.
     * @param language The language in which the word is needed.
     * @return A string representing the word in the specified language.
     */
    private fun getWordBasedOnGame(word: LanguageWord, language: String): String {
        return when (chosenGame) {
            "NlToEn" -> if (language == "nl") word.nlWord else word.enWord
            "EnToNl" -> if (language == "en") word.enWord else word.nlWord
            "DeToEn" -> if (language == "de") word.deWord else word.enWord
            "EnToDe" -> if (language == "en") word.enWord else word.deWord
            "FrToEn" -> if (language == "fr") word.frWord else word.enWord
            "EnToFr" -> if (language == "en") word.enWord else word.frWord
            else -> ""
        }
    }

    /**
     * Assigns incorrect answers to the available buttons in the game.
     */
    private fun assignIncorrectAnswersToButtons() {
        incorrectAnswers!!.clear()

        while (incorrectAnswers!!.size < 3) {
            val randomWord = getRandomWord()

            val incorrectAnswer = when (savedLanguage) {
                "fr" -> getWordBasedOnGame(randomWord, "fr")
                "nl" -> getWordBasedOnGame(randomWord, "nl")
                "de" -> getWordBasedOnGame(randomWord, "de")
                "en" -> getWordBasedOnGame(randomWord, "en")
                else -> getWordBasedOnGame(randomWord, "default")
            }

            if (incorrectAnswer != correctAnswer && !incorrectAnswers!!.contains(incorrectAnswer)) {
                incorrectAnswers!!.add(incorrectAnswer)
            }
        }

        // Assign the correct answer to a random button
        correctAnswerPosition = random!!.nextInt(4)
        findViewById<Button>(btnIds[correctAnswerPosition]).text = correctAnswer

        // Assign incorrect answers to the rest of the buttons
        var index = 0
        for (i in btnIds.indices) {
            if (i != correctAnswerPosition) {
                findViewById<Button>(btnIds[i]).text = incorrectAnswers!![index++]
            }
        }
    }

    /**
     * Processes the player's answer selection and updates the game state.
     * @param view The view context of the clicked button.
     */
    fun chooseAnswer(view: View?) {
        clickedBtn = view as Button?
        val answer = clickedBtn!!.text.toString()

        val strCorrect = resources.getString(R.string.correct)
        val strWrong = resources.getString(R.string.wrong)
        val isCorrect = answer.equals(correctAnswer, ignoreCase = true)

        if (isCorrect) {
            playSound(true)
            points++
            updateButtonBackground(clickedBtn!!, R.drawable.rounded_green)
            tvResult!!.text = strCorrect
        } else {
            playSound(false)
            updateButtonBackground(clickedBtn!!, R.drawable.rounded_red)
            tvResult!!.text = strWrong
            if (++wrong > maxWrongAnswers) {
                gameOver()
            }
        }

        updateUIAfterAnswer()
    }

    /**
     * Updates the appearance of the button based on whether the answer is correct or wrong.
     * @param button The button to be updated.
     * @param drawableId The resource ID of the drawable to be used for the button background.
     */
    private fun updateButtonBackground(button: Button, drawableId: Int) {
        button.background = ResourcesCompat.getDrawable(resources, drawableId, null)
        Handler(Looper.getMainLooper()).postDelayed({
            button.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_btn, null)
        }, 500)
    }

    /**
     * Updates the game's UI after an answer is selected.
     */
    private fun updateUIAfterAnswer() {
        tvPoints!!.text = getString(R.string.points, points, numberOfQuestions)
        Handler(Looper.getMainLooper()).postDelayed({
            tvResult!!.text = ""
            generateQuestion()
        }, 1000)
    }

    /**
     * Plays a sound based on whether the player's answer is correct or not.
     * @param isCorrect A boolean indicating whether the answer was correct.
     */
    private fun playSound(isCorrect: Boolean) {
        if (sharedPref.getSound()) {
            if (isCorrect) {

                //stops the previous sound
                stopPlayer()
                player = MediaPlayer.create(this, R.raw.correct_sound)
                startPlayer(player)
            } else {
                //stops the previous sound
                stopPlayer()
                player = MediaPlayer.create(this, R.raw.incorrect_sound)
                startPlayer(player)
            }
        }
    }

    /**
     * Starts a media player to play a sound.
     * @param myPlayer The MediaPlayer object to play the sound.
     */
    private fun startPlayer(myPlayer: MediaPlayer?) {
        myPlayer!!.start()
        myPlayer.setOnCompletionListener { stopPlayer() }
    }

    /**
     * Stops and releases the media player.
     */
    private fun stopPlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    /**
     * Releases the timer sound player.
     */
    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.getSound()) {
            timerPlayer!!.release()
        }
    }

    /**
     * Handles the game over scenario, stops the timer, and navigates to the game over screen.
     */
    private fun gameOver() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        btn0!!.isClickable = false
        btn1!!.isClickable = false
        btn2!!.isClickable = false
        btn3!!.isClickable = false
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
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
}