package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
            case("dutch"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","dutch");
                startActivity(intent);
                finish();
                break;
            case("EnBeginners"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","EnBeginners");
                startActivity(intent);
                finish();
                break;
            case("EnTravel"):

                intent = new Intent(this, Language_test.class);
                intent.putExtra("chosenGame","EnTravel");
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
}
