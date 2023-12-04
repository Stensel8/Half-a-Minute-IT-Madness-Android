package com.halfminute.itmadness;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MyAccount extends AppCompatActivity {

    SharedPref sharedPref;
    int points;
    String difficulty;
    String chosenGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);

        points = Objects.requireNonNull(getIntent().getExtras()).getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!(getIntent().getStringExtra("gameover") == null) && (Objects.requireNonNull(getIntent().getStringExtra("gameover")).equals("gameover"))) {
            Intent intent = new Intent(MyAccount.this, GameOver.class);
            intent.putExtra("gameover", "gameover");
            intent.putExtra("points", points);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("chosenGame", chosenGame);
            startActivity(intent);
            finish();
        }else if(!(getIntent().getStringExtra("main") == null) &&(Objects.requireNonNull(getIntent().getStringExtra("main")).equals("main"))){
            finish();
        }else{
            Intent intent = new Intent(MyAccount.this, Settings.class);
            startActivity(intent);
            finish();
        }
    }
}
