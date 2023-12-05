package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    DecimalFormat df;
    SharedPref sharedPref;
    ColorDrawable initialColor;
    int colorId;
    String difficulty;
    MediaPlayer player;
    private MediaPlayer timerPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_game);

        op1 = 0; //1st number of the operation
        op2 = 0; //2nd number of the operation
        correctAnswer = 0; //the answer of the question
        incorrectAnswer = 0; //the incorrect answers of the question
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
        millisUntilFinished = 30100; //30 seconds used for the timer
        points = 0;
        wrong = 0; //the wrong answers from the player
        maxWrongAnswers = 2; //the maximum wrong answers allowed
        numberOfQuestions = 0;
        df = new DecimalFormat("0.##"); //hide trailing zeros
        random = new Random();
        btnIds = new int[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3}; //the id's of the 4 buttons
        correctAnswerPosition = 0;
        incorrectAnswers = new ArrayList<>(); //arrayList with all the incorrect answers
        operatorArray = new String[]{"+", "-", "*", "÷"}; //every possible operation of the math game
        sharedPreferences = getSharedPreferences("gameDifficulty", Activity.MODE_PRIVATE);
        difficulty = sharedPreferences.getString("difficulty", "easy");
        startGame();

    }

    public void pauseGame(View view) {
        //saves the current game we're playing
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", "math");
        editor.apply();

        countDownTimer.cancel();
        Intent intent = new Intent(MathGame.this, PauseMenu.class);
        releasePlayer();
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

        tvTimer.setText((millisUntilFinished / 1000) + "s");
        tvPoints.setText(points + "/" + numberOfQuestions);
        generateQuestion();

        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                int initialColor = tvTimer.getCurrentTextColor();

                tvTimer.setText((seconds) + "s");

                if(seconds <= 5){
                    if (seconds == 5){
                        timerPlayer = MediaPlayer.create(MathGame.this, R.raw.five_sec_countdown);
                        playTimerSound();
                    }
                    tvTimer.setTextColor(getResources().getColor(R.color.wrong));
                    tvTimer.setTextSize(26);
                    new Handler().postDelayed(() -> {
                        //set it initial color and size
                        tvTimer.setTextSize(24);
                        tvTimer.setTextColor(initialColor);
                    }, 300);
                }
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();

    }

    private void playTimerSound() {
        if (sharedPref.getSound()){
            startPlayer(timerPlayer);
        }
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
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", "math");
        editor.apply();
        Intent intent = new Intent(MathGame.this, GameOver.class);
        intent.putExtra("points", points);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("chosenGame", "math");
        releasePlayer();
        startActivity(intent);
        finish();
    }

    private void releasePlayer() {
        if(timerPlayer != null && sharedPref.getSound()){
            timerPlayer.release();
        }
    }

    private void generateQuestion() {
        numberOfQuestions++;
        String selectedOperator = operatorArray[random.nextInt(4)]; //select a random operator

        ///////////////// easy mode ////////////////////////

        if(difficulty.equalsIgnoreCase("easy")){
            tvDifficulty.setText(getResources().getString(R.string.difficultyEasy));

            //when easy mode is turned on, the player get 6 lives instead of 3
            maxWrongAnswers = 5;

            op1 = 1 + random.nextInt(9);

            while(true){
                op2 = 1 + random.nextInt(9);
                correctAnswer = getAnswer(selectedOperator);

                //if the 2nd number is smaller than the 1st one and if the answer isn't a decimal number
                //then we stop the loop
                if(op2 < op1 && correctAnswer-(int)correctAnswer == 0)
                    break;
                if (op2 == op1)
                    break;
            }

            ///////////////// medium mode ////////////////////////

        }else if(difficulty.equalsIgnoreCase("medium")){
            tvDifficulty.setText(getResources().getString(R.string.difficultyMedium));

            op1 = random.nextInt(10);
            while(true){
                op2 = 1 + random.nextInt(9);
                correctAnswer = getAnswer(selectedOperator);

                if(correctAnswer-(int)correctAnswer == 0)
                    break;
            }

            ///////////////// hard mode ////////////////////////

        }else{
            tvDifficulty.setText(getResources().getString(R.string.difficultyHard));
            op1 = random.nextInt(21);
            op2 = 1 + random.nextInt(20);
            correctAnswer = getAnswer(selectedOperator);

        }

        //update the live of the player on every question
        tvLives.setText(String.valueOf((maxWrongAnswers + 1) - wrong));
        tvSum.setText(df.format(op1) + " " + selectedOperator + " " + df.format(op2) + " = ");
        correctAnswerPosition = random.nextInt(4);

        ((Button) findViewById(btnIds[correctAnswerPosition])).setText(df.format(correctAnswer));

        setIncorrectAnswers();
    }

    private void setIncorrectAnswers(){

        while(true){
            if(incorrectAnswers.size() > 3)
            {
                break;
            }

            String selectedOperator = operatorArray[random.nextInt(4)];

            if(difficulty.equalsIgnoreCase("hard")){
                op1 = random.nextInt(21);
                op2 = 1 + random.nextInt(20);
                incorrectAnswer = getAnswer(selectedOperator);

            }else {
                op1 = random.nextInt(10);
                while(true){
                    op2 = 1 + random.nextInt(9);
                    incorrectAnswer = getAnswer(selectedOperator);

                    if(incorrectAnswer-(int)incorrectAnswer == 0)
                        break;
                }
            }


            //to make sure we never get multiple correct answers
            if(incorrectAnswer == correctAnswer)
                continue;
            //to make sure we never get the same incorrect answer on the other buttons
            if(incorrectAnswers.contains(incorrectAnswer))
                continue;
            incorrectAnswers.add(incorrectAnswer);

//            Log.d("test", "incorrect: " + incorrectAnswers + " | correct: " + correctAnswer);

        }

        //get all the incorrect answers and assign them to a button one by one
        for(int i = 0; i< 4; i++){
            //doesn't put an incorrect answer at the correct answer's button
            if(i == correctAnswerPosition)
                continue;

            ((Button) findViewById(btnIds[i])).setText(df.format(incorrectAnswers.get(i)));

        }

        //now that all the buttons are assigned, we have to clear the arrayList for the next question
        incorrectAnswers.clear();
    }

    private double getAnswer(String selectedOperator) {
        double answer = switch (selectedOperator) {
            case "+" -> op1 + op2;
            case "-" -> op1 - op2;
            case "*" -> op1 * op2;
            case "÷" -> op1 / op2;
            default -> 0;
        };
        return answer;
    }

    public void chooseAnswer(View view) {

        clickedBtn = (Button) view;
        initialColor = (ColorDrawable) clickedBtn.getBackground();
        colorId = initialColor.getColor(); //get the clicked button backgroundColor

        //to make sure we don't verify the answer when clicking on the pause button
        if(!(view instanceof ImageButton)) {
            String answer = clickedBtn.getText().toString(); //we get the answer of the player
            String strCorrect = getResources().getString(R.string.correct);
            String strWrong = getResources().getString(R.string.wrong);
            boolean isCorrect = false;

            if(answer.equals(df.format(correctAnswer))) {
                isCorrect = true;
                playSound(isCorrect);
                points++;
                //change the color of the clicked button to green
                clickedBtn.setBackgroundColor(getResources().getColor(R.color.correct));

                new Handler().postDelayed(() -> {
                    //set it initial color
                    clickedBtn.setBackgroundColor(colorId);
                }, 300);
                tvResult.setText(strCorrect);

            }else{
                if(wrong < maxWrongAnswers)
                {
                    playSound(isCorrect);
                    tvResult.setText(strWrong);
                    //change the color of the clicked button to green
                    clickedBtn.setBackgroundColor(getResources().getColor(R.color.wrong));

                    new Handler().postDelayed(() -> {
                        //set its initial color
                        clickedBtn.setBackgroundColor(colorId);
                    }, 300);
                    wrong++;
                }else{
                    gameOver();
                }
            }

            tvPoints.setText(points + "/" + numberOfQuestions);

            new Handler().postDelayed(() -> {
                //make the text disappear after 1s
                tvResult.setText("");
            }, 1000);

            generateQuestion();
        }
    }

    private void playSound(boolean isCorrect) {
        if (sharedPref.getSound()){
            if (isCorrect){

                //stops the previous sound
                stopPlayer();

                player = MediaPlayer.create(this,R.raw.correct_fav);
                startPlayer(player);

            }else{
                //stops the previous sound
                stopPlayer();

                player = MediaPlayer.create(this,R.raw.wrong);
                startPlayer(player);
            }
        }
    }

    private void startPlayer(MediaPlayer myPlayer) {
        myPlayer.start();
        myPlayer.setOnCompletionListener(mp -> stopPlayer());
    }

    private void stopPlayer() {
        if(player != null){
            player.release();
            player = null;
        }
    }
}
