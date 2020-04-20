package com.assbinc.secondsGame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    SharedPreferences sharedPreferences;
    ImageView ivHighScore;
    TextView tvHighScore;
    SharedPref sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ivHighScore = findViewById(R.id.ivHighScore);
        tvHighScore = findViewById(R.id.tvHighScore);
//        getSupportActionBar().hide();
        int points = getIntent().getExtras().getInt("points");
        tvPoints = findViewById(R.id.tvPoints);

        sharedPreferences = getSharedPreferences("pref", 0);
        int pointsSP = sharedPreferences.getInt("pointsSP", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(points > pointsSP){
            pointsSP = points;
            editor.putInt("pointsSP", pointsSP);
            editor.commit();
            ivHighScore.setVisibility(View.VISIBLE);
        }
        tvPoints.setText(""+ points);
        tvHighScore.setText(""+ pointsSP);
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

    public void restart(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, MathGame.class);
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
}
