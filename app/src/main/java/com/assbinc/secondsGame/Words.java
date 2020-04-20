package com.assbinc.secondsGame;

public class Words {
    private GameEasy easyWords;
    private GameMedium mediumWords;
    private GameHard hardWords;

    public GameEasy getEasyWords() {
        return easyWords;
    }

    public GameMedium getMediumWords() {
        return mediumWords;
    }

    public GameHard getHardWords() {
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
    private String frWord;
    private String nlWord;
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
    private String frWord;
    private String nlWord;
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
        return "GameMedium{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}

class GameHard{
    private String frWord;
    private String nlWord;
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
        return "GameHard{" +
                "frWord='" + frWord + '\'' +
                ", nlWord='" + nlWord + '\'' +
                ", enWord='" + enWord + '\'' +
                '}';
    }
}