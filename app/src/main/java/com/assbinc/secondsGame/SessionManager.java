package com.assbinc.secondsGame;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences sessionPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "LOGIN";
    private static final String IS_LOGGED = "isLogged";
    private static final String USERNAME = "username";
    private static final String UID = "uid";
    private static final String PROFILESCORE = "profilescore";

    public SessionManager(Context context) {
        this.context = context;
        sessionPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sessionPreferences.edit();
    }

    public void createSession(String username, String uid, String profileScore){
        editor.putBoolean(IS_LOGGED, true); //boolean used to no if logged-in or not
        editor.putString(USERNAME, username); //saves the username
        editor.putString(UID, uid); //saves the Firestore uid
        editor.putString(PROFILESCORE, profileScore); //saves the profileScore
        editor.apply();
    }

    public String getUsername(){
        return sessionPreferences.getString(USERNAME, "");
    }

    public String getProfileScore(){
        return sessionPreferences.getString(PROFILESCORE, "");
    }


    public boolean isLoggedIn(){
        return sessionPreferences.getBoolean(IS_LOGGED, false);
    }

    //check if logged-in
    public boolean checkLoggedIn(){
        if(this.isLoggedIn())
            return true;
        else
            return false;
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }

    //TODO: put db here data to avoid fetching data from firestore
}
