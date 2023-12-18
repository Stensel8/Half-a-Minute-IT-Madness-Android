package com.halfminute.itmadness

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.Random

class GuessingGame : AppCompatActivity() {
    var sharedPreferences: SharedPreferences? = null
    var op1 = 0.0
    var op2 = 0.0
    var correctAnswer = 0.0
    var incorrectAnswer = 0.0
    var tvTimer: TextView? = null
    var tvPoints: TextView? = null
    var tvSum: TextView? = null
    var tvResult: TextView? = null
    var tvDifficulty: TextView? = null
    var tvLives: TextView? = null
    var btn0: Button? = null
    var btn1: Button? = null
    var btn2: Button? = null
    var btn3: Button? = null
    var clickedBtn: Button? = null
    var countDownTimer: CountDownTimer? = null
    var millisUntilFinished: Long = 0
    var points = 0
    var wrong = 0
    var maxWrongAnswers = 0
    var numberOfQuestions = 0
    var random: Random? = null
    var btnIds: IntArray = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3)
    var correctAnswerPosition = 0
    var incorrectAnswers: ArrayList<Double>? = null
    var operatorArray: Array<String> = arrayOf("+", "-", "*", "÷")
    var df: DecimalFormat? = null
    var sharedPref: SharedPref? = null
    var initialColor: ColorDrawable? = null
    var colorId = 0
    var difficulty: String? = null
    var player: MediaPlayer? = null
    private var timerPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        //check dark mode
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref!!.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref!!.loadLocale(this) //loads the saved language
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guessing_game)
        op1 = 0.0 //1st number of the operation
        op2 = 0.0 //2nd number of the operation
        correctAnswer = 0.0 //the answer of the question
        incorrectAnswer = 0.0 //the incorrect answers of the question
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvSum = findViewById(R.id.tvSum)
        tvResult = findViewById(R.id.tvResult)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        tvLives = findViewById(R.id.tvLives)
        millisUntilFinished = 30100 //30 seconds used for the timer
        points = 0
        wrong = 0 //the wrong answers from the player
        maxWrongAnswers = 2 //the maximum wrong answers allowed
        numberOfQuestions = 0
        df = DecimalFormat("0.##") //hide trailing zeros
        random = Random()
        btnIds = intArrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3) //the id's of the 4 buttons
        correctAnswerPosition = 0
        incorrectAnswers = ArrayList() //arrayList with all the incorrect answers
        operatorArray = arrayOf("+", "-", "*", "÷") //every possible operation of the math game
        sharedPreferences = getSharedPreferences("gameDifficulty", MODE_PRIVATE)
        this.sharedPreferences
        val difficulty: String? = sharedPreferences?.getString("difficulty", "easy")

        if (difficulty == null) {
            // Show an error message to the user
            Toast.makeText(this, "Error: Difficulty not found, default setting 'easy' will be used", Toast.LENGTH_SHORT).show()
            // Use the default value "easy"
        }

    // Now you can set up the game with 'difficulty'

        startGame()
    }

    fun pauseGame(view: View?) {
        //saves the current game we're playing
        val localSharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = localSharedPreferences.edit()
        editor.putString("actualGame", "math")
        editor.apply()
        countDownTimer!!.cancel()
        val intent = Intent(this@GuessingGame, PauseMenu::class.java)
        releasePlayer()
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        countDownTimer!!.cancel()
        val intent = Intent(this@GuessingGame, ChooseGame::class.java)
        startActivity(intent)
        finish()
    }

    private fun startGame() {
        tvTimer!!.text = (millisUntilFinished / 1000).toString() + "s"
        tvPoints!!.text = "$points/$numberOfQuestions"
        generateQuestion()
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val initialColor = tvTimer!!.currentTextColor
                tvTimer!!.text = seconds.toString() + "s"
                if (seconds <= 5) {
                    if (seconds == 5L) {
                        timerPlayer =
                            MediaPlayer.create(this@GuessingGame, R.raw.five_sec_countdown)
                        playTimerSound()
                    }
                    tvTimer!!.setTextColor(resources.getColor(R.color.wrong))
                    tvTimer!!.textSize = 26f
                    Handler().postDelayed({

                        //set it initial color and size
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

    private fun gameOver() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        btn0?.isClickable = false
        btn1?.isClickable = false
        btn2?.isClickable = false
        btn3?.isClickable = false

        val sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("actualGame", "math")
        editor.apply()

        val intent = Intent(this@GuessingGame, GameOver::class.java)
        intent.putExtra("points", points)
        intent.putExtra("difficulty", difficulty)
        intent.putExtra("chosenGame", "math")
        releasePlayer()
        startActivity(intent)
        finish()
    }

    private fun releasePlayer() {
        if (timerPlayer != null && sharedPref!!.sound) {
            timerPlayer!!.release()
        }
    }

    private fun generateQuestion() {
        numberOfQuestions++
        val selectedOperator = operatorArray[random!!.nextInt(4)] //select a random operator

        ///////////////// easy mode ////////////////////////
        if (difficulty.equals("easy", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyEasy)

            //when easy mode is turned on, the player get 6 lives instead of 3
            maxWrongAnswers = 5
            op1 = (1 + random!!.nextInt(9)).toDouble()
            while (true) {
                op2 = (1 + random!!.nextInt(9)).toDouble()
                correctAnswer = getAnswer(selectedOperator)

                //if the 2nd number is smaller than the 1st one and if the answer isn't a decimal number
                //then we stop the loop
                if (op2 < op1 && correctAnswer - correctAnswer.toInt() == 0.0) break
                if (op2 == op1) break
            }

            ///////////////// medium mode ////////////////////////
        } else if (difficulty.equals("medium", ignoreCase = true)) {
            tvDifficulty!!.text = resources.getString(R.string.difficultyMedium)
            op1 = random!!.nextInt(10).toDouble()
            while (true) {
                op2 = (1 + random!!.nextInt(9)).toDouble()
                correctAnswer = getAnswer(selectedOperator)
                if (correctAnswer - correctAnswer.toInt() == 0.0) break
            }

            ///////////////// hard mode ////////////////////////
        } else {
            tvDifficulty!!.text = resources.getString(R.string.difficultyHard)
            op1 = random!!.nextInt(21).toDouble()
            op2 = (1 + random!!.nextInt(20)).toDouble()
            correctAnswer = getAnswer(selectedOperator)
        }

        //update the live of the player on every question
        tvLives!!.text = (maxWrongAnswers + 1 - wrong).toString()
        tvSum!!.text = df!!.format(op1) + " " + selectedOperator + " " + df!!.format(op2) + " = "
        correctAnswerPosition = random!!.nextInt(4)
        (findViewById<View>(btnIds[correctAnswerPosition]) as Button).text =
            df!!.format(correctAnswer)
        setIncorrectAnswers()
    }

    private fun setIncorrectAnswers() {
        while (true) {
            if (incorrectAnswers!!.size > 3) {
                break
            }
            val selectedOperator = operatorArray[random!!.nextInt(4)]
            if (difficulty.equals("hard", ignoreCase = true)) {
                op1 = random!!.nextInt(21).toDouble()
                op2 = (1 + random!!.nextInt(20)).toDouble()
                incorrectAnswer = getAnswer(selectedOperator)
            } else {
                op1 = random!!.nextInt(10).toDouble()
                while (true) {
                    op2 = (1 + random!!.nextInt(9)).toDouble()
                    incorrectAnswer = getAnswer(selectedOperator)
                    if (incorrectAnswer - incorrectAnswer.toInt() == 0.0) break
                }
            }


            //to make sure we never get multiple correct answers
            if (incorrectAnswer == correctAnswer) continue
            //to make sure we never get the same incorrect answer on the other buttons
            if (incorrectAnswers!!.contains(incorrectAnswer)) continue
            incorrectAnswers!!.add(incorrectAnswer)

//            Log.d("test", "incorrect: " + incorrectAnswers + " | correct: " + correctAnswer);
        }

        //get all the incorrect answers and assign them to a button one by one
        for (i in 0..3) {
            //doesn't put an incorrect answer at the correct answer's button
            if (i == correctAnswerPosition) continue
            (findViewById<View>(btnIds[i]) as Button).text = df!!.format(
                incorrectAnswers!![i]
            )
        }

        //now that all the buttons are assigned, we have to clear the arrayList for the next question
        incorrectAnswers!!.clear()
    }

    private fun getAnswer(selectedOperator: String): Double {
        return when (selectedOperator) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "*" -> op1 * op2
            "÷" -> op1 / op2
            else -> 0.0 // Cast 0 to Double
        }
    }

    fun chooseAnswer(view: View?) {
        clickedBtn = view as Button?
        initialColor = clickedBtn!!.background as ColorDrawable
        colorId = initialColor!!.color //get the clicked button backgroundColor

        //to make sure we don't verify the answer when clicking on the pause button
        if (view !is ImageButton) {
            val answer = clickedBtn!!.text.toString() //we get the answer of the player
            val strCorrect = resources.getString(R.string.correct)
            val strWrong = resources.getString(R.string.wrong)
            var isCorrect = false
            if (answer == df!!.format(correctAnswer)) {
                isCorrect = true
                playSound(isCorrect)
                points++
                //change the color of the clicked button to green
                clickedBtn!!.setBackgroundColor(resources.getColor(R.color.correct))
                Handler().postDelayed({
                    //set it initial color
                    clickedBtn!!.setBackgroundColor(colorId)
                }, 300)
                tvResult!!.text = strCorrect
            } else {
                if (wrong < maxWrongAnswers) {
                    playSound(isCorrect)
                    tvResult!!.text = strWrong
                    //change the color of the clicked button to green
                    clickedBtn!!.setBackgroundColor(resources.getColor(R.color.wrong))
                    Handler().postDelayed({
                        //set its initial color
                        clickedBtn!!.setBackgroundColor(colorId)
                    }, 300)
                    wrong++
                } else {
                    gameOver()
                }
            }
            tvPoints!!.text = "$points/$numberOfQuestions"
            Handler().postDelayed({
                //make the text disappear after 1s
                tvResult!!.text = ""
            }, 1000)
            generateQuestion()
        }
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
        myPlayer.setOnCompletionListener { mp: MediaPlayer? -> stopPlayer() }
    }

    private fun stopPlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }
}
