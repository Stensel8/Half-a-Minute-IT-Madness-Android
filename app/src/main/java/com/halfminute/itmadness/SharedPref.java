package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class SharedPref {

    SharedPreferences mySharedPref;

    public SharedPref(Context context){

        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }

    //save the dark mode state
    public void setNightMode(Boolean state){

        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("darkMode",state);
        editor.apply();
    }

    //load the dark mode state
    public Boolean loadNightMode(){
        return mySharedPref.getBoolean("darkMode", false);
    }

    //save the sound state
    public void setSound(Boolean state){

        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("sound",state);
        editor.apply();
    }

    //get the sound preference
    public Boolean getSound(){
        return mySharedPref.getBoolean("sound", true);
    }

    //set saved language
//set saved language
    public void setLocale(String lang, Context context) {
        java.util.Locale locale = new java.util.Locale(lang);

        java.util.Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        // Use the same key "language" for saving the language preference
        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

    //load saved language
    public void loadLocale(Context context){
        SharedPreferences pref = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("language", java.util.Locale.getDefault().getLanguage());
        setLocale(language, context);
    }
}
