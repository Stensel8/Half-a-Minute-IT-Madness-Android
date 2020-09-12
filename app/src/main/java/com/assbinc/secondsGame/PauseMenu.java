package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

public class PauseMenu extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);

        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pause_menu);

        //Ad
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView adView = findViewById(R.id.adViewPause);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    //set saved language
    private void setLocale(String lang) {
        Locale locale;
        if(lang.equals("")){ //if there's no saved language
            locale = new Locale(Locale.getDefault().getLanguage()); //get default language of the device
        }else{
            locale = new Locale(lang);
        }
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    public void resumeGame(View view) {
        Settings.btnAnimation(view);

        goBack();
    }

    private void goBack(){
        Intent intent;
        String actual = sharedPreferences.getString("actualGame","");
        switch (actual){
            case("math"):

                intent = new Intent(this, MathGame.class);
                startActivity(intent);
                break;
            case("NlToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","NlToEn");
                startActivity(intent);
                break;
            case("EnToNl"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToNl");
                startActivity(intent);
                break;
            case("FrToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","FrToEn");
                startActivity(intent);
                break;
            case("EnToFr"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToFr");
                startActivity(intent);
                break;
        }
        finish();
    }

    public void goSettings(View view) {
        Settings.btnAnimation(view);

        SharedPreferences sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("activity", "pause");
        editor.commit();
        Intent intent = new Intent(PauseMenu.this, Settings.class);
        startActivity(intent);
        finish();
    }

    public void goMain(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(PauseMenu.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeGame(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(PauseMenu.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }
}
