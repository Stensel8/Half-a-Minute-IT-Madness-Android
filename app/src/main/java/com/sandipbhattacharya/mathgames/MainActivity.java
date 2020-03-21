package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, StartGame.class);
        startActivity(intent);
        finish();
    }
}
