package com.halfminute.itmadness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseLanguageGame extends AppCompatActivity {

    private static final String TAG = "ChooseLanguageGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Checking if dark mode is enabled
        SharedPref sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this); // loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_language_game);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back button pressed. Navigating to ChooseGame");
                Intent intent = new Intent(ChooseLanguageGame.this, ChooseGame.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void startLanguageTest(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(this, LanguageGame.class);

        int viewId = view.getId();
        String chosenGame = "";

        if (viewId == R.id.btnNlToEn) {
            chosenGame = "NlToEn";
        } else if (viewId == R.id.btnEnToNl) {
            chosenGame = "EnToNl";
        } else if (viewId == R.id.btnDeToEn) {
            chosenGame = "DeToEn";
        } else if (viewId == R.id.btnEnToDe) {
            chosenGame = "EnToDe";
        } else if (viewId == R.id.btnFrToEn) {
            chosenGame = "FrToEn";
        } else if (viewId == R.id.btnEnToFr) {
            chosenGame = "EnToFr";
        } else {
            Log.w(TAG, "Unknown button clicked with ID: " + viewId);
            // Handle the default case if needed
        }

        Log.d(TAG, "Starting language test: " + chosenGame);
        intent.putExtra("chosenGame", chosenGame);
        startActivity(intent);
        finish();
    }
}
