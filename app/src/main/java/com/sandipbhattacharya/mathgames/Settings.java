package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(getIntent().getStringExtra("activity").equalsIgnoreCase("main")){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(getIntent().getStringExtra("activity").equalsIgnoreCase("pause")){
            Intent intent = new Intent(Settings.this, PauseMenu.class);
            startActivity(intent);
            finish();
        }

    }

}
