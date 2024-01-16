package com.halfminute.itmadness

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.Locale
import java.util.Random

class MathGame : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private var op1 = 0.0
    private var op2 = 0.0
    private var correctAnswer = 0.0
    private var incorrectAnswer = 0.0
    private lateinit var tvTimer: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvSum: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvLives: TextView
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var clickedBtn: Button
    private var countDownTimer: CountDownTimer? = null
    private var millisUntilFinished = 30100L
    private var points = 0
    private var wrong = 0
    private var maxWrongAnswers = 2
    private var numberOfQuestions = 0
    private val random = Random()
    private val btnIds = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3)
    private var correctAnswerPosition = 0
    private val incorrectAnswers = ArrayList<Double>()
    private val operatorArray = arrayOf("+", "-", "*", "÷")
    private val df = DecimalFormat("0.##")
    private var initialColor: ColorDrawable? = null
    private var colorId = 0
    private var difficulty: String = "easy"
    private var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)

        setContentView(R.layout.math_game)

        initViewComponents()
        difficulty = sharedPref.loadDifficulty()
        startGame()

        // Handle back pressed with OnBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                releasePlayer()
                val intent = Intent(this@MathGame, ChooseGame::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initViewComponents() {
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvSum = findViewById(R.id.tvSum)
        tvResult = findViewById(R.id.tvResult)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvLives = findViewById(R.id.tvLives)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
    }

    fun pauseGame(view: View) {
        sharedPref.saveChosenGame("math")
        countDownTimer?.cancel()
        releasePlayer()
        startActivity(Intent(this, PauseMenu::class.java))
        finish()
    }


    private fun startGame() {
        tvTimer.text = "${millisUntilFinished / 1000}s"
        tvPoints.text = "$points/$numberOfQuestions"
        generateQuestion()

        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "${millisUntilFinished / 1000}s"
                if (millisUntilFinished / 1000 <= 5) {
                    tvTimer.setTextColor(resources.getColor(R.color.wrong))
                    tvTimer.textSize = 26f  // Assuming the text size is 26sp
                    Handler().postDelayed({
                        tvTimer.textSize = 24f  // Assuming the original text size is 24sp
                        tvTimer.setTextColor(resources.getColor(R.color.buttonTextColor)) // Replace with your default text color
                    }, 1000)
                }
            }

            override fun onFinish() {
                gameOver()
            }
        }.start()
    }


    private fun playTimerSound() {
        if (sharedPref.getSound()) {
            timerPlayer?.start()
        }
    }

    private fun gameOver() {
        countDownTimer?.cancel()
        listOf(btn0, btn1, btn2, btn3).forEach { it.isClickable = false }
        sharedPref.saveChosenGame("math")
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("points", points)
            putExtra("difficulty", difficulty)
            putExtra("chosenGame", "math")
        }
        releasePlayer()
        startActivity(intent)
        finish()
    }

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.getSound()) {
            timerPlayer?.release()
            timerPlayer = null
        }
    }

    private fun generateQuestion() {
        numberOfQuestions++
        val selectedOperator = operatorArray[random.nextInt(4)]

        when (sharedPref.loadDifficulty().lowercase(Locale.getDefault())) {
            "easy" -> {
                tvDifficulty.setText(R.string.difficultyEasy)
                maxWrongAnswers = 5  // Easy mode has 5 lives
                op1 = (1 + random.nextInt(9)).toDouble()
                do {
                    op2 = (1 + random.nextInt(9)).toDouble()
                    correctAnswer = calculateAnswer(selectedOperator)
                } while (op2 >= op1 && correctAnswer % 1 != 0.0)
            }

            "medium" -> {
                tvDifficulty.setText(R.string.difficultyMedium)
                maxWrongAnswers = 3  // Medium mode has 3 lives
                op1 = random.nextInt(10).toDouble()
                do {
                    op2 = 1 + random.nextInt(9).toDouble()
                    correctAnswer = calculateAnswer(selectedOperator)
                } while (correctAnswer % 1 != 0.0)
            }

            else -> {  // This includes the "hard" difficulty
                tvDifficulty.setText(R.string.difficultyHard)
                maxWrongAnswers = 2  // Hard mode has 2 lives
                op1 = random.nextInt(21).toDouble()
                op2 = 1 + random.nextInt(20).toDouble()
                correctAnswer = calculateAnswer(selectedOperator)
            }
        }

        tvLives.text = "${maxWrongAnswers - wrong}"
        tvSum.text = "${df.format(op1)} $selectedOperator ${df.format(op2)} = "
        correctAnswerPosition = random.nextInt(4)

        findViewById<Button>(btnIds[correctAnswerPosition]).text = df.format(correctAnswer)
        setIncorrectAnswers()
    }

    private fun calculateAnswer(selectedOperator: String): Double {
        return when (selectedOperator) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "*" -> op1 * op2
            "÷" -> op1 / op2
            else -> 0.0
        }
    }


    private fun setIncorrectAnswers() {
        incorrectAnswers.clear()
        while (incorrectAnswers.size < 3) {
            incorrectAnswer = generateIncorrectAnswer()
            if (incorrectAnswer != correctAnswer && !incorrectAnswers.contains(incorrectAnswer)) {
                incorrectAnswers.add(incorrectAnswer)
            }
        }

        btnIds.filter { it != btnIds[correctAnswerPosition] }.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).text = df.format(incorrectAnswers[index])
        }
    }

    private fun generateIncorrectAnswer(): Double {
        // Logic to generate an incorrect answer
        var result: Double
        do {
            val randomOperator = operatorArray[random.nextInt(4)]
            val op1 = random.nextInt(21).toDouble()
            val op2 = 1 + random.nextInt(20).toDouble()
            result = when (randomOperator) {
                "+" -> op1 + op2
                "-" -> op1 - op2
                "*" -> op1 * op2
                "÷" -> op1 / op2
                else -> 0.0
            }
        } while (result == correctAnswer || incorrectAnswers.contains(result))
        return result
    }


    fun chooseAnswer(view: View) {
        if (view !is ImageButton) {
            clickedBtn = view as Button
            initialColor = clickedBtn.background as ColorDrawable
            colorId = initialColor!!.color

            val answer = clickedBtn.text.toString()
            val isCorrect = answer == df.format(correctAnswer)
            playSound(isCorrect)

            if (isCorrect) {
                updateForCorrectAnswer()
            } else {
                updateForIncorrectAnswer()
            }
        }
    }

    private fun updateForCorrectAnswer() {
        points++
        clickedBtn.setBackgroundColor(resources.getColor(R.color.correct))
        Handler(Looper.getMainLooper()).postDelayed({
            clickedBtn.setBackgroundColor(colorId)
            tvResult.text = getString(R.string.correct)
            generateQuestion()
        }, 300)
        tvPoints.text = "$points/$numberOfQuestions"
    }

    private fun updateForIncorrectAnswer() {
        wrong++
        clickedBtn.setBackgroundColor(resources.getColor(R.color.wrong))
        Handler(Looper.getMainLooper()).postDelayed({
            clickedBtn.setBackgroundColor(colorId)
            if (wrong >= maxWrongAnswers) {
                gameOver()
            } else {
                tvResult.text = getString(R.string.wrong)
                generateQuestion()
            }
        }, 300)
        tvPoints.text = "$points/$numberOfQuestions"
        tvLives.text = "${maxWrongAnswers - wrong}" // Update lives here
    }


    private fun playSound(isCorrect: Boolean) {
        if (sharedPref.getSound()) {
            stopPlayer() // stop any previous sound
            player = if (isCorrect) {
                MediaPlayer.create(this, R.raw.correct_sound)
            } else {
                MediaPlayer.create(this, R.raw.incorrect_sound)
            }
            startPlayer(player!!)
        }
    }


    private fun startPlayer(myPlayer: MediaPlayer) {
        myPlayer.start()
        myPlayer.setOnCompletionListener { stopPlayer() }
    }

    private fun stopPlayer() {
        player?.release()
        player = null
    }
}
