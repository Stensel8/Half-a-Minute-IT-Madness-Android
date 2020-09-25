package com.assbinc.secondsGame;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Highscore {
    private String username;
    private String difficulty;
    private String chosenGame;
    private int score;
    private String docId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getChosenGame() {
        return chosenGame;
    }

    public void setChosenGame(String chosenGame) {
        this.chosenGame = chosenGame;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDocId() {
        return docId;
    }

    public Highscore() {
    }

    public Highscore(String username, String difficulty, String chosenGame, int score, String docId) {
        this.username = username;
        this.difficulty = difficulty;
        this.chosenGame = chosenGame;
        this.score = score;
        this.docId = docId;
    }
}


