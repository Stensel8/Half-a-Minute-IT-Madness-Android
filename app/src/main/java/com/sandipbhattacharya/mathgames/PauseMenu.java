package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PauseMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pause_menu);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(PauseMenu.this, MathGame.class);
        startActivity(intent);
        finish();
    }

    public void resumeGame(View view) {

        Intent intent = new Intent(PauseMenu.this, MathGame.class);
        startActivity(intent);
        finish();
    }

    public void goSettings(View view) {

        Intent intent = new Intent(PauseMenu.this, Settings.class);
        intent.putExtra("activity","pause");
        startActivity(intent);
        finish();
    }

    public void goMain(View view) {

        Intent intent = new Intent(PauseMenu.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
