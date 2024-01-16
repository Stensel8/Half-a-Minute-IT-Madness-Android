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
    private var wordsList: Words? = null
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null

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

    private fun initializeSharedPref() {
        sharedPref = SharedPref(this)
    }

    private fun setAppTheme() {
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
    }

    private fun loadLocaleAndSettings() {
        // Locale and settings are now loaded through the SharedPref class
        sharedPref.loadLocale(this)
        savedLanguage = sharedPref.getSavedLanguage(this)
        difficulty = sharedPref.loadDifficulty()
        chosenGame = intent.getStringExtra("chosenGame") ?: ""
    }

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
        wordsList = Words.getInstance(this)
    }

    private fun setupOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                startActivity(Intent(this@LanguageGame, ChooseLanguageGame::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    fun pauseGame(view: View) {
        saveCurrentGame()
        countDownTimer?.cancel()
        releasePlayer()
        navigateToPauseMenu()
    }

    private fun saveCurrentGame() {
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        sharedPreferences.edit()?.apply {
            putString("actualGame", chosenGame)
            apply()
        }
    }

    private fun navigateToPauseMenu() {
        Intent(this, PauseMenu::class.java).also {
            startActivity(it)
            finish()
        }
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
        if (sharedPref.getSound()) {
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
                        question = wordsList!!.mediumWords[randomId].nlWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToNl" -> {
                        question = wordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].nlWord
                    }

                    "DeToEn" -> {
                        question = wordsList!!.mediumWords[randomId].deWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToDe" -> {
                        question = wordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].deWord
                    }

                    "FrToEn" -> {
                        question = wordsList!!.mediumWords[randomId].frWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].enWord
                    }

                    "EnToFr" -> {
                        question = wordsList!!.mediumWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.mediumWords[randomId].frWord
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.mediumWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.mediumWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.mediumWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.mediumWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.mediumWords[randomId].frWord
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
                        question = wordsList!!.hardWords[randomId].nlWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].enWord
                    }

                    "EnToNl" -> {
                        question = wordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].nlWord
                    }

                    "DeToEn" -> {
                        question = wordsList!!.hardWords[randomId].deWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].enWord
                    }

                    "EnToDe" -> {
                        question = wordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].deWord
                    }

                    "FrToEn" -> {
                        question = wordsList!!.hardWords[randomId].frWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].enWord
                    }

                    "EnToFr" -> {
                        question = wordsList!!.hardWords[randomId].enWord
                        tvQuestion!!.text = question
                        correctAnswer = wordsList!!.hardWords[randomId].frWord
                    }
                }
            }
            when (savedLanguage) {
                "fr" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].frWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].frWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }
                    }
                }

                "nl" -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].nlWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].frWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].nlWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }
                    }
                }

                else -> {
                    when (chosenGame) {
                        "NlToEn" -> {
                            question = wordsList!!.hardWords[randomId].nlWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }

                        "EnToNl" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].nlWord
                        }

                        "DeToEn" -> {
                            question = wordsList!!.hardWords[randomId].deWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }

                        "EnToDe" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].deWord
                        }

                        "FrToEn" -> {
                            question = wordsList!!.hardWords[randomId].frWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].enWord
                        }

                        "EnToFr" -> {
                            question = wordsList!!.hardWords[randomId].enWord
                            tvQuestion!!.text = question
                            correctAnswer = wordsList!!.hardWords[randomId].frWord
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

    private fun getRandomWord(): Word {
        return when (difficulty) {
            "easy" -> wordsList!!.easyWords.random()
            "medium" -> wordsList!!.mediumWords.random()
            "hard" -> wordsList!!.hardWords.random()
            else -> wordsList!!.easyWords.random()
        }
    }

    private fun getWordBasedOnGame(word: Word, language: String): String {
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

    private fun updateButtonBackground(button: Button, drawableId: Int) {
        button.background = ResourcesCompat.getDrawable(resources, drawableId, null)
        Handler(Looper.getMainLooper()).postDelayed({
            button.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_btn, null)
        }, 500)
    }

    private fun updateUIAfterAnswer() {
        tvPoints!!.text = getString(R.string.points, points, numberOfQuestions)
        Handler(Looper.getMainLooper()).postDelayed({
            tvResult!!.text = ""
            generateQuestion()
        }, 1000)
    }

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

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.getSound()) {
            timerPlayer!!.release()
        }
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