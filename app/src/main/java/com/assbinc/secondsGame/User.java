package com.assbinc.secondsGame;

import com.google.firebase.firestore.IgnoreExtraProperties;

import androidx.annotation.NonNull;

@IgnoreExtraProperties
public class User {
    private String username;
    private String email;
    private String password;
    private String uid;
    private int profileScore;
    private int friends;

    public User() {

    }

    public User(String username, String email, String password, int profileScore, int nbFriends, String uid) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileScore = profileScore;
        this.profileScore = nbFriends;
        this.uid = uid;
    }

    public String getUsername(){
        return this.username;
    }

    public String getEmail(){
        return this.email;
    }

    public int getProfileScore(){
        return this.profileScore;
    }

    public int getFriends() {
        return friends;
    }

    public String getUid() { return uid; }

    @NonNull
    @Override
    public String toString() {
        return "Username: " + username + " | email: " + email + " | profileScore: " + profileScore + " | password: " + password;
    }
}
