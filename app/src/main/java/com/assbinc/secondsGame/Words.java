package com.assbinc.secondsGame;

import androidx.annotation.NonNull;

public class Words {

    private String frWord;
    private String nlWord;
    private String enWord;

    public Words(String frWord, String nlWord, String enWord) {
        this.frWord = frWord;
        this.nlWord = nlWord;
        this.enWord = enWord;
    }

    public String getFrWord() {
        return frWord;
    }

    public String getNlWord() {
        return nlWord;
    }

    public String getEnWord() {
        return enWord;
    }

    @Override
    public String toString() {
        return "Words{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}
