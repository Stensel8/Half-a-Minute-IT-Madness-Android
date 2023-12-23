package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GuessingGame extends AppCompatActivity {

    TextView tvTimer, tvPoints, tvDifficulty, tvLives, txtvw1, txtvw2, txtvw3, txtvw4;
    CountDownTimer countDownTimer;
    long millisUntilFinished = 30100; // 30 seconds for the timer
    int points = 0;
    int selectedCount = 0;
    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    String difficulty;
    String[] randomWords = {"Word1", "Word2", "Word3", "Word4"}; // Voorbeeldwoorden
    Random random = new Random();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        setContentView(R.layout.guessing_game);

        // Opslaan van de huidige game in SharedPreferences
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", "guessing");
        editor.apply();

        // Ophalen van de moeilijkheidsgraad
        SharedPreferences difficultyPreferences = getSharedPreferences("gameDifficulty", Activity.MODE_PRIVATE);
        difficulty = difficultyPreferences.getString("difficulty", "easy");

        initViews();
        startGame();
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvPoints = findViewById(R.id.tvPoints);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvLives = findViewById(R.id.tvLives);
        txtvw1 = findViewById(R.id.txtvw1);
        txtvw2 = findViewById(R.id.txtvw2);
        txtvw3 = findViewById(R.id.txtvw3);
        txtvw4 = findViewById(R.id.txtvw4);

        TextView[] textViews = {txtvw1, txtvw2, txtvw3, txtvw4};
        for (TextView textView : textViews) {
            textView.setOnClickListener(this::onWordClick);
        }
    }

    private void startGame() {
        generateRandomWords();
        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(millisUntilFinished / 1000 + "s");
            }
            public void onFinish() {
                tvTimer.setText("0s");
                checkScore();
            }
        }.start();
    }

    private void generateRandomWords() {
        Set<String> usedWords = new HashSet<>();
        TextView[] textViews = {txtvw1, txtvw2, txtvw3, txtvw4};
        for (TextView textView : textViews) {
            String randomWord;
            do {
                randomWord = randomWords[random.nextInt(randomWords.length)];
            } while (!usedWords.add(randomWord));
            textView.setText(randomWord);
            textView.setBackgroundColor(Color.WHITE);
        }
    }

    private void onWordClick(View view) {
        TextView clickedTextView = (TextView) view;
        if (clickedTextView.getCurrentTextColor() == Color.GREEN) {
            clickedTextView.setBackgroundColor(Color.WHITE);
            selectedCount--;
        } else {
            clickedTextView.setBackgroundColor(Color.GREEN);
            selectedCount++;
        }
    }

    private void checkScore() {
        if (selectedCount == 4) {
            points += 4;
            tvPoints.setText("Punten: " + points);
        }
        selectedCount = 0;
        generateRandomWords();
    }

    public void pauseGame(View view) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent(GuessingGame.this, PauseMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent(GuessingGame.this, PauseMenu.class);
        startActivity(intent);
        finish();
    }
}
