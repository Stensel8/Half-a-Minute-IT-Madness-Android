package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

public class ChooseLanguageGame extends AppCompatActivity {

    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_language_game);
//        getSupportActionBar().hide();

    }

    //set saved language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //load saved language
    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language);
    }

    public void startLanguageTest(View view){
        Settings.btnAnimation(view);

        Intent intent = new Intent(this, Language_test.class);

        switch(view.getId()){
            case (R.id.btnNlToEn):
                intent.putExtra("chosenGame","NlToEn");
                break;
            case (R.id.btnEnToNl):
                intent.putExtra("chosenGame","EnToNl");
                break;
            case (R.id.btnFrToEn):
                intent.putExtra("chosenGame","FrToEn");
                break;
            case (R.id.btnEnToFr):
                intent.putExtra("chosenGame","EnToFr");
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
