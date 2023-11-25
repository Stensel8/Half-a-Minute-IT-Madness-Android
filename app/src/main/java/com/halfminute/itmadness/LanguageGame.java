package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.halfminute.itmadness.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class LanguageGame extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences, pref;
    Random random;
    String correctAnswer, incorrectAnswer, savedLanguage,chosenGame, difficulty;
    TextView tvQuestion, tvTimer, tvPoints, tvResult, tvLives, tvDifficulty;
    Button btn0, btn1, btn2, btn3, clickedBtn;
    CountDownTimer countDownTimer;
    long millisUntilFinished;
    int points, wrong, maxWrongAnswers, numberOfQuestions, randomId, previousRandom;
    int[] btnIds;
    int correctAnswerPosition;
    ArrayList<String> incorrectAnswers;
    Gson gson;
    Words wordsList;
    private MediaPlayer player;
    private MediaPlayer timerPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this); // Loads the saved language

        pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        savedLanguage = pref.getString("My lang", java.util.Locale.getDefault().getLanguage()); // Current language of the game

        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_test);

        // Initialize variables
        correctAnswer = ""; // The answer of the question
        incorrectAnswer = ""; // The incorrect answers of the question
        tvTimer = findViewById(R.id.tvTimer);
        tvPoints = findViewById(R.id.tvPoints);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvResult = findViewById(R.id.tvResult2);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        tvLives = findViewById(R.id.tvLives);
        tvDifficulty = findViewById(R.id.tvDifficulty2);
        millisUntilFinished = 30100; // 30 seconds used for the timer
        points = 0;
        wrong = 0; // The wrong answers from the player
        maxWrongAnswers = 2; // The maximum wrong answers allowed
        numberOfQuestions = 0;
        random = new Random();
        randomId = 0; // Random used to get the question
        previousRandom = Integer.MAX_VALUE; // Random from the previous question // It is initialised to max Int to be sure that the first random will not be the same as this random
        btnIds = new int[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3}; // The id's of the 4 buttons
        correctAnswerPosition = 0;
        incorrectAnswers = new ArrayList<>(); // ArrayList with all the incorrect answers
        gson = new Gson(); // A Google library used to facilitate the use of json files
        wordsList = gson.fromJson(WordsJson.myWords, Words.class); // We get the json string in the WordsJson class thanks to the Words class
        chosenGame = getIntent().getStringExtra("chosenGame");
        sharedPreferences = getSharedPreferences("gameDifficulty", Activity.MODE_PRIVATE);
        difficulty = sharedPreferences.getString("difficulty", "easy");

        // Create a callback for onBackPressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                countDownTimer.cancel();
                Intent intent = new Intent(LanguageGame.this, ChooseLanguageGame.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback); // Add the callback to the onBackPressedDispatcher

        startGame();
    }

    public void pauseGame(View view) {
        // Saves the current game we're playing
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", chosenGame);
        editor.apply();

        countDownTimer.cancel();
        Intent intent = new Intent(LanguageGame.this, PauseMenu.class);
        releasePlayer();
        startActivity(intent);
        finish();
    }


    private void startGame() {

        tvTimer.setText(getString(R.string.timer_seconds, millisUntilFinished / 1000));
        tvPoints.setText(getString(R.string.score_format, points, numberOfQuestions));
        generateQuestion();

        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                int initialColor = tvTimer.getCurrentTextColor();

                tvTimer.setText(getString(R.string.timer_seconds, seconds));

                if(seconds <= 5){
                    if (seconds == 5){
                        timerPlayer = MediaPlayer.create(LanguageGame.this, R.raw.five_sec_countdown);
                        playTimerSound();
                    }
                    tvTimer.setTextColor(getResources().getColor(R.color.wrong, getTheme()));
                    tvTimer.setTextSize(26);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
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

    private void generateQuestion() {

        numberOfQuestions++;

        /////////////////////// easy mode /////////////////////////
        if (difficulty.equalsIgnoreCase("easy")){

            tvDifficulty.setText(getResources().getString(R.string.difficultyEasy));

            //if the random is the same as the random from the previous question
            //we get a new random
            randomId = random.nextInt(wordsList.getEasyWords().size());

            while (randomId == previousRandom){
                randomId = random.nextInt(wordsList.getEasyWords().size());
            }

            //when easy mode is turned on, the player get 6 lives instead of 3
            maxWrongAnswers = 5;

            if(savedLanguage.equals("fr")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getEasyWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getEasyWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                }
            }else if(savedLanguage.equals("nl")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getEasyWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getEasyWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                }
            }else {
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getEasyWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getEasyWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getEasyWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                    }
                }
            }

            /////////////////////// medium mode /////////////////////////

        }else if(difficulty.equalsIgnoreCase("medium")){

            tvDifficulty.setText(getResources().getString(R.string.difficultyMedium));

            randomId = random.nextInt(wordsList.getMediumWords().size());

            while (randomId == previousRandom){
                randomId = random.nextInt(wordsList.getMediumWords().size());
            }

            if(savedLanguage.equals("fr")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getMediumWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getMediumWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                }
            }else if(savedLanguage.equals("nl")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getMediumWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getMediumWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                }
            }else {
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getMediumWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getMediumWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getMediumWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                    }
                }
            }

            /////////////////////// hard mode /////////////////////////

        }else{

            tvDifficulty.setText(getResources().getString(R.string.difficultyHard));

            randomId = random.nextInt(wordsList.getHardWords().size());

            while (randomId == previousRandom){
                randomId = random.nextInt(wordsList.getHardWords().size());
            }

            if(savedLanguage.equals("fr")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getHardWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getHardWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                }
            }else if(savedLanguage.equals("nl")){
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getHardWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getHardWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                }
            }else {
                String question;
                switch (chosenGame) {
                    case "NlToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getNlWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                    case "EnToNl" -> {
                        question = wordsList.getHardWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                    }
                    case "FrToEn" -> {
                        question = wordsList.getHardWords().get(randomId).getFrWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                    case "EnToFr" -> {
                        question = wordsList.getHardWords().get(randomId).getEnWord();
                        tvQuestion.setText(question);
                        correctAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                    }
                }
            }
        }

        //we store the random to check it on the next question
        previousRandom = randomId;

        //update the live of the player on every question
        tvLives.setText(getString(R.string.lives, (maxWrongAnswers+1) - wrong));

        //little bonus when you're at the 30th question
        if(numberOfQuestions == 31){
            correctAnswer = "30 seconds game";
            tvQuestion.setText(getString(R.string.game_time));
        }

        //get a random position between the 4 buttons
        correctAnswerPosition = random.nextInt(4);

        ((Button) findViewById(btnIds[correctAnswerPosition])).setText(correctAnswer);

        setIncorrectAnswers();

    }

    //assign the incorrect answers to the other buttons
    private void setIncorrectAnswers(){
        while(true) {

            ///////////////////// easy mode //////////////////////////

            if (difficulty.equalsIgnoreCase("easy")) {
                if (incorrectAnswers.size() > 3) {
                    break;
                }

                randomId = random.nextInt(wordsList.getEasyWords().size());

                if(savedLanguage.equals("fr")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                }else if(savedLanguage.equals("nl")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                    }
                }else{
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getEnWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getEasyWords().get(randomId).getFrWord();
                    }
                }

                //to make sure we never get multiple correct answers
                if(Objects.equals(incorrectAnswer, correctAnswer))
                    continue;
                //to make sure we never get the same incorrect answer on the other buttons
                if(incorrectAnswers.contains(incorrectAnswer))
                    continue;
                incorrectAnswers.add(incorrectAnswer);

                ///////////////////// medium mode //////////////////////////

            }else if(difficulty.equalsIgnoreCase("medium")){
                if (incorrectAnswers.size() > 3) {
                    break;
                }

                randomId = random.nextInt(wordsList.getMediumWords().size());

                if(savedLanguage.equals("fr")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                }else if(savedLanguage.equals("nl")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                    }
                }else{
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getEnWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getMediumWords().get(randomId).getFrWord();
                    }
                }

                if(Objects.equals(incorrectAnswer, correctAnswer))
                    continue;
                if(incorrectAnswers.contains(incorrectAnswer))
                    continue;
                incorrectAnswers.add(incorrectAnswer);

                ///////////////////// hard mode //////////////////////////
            }else{
                if (incorrectAnswers.size() > 3) {
                    break;
                }
                randomId = random.nextInt(wordsList.getHardWords().size());

                if(savedLanguage.equals("fr")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                }else if(savedLanguage.equals("nl")){
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                    }
                }else{
                    switch (chosenGame) {
                        case "NlToEn", "FrToEn" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getEnWord();
                        case "EnToNl" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getNlWord();
                        case "EnToFr" ->
                                incorrectAnswer = wordsList.getHardWords().get(randomId).getFrWord();
                    }
                }

                if(Objects.equals(incorrectAnswer, correctAnswer))
                    continue;
                if(incorrectAnswers.contains(incorrectAnswer))
                    continue;
                incorrectAnswers.add(incorrectAnswer);
            }
        }

        //get all the incorrect answers and assign them to a button one by one
        for(int i = 0; i< 4; i++){
            //doesn't put an incorrect answer at the correct answer's button
            if(i == correctAnswerPosition)
                continue;

            ((Button) findViewById(btnIds[i])).setText(incorrectAnswers.get(i));

        }

        //now that all the buttons are assigned, we have to clear the arrayList for the next question
        incorrectAnswers.clear();
    }
    private void gameOver() {
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
        editor.putString("actualGame", chosenGame);
        editor.apply();
        Intent intent = new Intent(LanguageGame.this, GameOver.class);
        intent.putExtra("points", points);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("chosenGame", "languageGame");
        releasePlayer();
        startActivity(intent);
        finish();
    }

    private void releasePlayer() {
        if(timerPlayer != null && sharedPref.getSound()){
            timerPlayer.release();
        }
    }


    public void chooseAnswer(View view) {

        //Log.i("GSon", "size: " + wordsList.getHardWords().size() + " | random: " + randomId + " | GSon: " + wordsList.getHardWords().get(randomId).getFrWord());

        clickedBtn = (Button) view;

        //to make sure we don't verify the answer when clicking on the pause button
        String answer = clickedBtn.getText().toString(); //we get the answer of the player
        String strCorrect = getResources().getString(R.string.correct);
        String strWrong = getResources().getString(R.string.wrong);
        boolean isCorrect = false;

        if (answer.equalsIgnoreCase(correctAnswer)) {
            isCorrect = true;
            playSound(isCorrect);
            points++;

            //change the color of the clicked button to green
            clickedBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_green, null));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                //set its initial color
                clickedBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_btn, null));
            }, 500);


            tvResult.setText(strCorrect);
        } else {
            if (wrong < maxWrongAnswers) {
                playSound(isCorrect);

                //change the color of the clicked button to red
                clickedBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_red, null));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    //set its initial color
                    clickedBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_btn, null));
                }, 500);


                tvResult.setText(strWrong);
                wrong++;
            } else {
                gameOver();
            }
        }

        tvPoints.setText(getString(R.string.points, points, numberOfQuestions));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //make the text disappear after 1s
            tvResult.setText("");
        }, 1000);


        generateQuestion();
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
