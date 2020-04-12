package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.isDarkMode? R.style.darkTheme: R.style.lightTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
    }

    public void chooseGamePage(View view) {
        Intent intent = new Intent(MainActivity.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    public void goSettings(View view) {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        intent.putExtra("activity","main");
        startActivity(intent);
        finish();
    }

}
