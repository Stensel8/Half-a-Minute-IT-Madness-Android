package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class PauseMenu extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
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

        goBack();
    }

    private void goBack(){
        Intent intent;
        String actual = sharedPreferences.getString("actualGame","");
        switch (actual){
            case("math"):

                intent = new Intent(this, MathGame.class);
                startActivity(intent);
                finish();
                break;
            case("NlToEn"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","NlToEn");
                startActivity(intent);
                finish();
                break;
            case("EnToNl"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","EnToNl");
                startActivity(intent);
                finish();
                break;
            case("FrToEn"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","FrToEn");
                startActivity(intent);
                finish();
                break;
            case("EnToFr"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","EnToFr");
                startActivity(intent);
                finish();
                break;
        }
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

    public void changeGame(View view) {
        Intent intent = new Intent(PauseMenu.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }
}
