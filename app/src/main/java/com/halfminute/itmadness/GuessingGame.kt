package com.halfminute.itmadness

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class GuessingGame : AppCompatActivity() {

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
    private val randomWords = arrayOf("Word1", "Word2", "Word3", "Word4")
    private val random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This object is used to access the stored shared preferences, specifically the theme preference.
        val sharedPref = SharedPref(this)

        // This needs to be done before setContentView is called, as the theme cannot be changed after the layout is inflated.
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)

        // This inflates the layout defined in R.layout.guessing_game and sets it as the content view for this activity.
        setContentView(R.layout.guessing_game)

        // These variables represent the TextViews defined in your layout file. findViewById is used to link them.
        tvTimer = findViewById(R.id.tvTimer)
        tvPoints = findViewById(R.id.tvPoints)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvLives = findViewById(R.id.tvLives)
        txtvw1 = findViewById(R.id.txtvw1)
        txtvw2 = findViewById(R.id.txtvw2)
        txtvw3 = findViewById(R.id.txtvw3)
        txtvw4 = findViewById(R.id.txtvw4)

        // This loop sets an onClickListener for each TextView, linking them to the onWordClick function.
        listOf(txtvw1, txtvw2, txtvw3, txtvw4).forEach { textView ->
            textView.setOnClickListener { onWordClick(it) }
        }

        // Initialize isSelectedMap for each TextView
        listOf(txtvw1, txtvw2, txtvw3, txtvw4).forEach { textView ->
            isSelectedMap[textView] = false
            textView.setOnClickListener { onWordClick(it) }
        }

        // This function initializes and starts the game logic.
        startGame()

        // This sets up a custom behavior for when the back button is pressed in this activity.
        handleBackButton()
    }

    private fun startGame() {
        generateRandomWords()
        selectedCount = 0
        tvPoints.text = getString(R.string.points, 0, 0)

        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = (millisUntilFinished / 1000).toString() + "s"
            }

            override fun onFinish() {
                calculatePoints()
                navigateToGameOver()
            }
        }.start()
    }

    private fun generateRandomWords() {
        val usedWords = mutableSetOf<String>()
        val textViews = listOf(txtvw1, txtvw2, txtvw3, txtvw4)
        textViews.forEach { textView ->
            var randomWord: String
            do {
                randomWord = randomWords[random.nextInt(randomWords.size)]
            } while (!usedWords.add(randomWord))
            textView.text = randomWord
            textView.setBackgroundColor(Color.WHITE)
        }
    }

    private fun onWordClick(view: View) {
        val clickedTextView = view as TextView

        // Toggle the selected state and update the map
        val isSelected = !(isSelectedMap[clickedTextView] ?: false)
        isSelectedMap[clickedTextView] = isSelected

        // Change the background color based on the selected state
        if (isSelected) {
            clickedTextView.setBackgroundColor(Color.GREEN)
        } else {
            clickedTextView.setBackgroundColor(Color.WHITE)
        }

        // Update the selected count
        updateSelectedCount()
    }

    private fun updateSelectedCount() {
        // Calculate the number of selected TextViews
        selectedCount = isSelectedMap.count { it.value }
    }

    private fun calculatePoints() {
        val points = selectedCount
        tvPoints.text = getString(R.string.points, points, 0)
    }

    private fun navigateToGameOver() {
        countDownTimer?.cancel()
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("points", selectedCount)
        }
        startActivity(intent)
        finish()
    }

    private fun handleBackButton() {
        onBackPressedDispatcher.addCallback(this) {
            countDownTimer?.cancel()
            navigateToGameOver()
        }
    }

    fun pauseGame(view: View) {
        countDownTimer?.cancel()
        navigateToPauseMenu()
    }

    private fun navigateToPauseMenu() {
        val intent = Intent(this, PauseMenu::class.java)
        startActivity(intent)
        finish()
    }
}
