package com.halfminute.itmadness;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "session_preferences";
    private static final String HIGH_SCORE_KEY = "pointsHC";

    private final SharedPreferences sessionPreferences;
    private final SharedPreferences.Editor editor;
    private final Context context;
    private final int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        sessionPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sessionPreferences.edit();
    }

    public int getHighScore() {
        return sessionPreferences.getInt(HIGH_SCORE_KEY, 0);
    }

    public void saveHighScore(int newScore) {
        int currentHighScore = getHighScore();
        if (newScore > currentHighScore) {
            editor.putInt(HIGH_SCORE_KEY, newScore);
            editor.apply();
        }
    }
}
