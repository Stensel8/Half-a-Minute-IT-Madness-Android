package com.assbinc.secondsGame;

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
        editor.commit();
    }

    //load the dark mode state
    public Boolean loadNightMode(){
        Boolean state = mySharedPref.getBoolean("darkMode", false);
        return state;
    }

    //set saved language
    public void setLocale(String lang, Context context) {
        java.util.Locale locale;
        if(lang.equals("")){ //if there's no saved language
            locale = new java.util.Locale(java.util.Locale.getDefault().getLanguage()); //get default language of the device
        }else{
            locale = new java.util.Locale(lang);
        }
        java.util.Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    //load saved language
    public void loadLocale(Context context){
        SharedPreferences pref = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language, context);
    }
}
