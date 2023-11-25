package com.halfminute.itmadness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.halfminute.itmadness.R;

public class ChooseLanguageGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_language_game);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChooseLanguageGame.this, ChooseGame.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void startLanguageTest(View view){
        Settings.btnAnimation(view);

        Intent intent = new Intent(this, LanguageGame.class);

        switch (view.getId()) {
            case (R.id.btnNlToEn) -> intent.putExtra("chosenGame", "NlToEn");
            case (R.id.btnEnToNl) -> intent.putExtra("chosenGame", "EnToNl");
            case (R.id.btnFrToEn) -> intent.putExtra("chosenGame", "FrToEn");
            case (R.id.btnEnToFr) -> intent.putExtra("chosenGame", "EnToFr");
        }
        startActivity(intent);
        finish();
    }
}
