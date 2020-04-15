package com.sandipbhattacharya.mathgames;

import android.content.Context;
import android.content.SharedPreferences;

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
}
