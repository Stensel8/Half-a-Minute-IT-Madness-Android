package com.assbinc.secondsGame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Words {
    @SerializedName("easyWords")
    @Expose
    private List<GameEasy> easyWords = new ArrayList<GameEasy>();
    @SerializedName("mediumWords")
    @Expose
    private List<GameMedium> mediumWords = new ArrayList<GameMedium>();
    @SerializedName("HardWords")
    @Expose
    private List<GameHard> hardWords = new ArrayList<GameHard>();

    public List<GameEasy> getEasyWords() {
        return easyWords;
    }

    public List<GameMedium> getMediumWords() {
        return mediumWords;
    }

    public List<GameHard> getHardWords() {
        return hardWords;
    }

    @Override
    public String toString() {
        return "Words{" +
                "easyWords=" + easyWords +
                ", mediumWords=" + mediumWords +
                ", hardWords=" + hardWords +
                '}';
    }
}

class GameEasy{
    @SerializedName("frWord")
    @Expose
    private String frWord;
    @SerializedName("nlWord")
    @Expose
    private String nlWord;
    @SerializedName("enWord")
    @Expose
    private String enWord;

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
        return "GameEasy{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}

class GameMedium{
    @SerializedName("frWord")
    @Expose
    private String frWord;
    @SerializedName("nlWord")
    @Expose
    private String nlWord;
    @SerializedName("enWord")
    @Expose
    private String enWord;

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
        return "GameEasy{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}

class GameHard{
    @SerializedName("frWord")
    @Expose
    private String frWord;
    @SerializedName("nlWord")
    @Expose
    private String nlWord;
    @SerializedName("enWord")
    @Expose
    private String enWord;

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
        return "GameEasy{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}