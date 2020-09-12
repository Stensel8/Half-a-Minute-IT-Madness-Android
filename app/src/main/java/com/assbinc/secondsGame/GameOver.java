package com.assbinc.secondsGame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    SharedPreferences sharedPreferences;
    ImageView ivHighScore;
    TextView tvHighScore, tvChosenGame, tvDifficulty, tvTop1, tvTop2, tvTop3, tvTop4, tvTop5;
    TableLayout tlScore;
    Button btnGoLogin;
    SharedPref sharedPref;
    SessionManager session;
    int points;
    String difficulty, chosenGame;
    DatabaseHelper db;
    List<String> scoreList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        session = new SessionManager(this);

        tlScore = findViewById(R.id.tlScore);
        ivHighScore = findViewById(R.id.ivHighScore);
        tvHighScore = findViewById(R.id.tvHighScore);
        btnGoLogin = findViewById(R.id.btnGoLoginGover);
        tvTop1 = findViewById(R.id.tvTop1);
        tvTop2 = findViewById(R.id.tvTop2);
        tvTop3 = findViewById(R.id.tvTop3);
        tvTop4 = findViewById(R.id.tvTop4);
        tvTop5 = findViewById(R.id.tvTop5);
        tvPoints = findViewById(R.id.tvPoints);
        tvChosenGame = findViewById(R.id.tvChosenGame);
        tvDifficulty = findViewById(R.id.tvDifficultyGOver);

        //displays the top 5 if the user is connected
        tlScore.setVisibility(session.checkLoggedIn()? View.VISIBLE: View.INVISIBLE);
        btnGoLogin.setVisibility(session.checkLoggedIn()? View.GONE: View.VISIBLE);

        points = getIntent().getExtras().getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        sharedPreferences = getSharedPreferences("pref", 0);
        int pointsHC;
        db = new DatabaseHelper(this);

        //we get the user's high-score if he's logged-in. if not we get the saved high-score
        if(!session.isLoggedIn()){
            pointsHC = sharedPreferences.getInt("pointsHC", 0);
        }else {
            pointsHC = db.getProfileScore(session.getUsername());
            db.addScore(session.getUsername(),difficulty, chosenGame, points);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(points > pointsHC){
            //saves the high-score
            pointsHC = points;
            editor.putInt("pointsHC", pointsHC);
            editor.commit();

            //update the high-score if logged-in
            if(session.isLoggedIn()){
                db.updateScoreProfile(session.getUsername(), pointsHC);
            }

            ivHighScore.setVisibility(View.VISIBLE);
        }

        Cursor res = db.getHScore(difficulty, chosenGame);

        if(res != null){

            scoreList = new ArrayList<>();
            while (res.moveToNext()){
                //we add the name and the high-score of the 5 best users to the arrayList
                scoreList.add(res.getString(0) + ": " + res.getString(1));
            }
        }

        //we change the String values to display them in multiple languages
        if(chosenGame.equalsIgnoreCase("languageGame")){
            chosenGame = getResources().getString(R.string.languageGameButton);
        }else{
            chosenGame = getResources().getString(R.string.mathGameButton);
        }

        switch (difficulty){
            case "easy":
                difficulty = getResources().getString(R.string.difficultyEasy);
                break;
            case "medium":
                difficulty = getResources().getString(R.string.difficultyMedium);
                break;
            case "hard":
                difficulty = getResources().getString(R.string.difficultyHard);
                break;
        }

        if(!scoreList.isEmpty()){
            if(scoreList.size() >= 1){
                tvTop1.setText(scoreList.get(0));
            }
            if(scoreList.size() >= 2){
                tvTop2.setText(scoreList.get(1));
            }
            if(scoreList.size() >= 3){
                tvTop3.setText(scoreList.get(2));
            }
            if(scoreList.size() >= 4){
                tvTop4.setText(scoreList.get(3));
            }
            if(scoreList.size() >= 5){
                tvTop5.setText(scoreList.get(4));
            }
        }

        tvPoints.setText(""+ points);
        tvHighScore.setText(""+ pointsHC);
        tvChosenGame.setText(getResources().getString(R.string.chosenGame) + chosenGame);
        tvDifficulty.setText(getResources().getString(R.string.difficultyTitle) + ": " + difficulty);
    }

    //set saved language
    private void setLocale(String lang) {
        Locale locale;
        if(lang.equals("")){ //if there's no saved language
            locale = new Locale(Locale.getDefault().getLanguage()); //get default language of the device
        }else{
            locale = new Locale(lang);
        }
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

    public void restart(View view) {
        Settings.btnAnimation(view);

        Intent intent;
        SharedPreferences sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        String actual = sharedPreferences.getString("actualGame","");

        switch (actual){
            case("math"):
                intent = new Intent(this, MathGame.class);
                startActivity(intent);
                break;
            case("NlToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","NlToEn");
                startActivity(intent);
                break;
            case("EnToNl"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToNl");
                startActivity(intent);
                break;
            case("FrToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","FrToEn");
                startActivity(intent);
                break;
            case("EnToFr"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToFr");
                startActivity(intent);
                break;
        }
        finish();
    }

    public void changeGame(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    public void main(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        Settings.btnAnimation(view);

        finish();
    }

    public void goToLogin(View view) {
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        Intent intent = new Intent(GameOver.this, MyAccount.class);
        intent.putExtra("gameover", "gameover");
        intent.putExtra("points", points);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("chosenGame", chosenGame);
        startActivity(intent);
        finish();
    }
}
