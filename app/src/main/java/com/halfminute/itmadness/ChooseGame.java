package com.halfminute.itmadness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseGame extends AppCompatActivity {

    SharedPref sharedPref; // Declare a SharedPref object to access the shared preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Checking if dark mode is enabled
        sharedPref = new SharedPref(this); // Initialize the SharedPref object with the current context
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme); // Set the theme according to the shared preference value
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game); // Set the layout for this activity

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChooseGame.this, MainActivity.class); // Create an intent to go back to the main activity
                startActivity(intent); // Start the main activity
                finish(); // Finish this activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback); // Add the callback to handle the back button press
    }

    public void startMathGame(View view) {
        Settings.btnAnimation(view); // Apply the button animation to the view
        Intent intent = new Intent(ChooseGame.this, MathGame.class); // Create an intent to start the math game activity
        startActivity(intent); // Start the math game activity
        finish(); // Finish this activity
    }

    public void startLanguageGame(View view) {
        Settings.btnAnimation(view); // Apply the button animation to the view
        Intent intent = new Intent(ChooseGame.this, ChooseLanguageGame.class); // Create an intent to start the choose language game activity
        startActivity(intent); // Start the choose language game activity
        finish(); // Finish this activity
    }
}
