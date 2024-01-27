package com.halfminute.itmadness

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import java.util.Random

class GuessingGame : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var words: List<Word>
    private lateinit var tvTimer: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var txtvw1: TextView
    private lateinit var txtvw2: TextView
    private lateinit var txtvw3: TextView
    private lateinit var txtvw4: TextView
    private var countDownTimer: CountDownTimer? = null
    private val millisUntilFinished = 30100L
    private var selectedCount = 0
    private val isSelectedMap = mutableMapOf<TextView, Boolean>()
    private val random = Random()
    private var isBackButtonPressed = false
    private var pointsPerCorrectAnswer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(this)

        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        setContentView(R.layout.guessing_game)

        initializeTextViews()
        loadWords()
        startGame()
        setupOnBackPressedCallback()
    }

    private fun initializeTextViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        txtvw1 = findViewById(R.id.txtvw1)
        txtvw2 = findViewById(R.id.txtvw2)
        txtvw3 = findViewById(R.id.txtvw3)
        txtvw4 = findViewById(R.id.txtvw4)

        listOf(txtvw1, txtvw2, txtvw3, txtvw4).forEach { textView ->
            isSelectedMap[textView] = false
            textView.setOnClickListener { onWordClick(it) }
        }
    }

    private fun loadWords() {
        val difficultyString = sharedPref.loadDifficulty().uppercase(Locale.getDefault())
        val difficulty = try {
            Difficulty.valueOf(difficultyString)
        } catch (e: IllegalArgumentException) {
            sharedPref.showCustomSnackbar(
                this,
                "Invalid difficulty saved: $difficultyString. Default value 'EASY' is used."
            )
            Difficulty.EASY
        }

        sharedPref.getSavedLanguage(this)
        words = Words.getInstance(this)?.getWordsByDifficulty(difficulty) ?: emptyList()
        tvDifficulty.text = difficulty.name

        pointsPerCorrectAnswer = when (difficulty) {
            Difficulty.EASY -> 2
            Difficulty.MEDIUM -> 3
            Difficulty.HARD -> 4
        }
    }


    private fun generateRandomWords() {
        val usedWords = mutableSetOf<Word>()
        val textViews = listOf(txtvw1, txtvw2, txtvw3, txtvw4)
        textViews.forEach { textView ->
            var randomWord: Word
            do {
                randomWord = words[random.nextInt(words.size)]
            } while (!usedWords.add(randomWord))

            textView.text = when (sharedPref.getSavedLanguage(this)) {
                "fr" -> randomWord.frWord
                "nl" -> randomWord.nlWord
                "en" -> randomWord.enWord
                "de" -> randomWord.deWord
                else -> randomWord.enWord
            }
            textView.setBackgroundColor(Color.WHITE)
        }
    }

    private fun startGame() {
        generateRandomWords()
        selectedCount = 0
        tvPoints.text = getString(R.string.points, 0, pointsPerCorrectAnswer)

        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                calculatePoints()
                navigateToGameOver()
            }
        }.start()
    }

    private fun calculatePoints() {
        val points = selectedCount * pointsPerCorrectAnswer
        tvPoints.text = getString(R.string.points, points, 0)
    }


    private fun navigateToGameOver() {
        countDownTimer?.cancel()
        val points = selectedCount * pointsPerCorrectAnswer // Calculate the points based on the selected count and points per correct answer
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("points", points) // Pass the calculated points
            putExtra("chosenGame", "guessing")
        }
        startActivity(intent)
        finish()
    }


    private fun onWordClick(view: View) {
        val clickedTextView = view as TextView
        val isSelected = !(isSelectedMap[clickedTextView] ?: false)
        isSelectedMap[clickedTextView] = isSelected

        clickedTextView.setBackgroundColor(if (isSelected) Color.GREEN else Color.WHITE)
        updateSelectedCount()
    }

    private fun updateSelectedCount() {
        selectedCount = isSelectedMap.count { it.value }
    }

    private fun setupOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                if (!isBackButtonPressed) {
                    isBackButtonPressed = true
                    sharedPref.showCustomSnackbar(
                        this@GuessingGame,
                        "Back button pressed. Press again to navigate back."
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        isBackButtonPressed = false
                    }, 3000)
                } else {
                    navigateToPreviousActivity()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToPreviousActivity() {
        val intent = Intent(this, ChooseGame::class.java)
        startActivity(intent)
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun pauseGame(ignoredView: View) {
        sharedPref.saveChosenGame("guessing")
        countDownTimer?.cancel()

        val intent = Intent(this, PauseMenu::class.java)
        startActivity(intent)
        finish()
    }
}
