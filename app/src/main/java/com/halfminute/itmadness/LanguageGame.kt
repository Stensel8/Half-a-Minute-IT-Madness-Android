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
    private lateinit var btnIds: IntArray
    private var correctAnswerPosition = 0
    private lateinit var correctAnswer: String
    private lateinit var savedLanguage: String
    private lateinit var chosenGame: String
    private lateinit var difficulty: String
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null
    private var incorrectAnswers = mutableListOf<String>()

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
        words = Words.getInstance(this)
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
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("actualGame", chosenGame)
        editor.apply()
        countDownTimer?.cancel()
        val intent = Intent(this@LanguageGame, PauseMenu::class.java)
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

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.sound) {
            timerPlayer!!.release()
        }
    }


    private fun playTimerSound() {
        if (sharedPref.sound) {
            startPlayer(timerPlayer)
        }
    }

    private fun generateQuestion() {
        numberOfQuestions++
        val wordList =
            words.getWordsByDifficulty(Difficulty.valueOf(difficulty.uppercase(Locale.ROOT)))
        val word = wordList[random.nextInt(wordList.size)]
        setQuestionAndAnswer(word)
        setIncorrectAnswers()
    }

    private fun setQuestionAndAnswer(word: Word) {
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
        correctAnswerPosition = random.nextInt(4)
        (findViewById<View>(btnIds[correctAnswerPosition]) as Button).text = correctAnswer
    }

    private fun setIncorrectAnswers() {
        incorrectAnswers.clear()
        val wordList =
            words.getWordsByDifficulty(Difficulty.valueOf(difficulty.uppercase(Locale.ROOT)))

        while (incorrectAnswers.size < 3) {
            val incorrectWord = wordList[random.nextInt(wordList.size)]
            val incorrectAnswer = getIncorrectAnswer(incorrectWord)
            if (incorrectAnswer != correctAnswer && !incorrectAnswers.contains(incorrectAnswer)) {
                incorrectAnswers.add(incorrectAnswer)
            }
        }

        var btnIndex = 0
        for (i in btnIds.indices) {
            if (i != correctAnswerPosition) {
                (findViewById<View>(btnIds[i]) as Button).text = incorrectAnswers[btnIndex++]
            }
        }
    }

    private fun getIncorrectAnswer(word: Word): String {
        return when (chosenGame) {
            "NlToEn" -> word.nlWord
            "EnToNl" -> word.enWord
            "DeToEn" -> word.deWord
            "EnToDe" -> word.enWord
            "FrToEn" -> word.frWord
            "EnToFr" -> word.enWord
            else -> ""
        }
    }

    private fun gameOver() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        disableButtons()
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

    private fun disableButtons() {
        btn0.isClickable = false
        btn1.isClickable = false
        btn2.isClickable = false
        btn3.isClickable = false
    }

    fun chooseAnswer(view: View?) {
        clickedBtn = view as? Button
        clickedBtn ?: return // Return early if the cast fails
        val answer = clickedBtn!!.text.toString()
        val strCorrect = resources.getString(R.string.correct)
        val strWrong = resources.getString(R.string.wrong)
        var isCorrect = false

        if (answer.equals(correctAnswer, ignoreCase = true)) {
            isCorrect = true
            playSound(isCorrect)
            points++
            changeButtonColor(clickedBtn!!, R.drawable.rounded_green)
            tvResult.text = strCorrect
        } else {
            if (wrong < maxWrongAnswers) {
                playSound(isCorrect)
                changeButtonColor(clickedBtn!!, R.drawable.rounded_red)
                tvResult.text = strWrong
                wrong++
            } else {
                gameOver()
            }
        }
        
        updateUI()
        generateQuestion()
    }

    private fun updateUI() {
        tvPoints.text = getString(R.string.points, points, numberOfQuestions)
        Handler(Looper.getMainLooper()).postDelayed({
            tvResult.text = ""
        }, 1000)
    }

    private fun changeButtonColor(button: Button, drawableResId: Int) {
        button.background = ResourcesCompat.getDrawable(resources, drawableResId, null)
        Handler(Looper.getMainLooper()).postDelayed({
            button.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_btn, null)
        }, 500)
    }

    private fun playSound(isCorrect: Boolean) {
        if (sharedPref.sound) {
            val soundResId = if (isCorrect) R.raw.correct_sound else R.raw.incorrect_sound
            stopPlayer()
            player = MediaPlayer.create(this, soundResId)
            startPlayer(player)
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
