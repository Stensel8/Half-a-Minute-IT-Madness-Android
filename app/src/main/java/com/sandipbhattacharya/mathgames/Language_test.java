package com.sandipbhattacharya.mathgames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class Language_test extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_test);

    }

    public void pauseGame(View view) {
        String chosenGame = getIntent().getStringExtra("chosenGame");
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actualGame", chosenGame);
        editor.commit();
//        countDownTimer.cancel();
        Intent intent = new Intent(Language_test.this, PauseMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
//        countDownTimer.cancel();
        Intent intent = new Intent(Language_test.this, ChooseLanguageGame.class);
        startActivity(intent);
        finish();
    }
}
