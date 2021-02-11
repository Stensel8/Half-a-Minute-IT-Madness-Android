package com.assbinc.secondsGame;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Suggestions {
    private String username;
    private String uid;
    private String idea;

    Suggestions(){}

    Suggestions(String username, String uid, String idea){
        this.username = username;
        this.uid = uid;
        this.idea = idea;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() { return uid; }

    public String getIdea() { return idea; }
}
