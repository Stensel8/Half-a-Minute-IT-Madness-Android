package com.halfminute.itmadness

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GameEasy(
    @SerializedName("frWord") @Expose val frWord: String,
    @SerializedName("nlWord") @Expose val nlWord: String,
    @SerializedName("enWord") @Expose val enWord: String,
    @SerializedName("deWord") @Expose val deWord: String
) {
    override fun toString(): String {
        return "com.halfminute.itmadness.GameEasy(frWord='$frWord', nlWord='$nlWord', enWord='$enWord', deWord='$deWord')"
    }

    // Getter methods
    fun getEasyFrWord(): String {
        return frWord
    }

    fun getEasyNlWord(): String {
        return nlWord
    }

    fun getEasyEnWord(): String {
        return enWord
    }

    fun getEasyDeWord(): String {
        return deWord
    }
}

data class GameMedium(
    @SerializedName("frWord") @Expose val frWord: String,
    @SerializedName("nlWord") @Expose val nlWord: String,
    @SerializedName("enWord") @Expose val enWord: String,
    @SerializedName("deWord") @Expose val deWord: String
) {
    override fun toString(): String {
        return "com.halfminute.itmadness.GameMedium(frWord='$frWord', nlWord='$nlWord', enWord='$enWord', deWord='$deWord')"
    }

    // Getter methods
    fun getMediumFrWord(): String {
        return frWord
    }

    fun getMediumNlWord(): String {
        return nlWord
    }

    fun getMediumEnWord(): String {
        return enWord
    }

    fun getMediumDeWord(): String {
        return deWord
    }
}

data class GameHard(
    @SerializedName("frWord") @Expose val frWord: String,
    @SerializedName("nlWord") @Expose val nlWord: String,
    @SerializedName("enWord") @Expose val enWord: String,
    @SerializedName("deWord") @Expose val deWord: String
) {
    override fun toString(): String {
        return "com.halfminute.itmadness.GameHard(frWord='$frWord', nlWord='$nlWord', enWord='$enWord', deWord='$deWord')"
    }

    // Getter methods
    fun getHardFrWord(): String {
        return frWord
    }

    fun getHardNlWord(): String {
        return nlWord
    }

    fun getHardEnWord(): String {
        return enWord
    }

    fun getHardDeWord(): String {
        return deWord
    }
}

data class Words(
    @SerializedName("easyWords") @Expose val easyWords: List<GameEasy>,
    @SerializedName("mediumWords") @Expose val mediumWords: List<GameMedium>,
    @SerializedName("hardWords") @Expose val hardWords: List<GameHard>
) {
    override fun toString(): String {
        return "com.halfminute.itmadness.Words(easyWords=$easyWords, mediumWords=$mediumWords, hardWords=$hardWords)"
    }
}
