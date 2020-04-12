package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.isDarkMode? R.style.darkTheme: R.style.lightTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);
//        getSupportActionBar().hide();
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
        Intent intent = new Intent(ChooseGame.this, MathGame.class);
        startActivity(intent);
        finish();
    }

    public void startLanguageGame(View view) {
        Intent intent = new Intent(ChooseGame.this, LanguageGame.class);
        startActivity(intent);
        finish();
    }


}
