package com.halfminute.itmadness;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Words {
    @SerializedName("easyWords")
    @Expose
    private final List<GameEasy> easyWords = new ArrayList<>();
    @SerializedName("mediumWords")
    @Expose
    private final List<GameMedium> mediumWords = new ArrayList<>();
    @SerializedName("HardWords")
    @Expose
    private final List<GameHard> hardWords = new ArrayList<>();

    public List<GameEasy> getEasyWords() {
        return easyWords;
    }

    public List<GameMedium> getMediumWords() {
        return mediumWords;
    }

    public List<GameHard> getHardWords() {
        return hardWords;
    }

    @NonNull
    @Override
    public String toString() {
        return "Words{" +
                "easyWords=" + easyWords +
                ", mediumWords=" + mediumWords +
                ", hardWords=" + hardWords +
                '}';
    }
}

record GameEasy(@SerializedName("frWord") @Expose String frWord,
                @SerializedName("nlWord") @Expose String nlWord,
                @SerializedName("enWord") @Expose String enWord) {


    @Override
    public String frWord() {
        return frWord;
    }

    @Override
    public String nlWord() {
        return nlWord;
    }

    @Override
    public String enWord() {
        return enWord;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameEasy{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}

record GameMedium(@SerializedName("frWord") @Expose String frWord,
                  @SerializedName("nlWord") @Expose String nlWord,
                  @SerializedName("enWord") @Expose String enWord) {


    @Override
    public String frWord() {
        return frWord;
    }

    @Override
    public String nlWord() {
        return nlWord;
    }

    @Override
    public String enWord() {
        return enWord;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameMedium{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}

record GameHard(@SerializedName("frWord") @Expose String frWord,
                @SerializedName("nlWord") @Expose String nlWord,
                @SerializedName("enWord") @Expose String enWord) {


    @Override
    public String frWord() {
        return frWord;
    }

    @Override
    public String nlWord() {
        return nlWord;
    }

    @Override
    public String enWord() {
        return enWord;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameHard{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}