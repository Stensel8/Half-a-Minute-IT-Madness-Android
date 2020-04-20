package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

public class ChooseGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(ChooseGame.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startMathGame(View view) {
        Settings.btnAnimation(view);
        Intent intent = new Intent(ChooseGame.this, MathGame.class);
        startActivity(intent);
        finish();
    }

    public void startLanguageGame(View view) {
        Settings.btnAnimation(view);
        Intent intent = new Intent(ChooseGame.this, ChooseLanguageGame.class);
        startActivity(intent);
        finish();
    }


}
