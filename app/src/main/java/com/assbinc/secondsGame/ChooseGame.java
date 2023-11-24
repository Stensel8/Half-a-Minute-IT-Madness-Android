package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.assbinc.secondsgame.R;

public class ChooseGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);
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
