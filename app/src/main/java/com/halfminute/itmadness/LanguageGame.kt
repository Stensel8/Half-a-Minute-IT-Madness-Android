package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import java.util.Locale
import java.util.Random


class LanguageGame : AppCompatActivity() {
    private lateinit var sharedPref: SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pref: SharedPreferences
    private lateinit var random: Random
    private lateinit var tvQuestion: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvLives: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private var clickedBtn: Button? = null
    private var countDownTimer: CountDownTimer? = null
    private var millisUntilFinished: Long = 30100
    private var points = 0
    private var wrong = 0
    private var maxWrongAnswers = 2
    private var numberOfQuestions = 0
    private var randomId = 0
    private var previousRandom = Int.MAX_VALUE
    private lateinit var btnIds: IntArray
    private var correctAnswerPosition = 0
    private var incorrectAnswers = mutableListOf<String>()
    private lateinit var correctAnswer: String
    private lateinit var savedLanguage: String
    private lateinit var chosenGame: String
    private lateinit var difficulty: String
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null

    private lateinit var words: Words


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)
        pref = getSharedPreferences("Settings", MODE_PRIVATE)
        savedLanguage = pref.getString("My lang", Locale.getDefault().language) ?: ""
        setContentView(R.layout.language_test)

        initializeViews()
        chosenGame = intent.getStringExtra("chosenGame") ?: ""
        sharedPreferences = getSharedPreferences("gameDifficulty", MODE_PRIVATE)
        difficulty = sharedPreferences.getString("difficulty", "easy") ?: "easy"

        setupOnBackPressedCallback()
        words = Words.instance
        random = Random()
        startGame()
    }

    private fun initializeViews() {
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
        btnIds = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3)
    }

    private fun setupOnBackPressedCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                startActivity(Intent(this@LanguageGame, ChooseLanguageGame::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        startGame()
    }


    fun pauseGame(view: View) {
        // Saves the current game we're playing
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("actualGame", chosenGame)
        editor.apply()
        countDownTimer!!.cancel()
        val intent = Intent(this@LanguageGame, PauseMenu::class.java)
        releasePlayer()
        startActivity(intent)
        finish()
    }

    private fun startGame() {
        tvTimer.text = getString(R.string.timer_seconds, millisUntilFinished / 1000)
        tvPoints.text = getString(R.string.score_format, points, numberOfQuestions)
        generateQuestion()
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val initialColor = tvTimer.currentTextColor
                tvTimer.text = getString(R.string.timer_seconds, seconds)
                if (seconds <= 5) {
                    if (seconds == 5L) {
                        timerPlayer =
                            MediaPlayer.create(this@LanguageGame, R.raw.five_sec_countdown)
                        playTimerSound()
                    }
                    tvTimer.setTextColor(resources.getColor(R.color.wrong, theme))
                    tvTimer.textSize = 26f
                    Handler(Looper.getMainLooper()).postDelayed({

                        // set it initial color and size
                        tvTimer.textSize = 24f
                        tvTimer.setTextColor(initialColor)
                    }, 300)
                }
            }

            override fun onFinish() {
                gameOver()
            }
        }.start()
    }

    private fun playTimerSound() {
        if (sharedPref.sound) {
            startPlayer(timerPlayer)
        }
    }

    private fun generateQuestion() {
        numberOfQuestions++
        words = Words.instance // Ensure that the Words instance is initialized
        val wordList =
            words.getWordsByDifficulty(Difficulty.valueOf(difficulty.uppercase(Locale.ROOT)))
        val word = wordList[random.nextInt(wordList.size)]

        // Determine the question and correct answer based on the chosen game
        when (chosenGame) {
            "NlToEn" -> {
                tvQuestion.text = word.nlWord
                correctAnswer = word.enWord
            }

            "EnToNl" -> {
                tvQuestion.text = word.enWord
                correctAnswer = word.nlWord
            }

            "DeToEn" -> {
                tvQuestion.text = word.deWord
                correctAnswer = word.enWord
            }

            "EnToDe" -> {
                tvQuestion.text = word.enWord
                correctAnswer = word.deWord
            }

            "FrToEn" -> {
                tvQuestion.text = word.frWord
                correctAnswer = word.enWord
            }

            "EnToFr" -> {
                tvQuestion.text = word.enWord
                correctAnswer = word.frWord
            }
        }

        // Set the correct answer in a random button position
        correctAnswerPosition = random.nextInt(4)
        (findViewById<View>(btnIds[correctAnswerPosition]) as Button).text = correctAnswer

        // Prepare incorrect answers for other buttons
        setIncorrectAnswers()
    }

    //assign the incorrect answers to the other buttons
    private fun setIncorrectAnswers() {
        incorrectAnswers.clear()
        val wordList =
            words.getWordsByDifficulty(Difficulty.valueOf(difficulty.uppercase(Locale.ROOT)))

        while (incorrectAnswers.size < 3) {
            val incorrectWord = wordList[random.nextInt(wordList.size)]
            val incorrectAnswer = when (chosenGame) {
                "NlToEn" -> incorrectWord.nlWord
                "EnToNl" -> incorrectWord.enWord
                "DeToEn" -> incorrectWord.deWord
                "EnToDe" -> incorrectWord.enWord
                "FrToEn" -> incorrectWord.frWord
                "EnToFr" -> incorrectWord.enWord
                // Add other cases as necessary
                else -> ""
            }

            if (incorrectAnswer != correctAnswer && !incorrectAnswers.contains(incorrectAnswer)) {
                incorrectAnswers.add(incorrectAnswer)
            }
        }

        // Assign incorrect answers to the buttons, excluding the one with the correct answer
        var btnIndex = 0
        for (i in btnIds.indices) {
            if (i != correctAnswerPosition) {
                (findViewById<View>(btnIds[i]) as Button).text = incorrectAnswers[btnIndex++]
            }
        }
    }

    private fun gameOver() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        btn0.isClickable = false
        btn1.isClickable = false
        btn2.isClickable = false
        btn3.isClickable = false
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

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.sound) {
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
            tvResult.text = strCorrect
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
                tvResult.text = strWrong
                wrong++
            } else {
                gameOver()
            }
        }
        tvPoints.text = getString(R.string.points, points, numberOfQuestions)
        Handler(Looper.getMainLooper()).postDelayed({
            //make the text disappear after 1s
            tvResult.text = ""
        }, 1000)
        generateQuestion()
    }

    private fun playSound(isCorrect: Boolean) {
        if (sharedPref.sound) {
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
}
