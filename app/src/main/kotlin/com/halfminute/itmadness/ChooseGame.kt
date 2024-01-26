package com.halfminute.itmadness

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.halfminute.itmadness.databinding.ChooseGameBinding
import kotlin.random.Random

class ChooseGame : AppCompatActivity() {

    private lateinit var sharedPref: SharedPref
    private lateinit var binding: ChooseGameBinding
    private var timer: CountDownTimer? = null
    private var isDiceReady = true
    private var remainingTime: Long = 0
    private var thrownDiceValue: Int? = null
    private var isEnteringGame = false

    private val diceSides = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(this)
        setTheme(if (sharedPref.loadNightMode()) R.style.darkTheme else R.style.lightTheme)
        sharedPref.loadLocale(this)
        binding = ChooseGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeUI(savedInstanceState)
        setOnBackPressedCallback()
        setOnClickListener()
        updateDiceState()

        // Retrieve the last thrown dice value and update the UI
        thrownDiceValue = sharedPref.loadLastThrownDiceValue()
        showLastThrownDiceValue()
    }

    private fun initializeUI(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            isDiceReady = it.getBoolean("isDiceReady", true)
            remainingTime = it.getLong("remainingTime")
            thrownDiceValue = sharedPref.loadLastThrownDiceValue()
            if (remainingTime > 0) startTimer(remainingTime)
        }
        showLastThrownDiceValue()
    }

    private fun showLastThrownDiceValue() {
        binding.diceValueTextView.visibility = if (thrownDiceValue != null) View.VISIBLE else View.GONE
        binding.diceValueTextView.text = getString(R.string.last_dice_value, thrownDiceValue)
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = navigateToMainActivity()
        })
    }

    private fun setOnClickListener() {
        with(binding) {
            btnMathGame.setOnClickListener { startGame("math", it) }
            btnLanguageGame.setOnClickListener { startGame("language", it) }
            btnGuessingGame.setOnClickListener { startGame("guessing", it) }
            diceRollButton.setOnClickListener {
                if (isDiceReady) {
                    rollDice()
                    showLastThrownDiceValue()
                    startTimer()
                } else {
                    showCustomSnackbar("Please wait for the dice to be ready!")
                    Log.d("ChooseGame", "Snackbar message shown: Please wait for the dice to be ready!")
                }
            }
        }
    }


    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun updateDiceState() {
        binding.diceRollButton.text = if (isDiceReady) getString(R.string.throw_dice) else "Thrown $thrownDiceValue. Waiting: 10s"
        updateButtonBackground(if (isDiceReady) R.color.dice_ready_color else R.color.dice_rolled_color)
        updateDiceBackgroundColor()
    }

    private fun updateButtonBackground(colorResId: Int) {
        binding.diceRollButton.setBackgroundColor(ContextCompat.getColor(this, colorResId))
    }

    private fun updateDiceBackgroundColor() {
        val backgroundColor = ContextCompat.getColor(this, if (isDiceReady) R.color.dice_ready_color else R.color.dice_rolled_color)
        binding.diceRollButton.setBackgroundColor(backgroundColor)
        binding.diceRollButton.isClickable = isDiceReady
    }

    private fun rollDice() {
        thrownDiceValue = Random.nextInt(1, diceSides + 1)
        isDiceReady = false
        sharedPref.saveLastThrownDiceValue(thrownDiceValue ?: 0)
        updateDiceState()
    }

    private fun startTimer(initialTime: Long = 10000) {
        timer?.cancel()
        timer = object : CountDownTimer(initialTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val remainingSeconds = millisUntilFinished / 1000
                binding.diceRollButton.text = "Thrown $thrownDiceValue. Waiting: $remainingSeconds s"
            }

            override fun onFinish() {
                isDiceReady = true
                updateDiceBackgroundColor()
                binding.diceRollButton.text = getString(R.string.throw_dice)
                if (isEnteringGame) isEnteringGame = false
            }
        }.start()
    }

    private fun startGame(gameType: String, view: View) {
        Settings.btnAnimation(view)
        sharedPref.saveChosenGame(gameType)
        isEnteringGame = true
        val intent = when (gameType) {
            "math" -> Intent(this, MathGame::class.java)
            "language" -> Intent(this, ChooseLanguageGame::class.java)
            "guessing" -> Intent(this, GuessingGame::class.java)
            else -> return
        }
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isDiceReady", isDiceReady)
        outState.putLong("remainingTime", remainingTime)
        thrownDiceValue?.let { outState.putInt("thrownDiceValue", it) }
    }

    private fun showCustomSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
