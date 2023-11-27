package com.halfminute.itmadness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    SharedPreferences sharedPreferences;
    ImageView ivHighScore;
    TextView tvHighScore, tvChosenGame, tvDifficulty, tvTop2;
    ProgressBar progressBarScore;
    TableLayout tlScore;
    SharedPref sharedPref;
    SessionManager session;
    int points;
    String difficulty, chosenGame;
    CollectionReference collection;
    private MediaPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        session = new SessionManager(this);
        FirebaseFirestore fireDb = FirebaseFirestore.getInstance();

        tlScore = findViewById(R.id.tlScore);
        ivHighScore = findViewById(R.id.ivHighScore);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvTop2 = findViewById(R.id.tvTop2);
        tvPoints = findViewById(R.id.tvPoints);
        tvChosenGame = findViewById(R.id.tvChosenGame);
        tvDifficulty = findViewById(R.id.tvDifficultyGOver);
        progressBarScore = findViewById(R.id.progressBarScore);

        //displays the top 5 if the user is connected
        tlScore.setVisibility(session.checkLoggedIn()? View.VISIBLE: View.INVISIBLE);
        progressBarScore.setVisibility(View.GONE);

        points = Objects.requireNonNull(getIntent().getExtras()).getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        sharedPreferences = getSharedPreferences("pref", 0);
        int pointsHC = 0;
        collection = fireDb.collection("highscore");
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(points > pointsHC){
            //saves the high-score
            pointsHC = points;
            editor.putInt("pointsHC", pointsHC);
            editor.apply();

            applause();
            
            ivHighScore.setVisibility(View.VISIBLE);
        }

        tvPoints.setText(""+ points);
        tvHighScore.setText(""+ pointsHC);
    }

    private void applause() {
        if(sharedPref.getSound()){
            player = MediaPlayer.create(this, R.raw.applause);
            player.start();
            player.setOnCompletionListener(mp -> stopPlayer());
        }
    }

    private void stopPlayer() {
        if(player != null){
            player.release();
            player = null;
        }
    }


    public void restart(View view) {
        Settings.btnAnimation(view);

        Intent intent;
        SharedPreferences sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        String actual = sharedPreferences.getString("actualGame","");
        stopPlayer();

        switch (actual) {
            case ("math") -> {
                intent = new Intent(this, MathGame.class);
                startActivity(intent);
            }
            case ("NlToEn") -> {
                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame", "NlToEn");
                startActivity(intent);
            }
            case ("EnToNl") -> {
                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame", "EnToNl");
                startActivity(intent);
            }
            case ("FrToEn") -> {
                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame", "FrToEn");
                startActivity(intent);
            }
            case ("EnToFr") -> {
                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame", "EnToFr");
                startActivity(intent);
            }
        }
        finish();
    }

    public void changeGame(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, ChooseGame.class);
        stopPlayer();
        startActivity(intent);
        finish();
    }

    public void main(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, MainActivity.class);
        stopPlayer();
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        Settings.btnAnimation(view);

        finish();
    }

}
