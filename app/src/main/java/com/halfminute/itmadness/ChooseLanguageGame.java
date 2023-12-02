package com.halfminute.itmadness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseLanguageGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Checking if dark mode is enabled
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

        int viewId = view.getId();
        if (viewId == R.id.btnNlToEn) {
            intent.putExtra("chosenGame", "NlToEn");
        } else if (viewId == R.id.btnEnToNl) {
            intent.putExtra("chosenGame", "EnToNl");
        } else if (viewId == R.id.btnFrToEn) {
            intent.putExtra("chosenGame", "FrToEn");
        } else if (viewId == R.id.btnEnToFr) {
            intent.putExtra("chosenGame", "EnToFr");
        } else {
            // Handle the default case if needed
        }

        startActivity(intent);
        finish();
    }

}
