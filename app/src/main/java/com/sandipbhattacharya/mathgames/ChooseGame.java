package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

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
        Intent intent = new Intent(ChooseGame.this, ChooseLanguageGame.class);
        startActivity(intent);
        finish();
    }


}
