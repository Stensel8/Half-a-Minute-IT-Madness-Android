package com.assbinc.secondsGame;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private static final String FRIENDS = "FRIENDS";

    public SessionManager(Context context) {
        this.context = context;
        sessionPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sessionPreferences.edit();
    }

    public void createSession(String username, String uid, int profileScore, int friends){
        editor.putBoolean(IS_LOGGED, true); //boolean used to no if logged-in or not
        editor.putString(USERNAME, username); //saves the username
        editor.putString(UID, uid); //saves the Firestore uid
        editor.putInt(PROFILESCORE, profileScore); //saves the profileScore
        editor.putInt(FRIENDS, friends); //saves the number of friends
        editor.apply();
    }

    public String getUsername(){
        return sessionPreferences.getString(USERNAME, "");
    }

    public int getProfileScore(){
        return sessionPreferences.getInt(PROFILESCORE, 0);
    }

    public int getNbFriends(){ return sessionPreferences.getInt(FRIENDS    , 0); }

    public String getUid(){
        return sessionPreferences.getString(UID, "");
    }

    public void setProfileScore(int profileScore, FirebaseFirestore db){
        DocumentReference docUser = db.collection("users").document(getUid());

        docUser.update("profileScore", profileScore);

        editor.putInt(PROFILESCORE, profileScore); //saves the new profileScore
        editor.apply();
    }

    public void setNbFriends(FirebaseFirestore db, TextView totalFriends){
        DocumentReference docUser = db.collection("users").document(getUid());

        db.collection("friends").whereEqualTo("username", getUsername()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                docUser.update("friends", FieldValue.increment(1));
                editor.putInt(FRIENDS, task.getResult().size());
                editor.apply();
                totalFriends.setText(getNbFriends() + "");

            }
        });
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
}
