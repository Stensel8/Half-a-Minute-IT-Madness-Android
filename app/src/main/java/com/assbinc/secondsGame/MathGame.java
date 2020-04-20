package com.assbinc.secondsGame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MathGame extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    double  op1, op2, correctAnswer, incorrectAnswer;
    TextView tvTimer, tvPoints, tvSum, tvResult, tvDifficulty, tvLives;
    Button btn0, btn1, btn2, btn3, clickedBtn;
    CountDownTimer countDownTimer;
    long millisUntilFinished;
    int points, wrong, maxWrongAnswers;
    int numberOfQuestions;
    Random random;
    int[] btnIds;
    int correctAnswerPosition;
    ArrayList<Double> incorrectAnswers;
    String[] operatorArray;
    DecimalFormat df = new DecimalFormat("0.##");
    SharedPref sharedPref;
    ColorDrawable initialColor;
    int colorId;
    String difficulty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_game);

        op1 = 0;
        op2 = 0;
        correctAnswer = 0;
        incorrectAnswer = 0;
        tvTimer = findViewById(R.id.tvTimer);
        tvPoints = findViewById(R.id.tvPoints);
        tvSum = findViewById(R.id.tvSum);
        tvResult = findViewById(R.id.tvResult);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        tvLives = findViewById(R.id.tvLives);
        millisUntilFinished = 30100;
        points = 0;
        wrong = 0;
        maxWrongAnswers = 2;
        numberOfQuestions = 0;
        random = new Random();
        btnIds = new int[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3};
        correctAnswerPosition = 0;
        incorrectAnswers = new ArrayList<>();
        operatorArray = new String[]{"+", "-", "*", "÷"};
        sharedPreferences = getSharedPreferences("gameDifficulty", Activity.MODE_PRIVATE);
        difficulty = sharedPreferences.getString("difficulty", "easy");
        startGame();

    }

    //set saved language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //load saved language
    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language);
    }

    public void pauseGame(View view) {

        countDownTimer.cancel();
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", "math");
        editor.commit();
        Intent intent = new Intent(MathGame.this, PauseMenu.class);
        intent.putExtra("actualGame","math");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        countDownTimer.cancel();
        Intent intent = new Intent(MathGame.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    private void startGame() {

        tvTimer.setText("" + (millisUntilFinished / 1000) + "s");
        tvPoints.setText("" + points + "/" + numberOfQuestions);
        generateQuestion();

        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();

    }

    private void gameOver(){
        if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        btn0.setClickable(false);
        btn1.setClickable(false);
        btn2.setClickable(false);
        btn3.setClickable(false);
        Intent intent = new Intent(MathGame.this, GameOver.class);
        intent.putExtra("points", points);
        startActivity(intent);
        finish();
    }

    private void generateQuestion() {
        numberOfQuestions++;
        String selectedOperator = operatorArray[random.nextInt(4)];
        if(difficulty.equalsIgnoreCase("easy")){
            tvDifficulty.setText(getResources().getString(R.string.difficultyEasy));
            maxWrongAnswers = 5;

            op1 = 1 + random.nextInt(9);
            while(true){
                op2 = 1 + random.nextInt(9);
                correctAnswer = getAnswer(selectedOperator);

                if(op2 < op1){
                    if(correctAnswer-(int)correctAnswer == 0)
                        break;
                }
                if (op2 == op1)
                    break;
            }
        }else if(difficulty.equalsIgnoreCase("medium")){
            tvDifficulty.setText(getResources().getString(R.string.difficultyMedium));

            op1 = random.nextInt(10);
            while(true){
                op2 = 1 + random.nextInt(9);
                correctAnswer = getAnswer(selectedOperator);

                if(correctAnswer-(int)correctAnswer == 0)
                    break;
            }
        }else{
            tvDifficulty.setText(getResources().getString(R.string.difficultyHard));
            op1 = random.nextInt(21);
            op2 = 1 + random.nextInt(20);
            correctAnswer = getAnswer(selectedOperator);

        }

        tvLives.setText((maxWrongAnswers+1) - wrong + "");
        tvSum.setText(df.format(op1) + " " + selectedOperator + " " + df.format(op2) + " = ");
        correctAnswerPosition = random.nextInt(4);

        ((Button) findViewById(btnIds[correctAnswerPosition])).setText(df.format(correctAnswer));

        setIncorrectAnswers(selectedOperator);
    }

    private void setIncorrectAnswers(String selectedOperator){
        while(true){
            if(incorrectAnswers.size() > 3)
            {
                break;
            }
            op1 = random.nextInt(10);
            op2 = 1 + random.nextInt(9);
            selectedOperator = operatorArray[random.nextInt(4)];
            incorrectAnswer = getAnswer(selectedOperator);

            if(incorrectAnswer == correctAnswer)
                continue;
            if(incorrectAnswers.contains(incorrectAnswer))
                continue;
            incorrectAnswers.add(incorrectAnswer);

//            Log.d("test", "incorrect: " + incorrectAnswers + " | correct: " + correctAnswer);

        }

        for(int i = 0; i< 4; i++){
            if(i == correctAnswerPosition)
                continue;

            ((Button) findViewById(btnIds[i])).setText(""+df.format(incorrectAnswers.get(i)));

        }

        incorrectAnswers.clear();
    }

    private double getAnswer(String selectedOperator) {
        double answer = 0;
        switch (selectedOperator){
            case "+":
                answer = op1 + op2;
                break;
            case "-":
                answer = op1 - op2;
                break;
            case "*":
                answer = op1 * op2;
                break;
            case "÷":
                answer = op1 / op2;
                break;
        }
        return answer;
    }

    public void chooseAnswer(View view) {

        clickedBtn = (Button) view;
        initialColor = (ColorDrawable) clickedBtn.getBackground();
        colorId = initialColor.getColor();

        if(!(view instanceof ImageButton)) {
            String answer = clickedBtn.getText().toString();
            String strCorrect = getResources().getString(R.string.correct);
            String strWrong = getResources().getString(R.string.wrong);

            if(answer.equals(df.format(correctAnswer))) {
                points++;
                //change the color of the clicked button to green
                clickedBtn.setBackgroundColor(getResources().getColor(R.color.correct));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //set it initial color
                        clickedBtn.setBackgroundColor(colorId);
                    }
                }, 500);
                tvResult.setText(strCorrect);

            }else{
                if(wrong < maxWrongAnswers)
                {
                    tvResult.setText(strWrong);
                    //change the color of the clicked button to green
                    clickedBtn.setBackgroundColor(getResources().getColor(R.color.wrong));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //set its initial color
                            clickedBtn.setBackgroundColor(colorId);
                        }
                    }, 500);
                    wrong++;
                }else{
                    gameOver();
                }
            }

            tvPoints.setText(points + "/" + numberOfQuestions);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //make the text disappear after 1s
                    tvResult.setText("");
                }
            }, 1000);
            generateQuestion();
        }
    }
}
