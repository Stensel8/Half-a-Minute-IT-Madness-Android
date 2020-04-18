package com.sandipbhattacharya.mathgames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Language_test extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    Random random;
    String correctAnswer, incorrectAnswer;
    TextView tvQuestion, tvTimer, tvPoints, tvResult;
    Button btn0, btn1, btn2, btn3;
    CountDownTimer countDownTimer;
    long millisUntilFinished;
    int points, wrong, maxWrongAnswers;
    int numberOfQuestions;
    int[] btnIds;
    int correctAnswerPosition;
    ArrayList<String> incorrectAnswers;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String chosenGame = getIntent().getStringExtra("chosenGame");

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_test);

        correctAnswer = "";
        incorrectAnswer = "";
        tvTimer = findViewById(R.id.tvTimer);
        tvPoints = findViewById(R.id.tvPoints);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvResult = findViewById(R.id.tvResult2);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        millisUntilFinished = 30100;
        points = 0;
        wrong = 0;
        maxWrongAnswers = 2;
        numberOfQuestions = 0;
        random = new Random();
        btnIds = new int[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3};
        correctAnswerPosition = 0;
        incorrectAnswers = new ArrayList<>();
        gson = new Gson();

        test();
        startGame();

        /*int questionRand = random.nextInt(26/2)*2; //select an even random number to get the associated word as a question
        int answerRand = questionRand+1; //get the answer from the random question

        TextView tvAnswer = findViewById(R.id.btn0);

        tvQuestion.setText(questionRand + "");
        tvAnswer.setText(answerRand + "");*/

//        if (Locale.getDefault().getLanguage().contentEquals("fr")){
//            switch (chosenGame){
//                case ("NlToEn"):
//                    tvQuestion.setText(getResources().getString(R.string.DutchToEnglish));
//
//                    break;
//                case("EnToNl"):
//                    tvQuestion.setText(getResources().getString(R.string.EnglishToDutch));
//
//                    break;
//                case("FrToEn"):
//                    tvQuestion.setText(getResources().getString(R.string.FrenchToEnglish));
//
//                    break;
//                case("EnToFr"):
//                    tvQuestion.setText(getResources().getString(R.string.EnglishToFrench));
//
//                    break;
//            }
//        }
}

    public void test(){
        String json = "{ 'words': [ { 'fr':'bonjour', 'en':'hello', 'nl':'goeiedag' ] }";

        gson.fromJson(json,Words[].class);
    }

    public void pauseGame(View view) {
        String chosenGame = getIntent().getStringExtra("chosenGame");
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", chosenGame);
        editor.commit();
        countDownTimer.cancel();
        Intent intent = new Intent(Language_test.this, PauseMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
        Intent intent = new Intent(Language_test.this, ChooseLanguageGame.class);
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

    private void generateQuestion() {

        numberOfQuestions++;

    }

    private void gameOver() {
        /*if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        btn0.setClickable(false);
        btn1.setClickable(false);
        btn2.setClickable(false);
        btn3.setClickable(false);
        Intent intent = new Intent(Language_test.this, GameOver.class);
        intent.putExtra("points", points);
        startActivity(intent);
        finish();*/
    }

    public void chooseAnswer(View view) {

        if (!(view instanceof ImageButton)) {
            String answer = ((Button) view).getText().toString();
            String strCorrect = getResources().getString(R.string.correct);
            String strWrong = getResources().getString(R.string.wrong);

            if (answer.equals(correctAnswer)) {
                points++;
                tvResult.setText(strCorrect);
            } else {
                if (wrong < maxWrongAnswers) {
                    tvResult.setText(strWrong);
                    wrong++;
                } else {
                    gameOver();
                }
            }

            tvPoints.setText(points + "/" + numberOfQuestions);
            generateQuestion();
        }
    }
}
