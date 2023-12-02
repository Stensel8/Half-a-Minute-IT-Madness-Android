package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Switch darkModeToggle;
        Switch notificationToggle;
        Switch soundToggle;

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        //load selected language
        sharedPref.loadLocale(this);
        FirebaseFirestore fireDb = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        //switch between dark and light mode
        darkModeToggle = findViewById(R.id.darkModeToggle);

        if(sharedPref.loadNightMode()){
            darkModeToggle.setChecked(true);
        }
        darkModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                sharedPref.setNightMode(true);
                recreate();
            }else{
                sharedPref.setNightMode(false);
                recreate();
            }
        });

        //sound toggle
        soundToggle = findViewById(R.id.soundToggle);

        if(sharedPref.getSound()){
            soundToggle.setChecked(true);
        }
        soundToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                sharedPref.setSound(true);
                recreate();
            }else{
                sharedPref.setSound(false);
                recreate();
            }
        });

        //change language
        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(v -> {
            btnAnimation(v);
            showChangeLanguageDialog(v);
        });
    }

    public void goMyAccount(View view){

        btnAnimation(view);
        Intent intent = new Intent(Settings.this, MyAccount.class);
        intent.putExtra("gameover", "settings");
        startActivity(intent);
        finish();
    }



    public void showChangeLanguageDialog(View view){
        //Array of languages to display in alert dialog
        final String[] listOfLang = {"Français", "Nederlands", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this,sharedPref.loadNightMode()? androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog: androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        mBuilder.setSingleChoiceItems(listOfLang, -1, (dialog, i) -> {
            if (i == 0){
                //set french
                sharedPref.setLocale("fr", getApplicationContext());
                recreate();
            }
            if (i == 1){
                //set dutch
                sharedPref.setLocale("nl", getApplicationContext());
                recreate();
            }
            if (i == 2){
                //set english
                sharedPref.setLocale("en", getApplicationContext());
                recreate();
            }
            //dismiss dialog when language selected
            dialog.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void chooseDifficulty(View view){
        btnAnimation(view);

        SharedPreferences.Editor editor = getSharedPreferences("gameDifficulty", MODE_PRIVATE).edit();
        String difficulty = "";

        int viewId = view.getId();
        if (viewId == R.id.difficultyEasyBtn) {
            //save data to shared preferences
            editor.putString("difficulty", "easy");
            editor.apply();
            difficulty = getResources().getString(R.string.difficultyEasy);
        } else if (viewId == R.id.difficultyMediumBtn) {
            editor.putString("difficulty", "medium");
            editor.apply();
            difficulty = getResources().getString(R.string.difficultyMedium);
        } else if (viewId == R.id.difficultyHardBtn) {
            editor.putString("difficulty", "hard");
            editor.apply();
            difficulty = getResources().getString(R.string.difficultyHard);
        }

        Toast.makeText(Settings.this, getResources().getString(R.string.changeDfficultyTo) + difficulty ,Toast.LENGTH_SHORT).show();
    }


    public void suggestionBox(View view){
        btnAnimation(view);

    }

    public static void btnAnimation(View view){
        //little animation when button is clicked
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        SharedPreferences sharedPreferences = getSharedPreferences("activity", Activity.MODE_PRIVATE);
        String actual = sharedPreferences.getString("activity","");

        if(actual.equalsIgnoreCase("main")){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
        }else if(actual.equalsIgnoreCase("pause")){
            Intent intent = new Intent(Settings.this, PauseMenu.class);
            startActivity(intent);
        }
        finish();
    }

}
