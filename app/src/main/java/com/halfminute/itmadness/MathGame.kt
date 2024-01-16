/**
 * Main MathGame class that handles the math game logic.
 */
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
import androidx.core.content.ContextCompat
import java.text.DecimalFormat
import java.util.Locale
import java.util.Random

class MathGame : AppCompatActivity() {

    // Shared preferences for the game settings
    private lateinit var sharedPref: SharedPref

    // Operand variables for the math operations
    private var op1 = 0.0
    private var op2 = 0.0

    // Correct and incorrect answers
    private var correctAnswer = 0.0
    private var incorrectAnswer = 0.0

    // UI components
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

    // Game variables
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

    /**
     * Initializes the activity and sets up the game.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)

        setContentView(R.layout.math_game)

        // Initialize view components
        initViewComponents()

        // Load game difficulty and start the game
        difficulty = sharedPref.loadDifficulty()
        startGame()

        // Handle back button press with OnBackPressedCallback
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

    // Function to initialize view components
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

    /**
     * Function to pause the game when the pause button is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun pauseGame(view: View) {
        sharedPref.saveChosenGame("math")
        countDownTimer?.cancel()
        releasePlayer()
        startActivity(Intent(this, PauseMenu::class.java))
        finish()
    }


    /**
     * Function to start the game and initialize game variables and countdown timer.
     */
    private fun startGame() {
        // Set initial UI text values
        tvTimer.text = "${millisUntilFinished / 1000}s"
        tvPoints.text = "$points/$numberOfQuestions"

        // Generate the first question
        generateQuestion()

        // Create a CountDownTimer for the game timer
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTimer.text = "${secondsLeft}s"

                // Play timer sound and update text color when last 5 seconds
                if (secondsLeft <= 5) {
                    playTimerSound()
                    tvTimer.setTextColor(ContextCompat.getColor(this@MathGame, R.color.wrong))
                    tvTimer.textSize = 26f

                    // Restore text color and size after 1 second
                    Handler(Looper.getMainLooper()).postDelayed({
                        tvTimer.textSize = 24f
                        tvTimer.setTextColor(ContextCompat.getColor(this@MathGame, R.color.buttonTextColor))
                    }, 1000)
                }
            }

            override fun onFinish() {
                // Handle game over when the timer finishes
                gameOver()
            }
        }.start()
    }

    /**
     * Function to play the timer sound when the time is running out.
     */
    private fun playTimerSound() {
        if (sharedPref.getSound()) {
            // Initialize timerPlayer if it's null
            if (timerPlayer == null) {
                timerPlayer = MediaPlayer.create(this, R.raw.five_sec_countdown) // Make sure you have this sound file
            }
            timerPlayer?.start()
        }
    }

    /**
     * Function to handle game over and navigate to the GameOver activity.
     */
    private fun gameOver() {
        countDownTimer?.cancel()
        // Disable button clicks and save the chosen game mode
        listOf(btn0, btn1, btn2, btn3).forEach { it.isClickable = false }
        sharedPref.saveChosenGame("math")
        // Start the GameOver activity with relevant data
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("points", points)
            putExtra("difficulty", difficulty)
            putExtra("chosenGame", "math")
        }
        releasePlayer()
        startActivity(intent)
        finish()
    }

    /**
     * Function to release the timerPlayer MediaPlayer.
     */
    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref.getSound()) {
            timerPlayer?.release()
            timerPlayer = null
        }
    }

    /**
     * Function to generate a new math question based on the selected difficulty level.
     */
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

    /**
     * Function to calculate the correct answer for a math question.
     */
    private fun calculateAnswer(selectedOperator: String): Double {
        return when (selectedOperator) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "*" -> op1 * op2
            "÷" -> op1 / op2
            else -> 0.0
        }
    }

    /**
     * Function to set incorrect answers for the multiple-choice buttons.
     */
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

    /**
     * Function to generate an incorrect answer for the math question.
     * This function ensures that the incorrect answer is different from the correct answer and
     * not already present in the list of incorrect answers.
     */
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

    /**
     * Function to handle user's answer selection when a choice button is clicked.
     */
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

    /**
     * Function to update the UI and logic when the user selects the correct answer.
     */
    private fun updateForCorrectAnswer() {
        points++
        clickedBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.correct))
        Handler(Looper.getMainLooper()).postDelayed({
            clickedBtn.setBackgroundColor(colorId)
            tvResult.text = getString(R.string.correct)
            generateQuestion()
        }, 300)
        tvPoints.text = "$points/$numberOfQuestions"
    }

    /**
     * Function to update the UI and logic when the user selects an incorrect answer.
     */
    private fun updateForIncorrectAnswer() {
        wrong++
        clickedBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.wrong))
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

    /**
     * Function to play a sound effect based on whether the user's answer was correct or incorrect.
     */
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

    /**
     * Function to start playing a MediaPlayer.
     */
    private fun startPlayer(myPlayer: MediaPlayer) {
        myPlayer.start()
        myPlayer.setOnCompletionListener { stopPlayer() }
    }

    /**
     * Function to stop and release a MediaPlayer.
     */
    private fun stopPlayer() {
        player?.release()
        player = null
    }
}
