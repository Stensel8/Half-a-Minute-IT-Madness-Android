package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.assbinc.secondsgame.R;


public class PauseMenu extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pause_menu);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    public void resumeGame(View view) {
        Settings.btnAnimation(view);

        goBack();
    }

    private void goBack(){
        Intent intent;
        String actual = sharedPreferences.getString("actualGame","");
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

    public void goSettings(View view) {
        Settings.btnAnimation(view);

        SharedPreferences sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("activity", "pause");
        editor.apply();
        Intent intent = new Intent(PauseMenu.this, Settings.class);
        startActivity(intent);
        finish();
    }

    public void goMain(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(PauseMenu.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeGame(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(PauseMenu.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }
}
