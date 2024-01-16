package com.halfminute.itmadness

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import java.util.Random

class GuessingGame : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var words: List<Word>
    private lateinit var tvTimer: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvLives: TextView
    private lateinit var txtvw1: TextView
    private lateinit var txtvw2: TextView
    private lateinit var txtvw3: TextView
    private lateinit var txtvw4: TextView
    private var countDownTimer: CountDownTimer? = null
    private val millisUntilFinished = 30100L
    private var selectedCount = 0
    private val isSelectedMap = mutableMapOf<TextView, Boolean>()
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(this)

        // Set theme and content view
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        setContentView(R.layout.guessing_game)

        // Initialize TextViews
        initializeTextViews()

        // Load words based on difficulty and language
        loadWords()

        // Start game and handle back button
        startGame()
        handleBackButton()

    }

    private fun initializeTextViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvLives = findViewById(R.id.tvLives)
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
            // Show a custom Snackbar message if an error occurs
            sharedPref.showCustomSnackbar(
                this,
                "Invalid difficulty saved: $difficultyString. Default value 'EASY' is used."
            )
            Difficulty.EASY // Default value if an invalid one is saved
        }

        sharedPref.getSavedLanguage(this)
        words = Words.getInstance(this)?.getWordsByDifficulty(difficulty) ?: emptyList()
        tvDifficulty.text = difficulty.name
    }

    private fun generateRandomWords() {
        val usedWords = mutableSetOf<Word>()
        val textViews = listOf(txtvw1, txtvw2, txtvw3, txtvw4)
        textViews.forEach { textView ->
            var randomWord: Word
            do {
                randomWord = words[random.nextInt(words.size)]
            } while (!usedWords.add(randomWord))
            // Set the text based on the selected language
            textView.text = when (sharedPref.getSavedLanguage(this)) {
                "fr" -> randomWord.frWord
                "nl" -> randomWord.nlWord
                "en" -> randomWord.enWord
                "de" -> randomWord.deWord
                else -> randomWord.enWord  // Default to English
            }
            textView.setBackgroundColor(Color.WHITE)
        }
    }

    private fun startGame() {
        generateRandomWords()
        selectedCount = 0
        tvPoints.text = getString(R.string.points, 0, 0)

        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
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
        val points = selectedCount
        tvPoints.text = getString(R.string.points, points, 0)
    }

    private fun navigateToGameOver() {
        countDownTimer?.cancel()
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("points", selectedCount)
            putExtra("chosenGame", "guessing") // Add the game type here
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

    private fun handleBackButton() {
        onBackPressedDispatcher.addCallback(this) {
            countDownTimer?.cancel()
            navigateToGameOver()
        }
    }

    fun pauseGame(view: View) {
        sharedPref.saveChosenGame("guessing")

        countDownTimer?.cancel()

        // If there's any media player, stop it here
        // mediaPlayer?.stop()

        val intent = Intent(this, PauseMenu::class.java)
        startActivity(intent)
        finish()
    }


}
