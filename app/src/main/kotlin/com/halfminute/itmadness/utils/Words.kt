package com.halfminute.itmadness.utils

import com.halfminute.itmadness.R

import android.content.Context
import org.json.JSONObject

data class Word(
    val frWord: String,
    val nlWord: String,
    val enWord: String,
    val deWord: String
)

data class Words(
    val easyWords: List<Word>,
    val mediumWords: List<Word>,
    val hardWords: List<Word>
) {
    fun getWordsByDifficulty(difficulty: Difficulty): List<Word> {
        return when (difficulty) {
            Difficulty.EASY -> easyWords
            Difficulty.MEDIUM -> mediumWords
            Difficulty.HARD -> hardWords
        }
    }

    companion object {
        fun getInstance(context: Context): Words? {
            return try {
                val inputStream = context.resources.openRawResource(R.raw.wordjsondata)
                val jsonContent = inputStream.bufferedReader().use { it.readText() }
                parseWords(jsonContent)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        private fun parseWords(json: String): Words {
            val root = JSONObject(json)
            return Words(
                easyWords = parseWordArray(root.getJSONArray("easyWords")),
                mediumWords = parseWordArray(root.getJSONArray("mediumWords")),
                hardWords = parseWordArray(root.getJSONArray("hardWords"))
            )
        }
        
        private fun parseWordArray(array: org.json.JSONArray): List<Word> {
            return (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Word(
                    frWord = obj.optString("frWord", ""),
                    nlWord = obj.optString("nlWord", ""),
                    enWord = obj.optString("enWord", ""),
                    deWord = obj.optString("deWord", "")
                )
            }
        }
    }
}

data class LanguageWord(
    val frWord: String,
    val nlWord: String,
    val enWord: String,
    val deWord: String
)

data class LanguageWords(
    val easyWords: List<LanguageWord>,
    val mediumWords: List<LanguageWord>,
    val hardWords: List<LanguageWord>
) {
    fun getWordsByDifficulty(difficulty: Difficulty): List<LanguageWord> {
        return when (difficulty) {
            Difficulty.EASY -> easyWords
            Difficulty.MEDIUM -> mediumWords
            Difficulty.HARD -> hardWords
        }
    }

    companion object {
        fun getInstance(context: Context): LanguageWords? {
            return try {
                val inputStream = context.resources.openRawResource(R.raw.language_wordjsondata)
                val jsonContent = inputStream.bufferedReader().use { it.readText() }
                parseLanguageWords(jsonContent)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        private fun parseLanguageWords(json: String): LanguageWords {
            val root = JSONObject(json)
            return LanguageWords(
                easyWords = parseLanguageWordArray(root.getJSONArray("easyWords")),
                mediumWords = parseLanguageWordArray(root.getJSONArray("mediumWords")),
                hardWords = parseLanguageWordArray(root.getJSONArray("hardWords"))
            )
        }
        
        private fun parseLanguageWordArray(array: org.json.JSONArray): List<LanguageWord> {
            return (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                LanguageWord(
                    frWord = obj.optString("frWord", ""),
                    nlWord = obj.optString("nlWord", ""),
                    enWord = obj.optString("enWord", ""),
                    deWord = obj.optString("deWord", "")
                )
            }
        }
    }
}
