package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class ChooseLanguageGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_language_game);
//        getSupportActionBar().hide();

    }

    public void startLanguageTest(View view){
        Intent intent = new Intent(this, Language_test.class);

        switch(view.getId()){
            case (R.id.btnDutch):
                intent.putExtra("chosenGame","dutch");
                break;
            case (R.id.btn1):
                intent.putExtra("chosenGame","EnBeginners");
                break;
            case (R.id.btn0):
                intent.putExtra("chosenGame","EnTravel");
                break;
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, ChooseGame.class);
        startActivity(intent);
        finish();
    }
}
