package com.sandipbhattacharya.mathgames;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    public static boolean isDarkMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Switch darkmodeToggle;

        //set dark theme that we configured
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darkTheme);
        }else{
            setTheme(R.style.lightTheme);
        }

        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.settings);


        //switch between dark and light mode
        darkmodeToggle = findViewById(R.id.darkModeToggle);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkmodeToggle.setChecked(true);
        }
        darkmodeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    isDarkMode = true;
                    recreate();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    isDarkMode = false;
                    recreate();
                }
            }
        });

        //change language
        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog(){
        //Array of languages to display in alert dialog
        final String[] listOfLang = {"Français", "Nederlands", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
        mBuilder.setSingleChoiceItems(listOfLang, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0){
                    //set french
                    setLocale("fr");
                    recreate();
                }
                if (i == 1){
                    //set french
                    setLocale("nl");
                    recreate();
                }
                if (i == 2){
                    //set french
                    setLocale("en");
                    recreate();
                }
                //dismiss dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //change language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My lang", lang);
        editor.apply();
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

        if(getIntent().getStringExtra("activity").equalsIgnoreCase("main")){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(getIntent().getStringExtra("activity").equalsIgnoreCase("pause")){
            Intent intent = new Intent(Settings.this, PauseMenu.class);
            startActivity(intent);
            finish();
        }

    }

}
