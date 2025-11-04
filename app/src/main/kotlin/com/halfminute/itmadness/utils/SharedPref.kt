package com.halfminute.itmadness.utils

import android.content.Context
import android.content.SharedPreferences
import com.halfminute.itmadness.ui.screens.LeaderboardEntry

class SharedPref(context: Context) {

    private val mySharedPref: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)

    fun saveLanguage(languageCode: String) {
        mySharedPref.edit().putString("language", languageCode).apply()
    }

    fun getLanguage(): String {
        return mySharedPref.getString("language", "System") ?: "System"
    }

    fun setSound(state: Boolean) {
        mySharedPref.edit().putBoolean("sound", state).apply()
    }

    fun getSound(): Boolean {
        return mySharedPref.getBoolean("sound", true)
    }

    // Language handling is now done automatically by the system via android:localeConfig
    // No need for manual locale switching - removed deprecated updateConfiguration() usage

    fun saveDifficulty(difficulty: String) {
        mySharedPref.edit().putString("difficulty", difficulty).apply()
    }

    fun loadDifficulty(): String {
        return mySharedPref.getString("difficulty", "easy") ?: "easy"
    }

    fun saveChosenGame(chosenGame: String) {
        mySharedPref.edit().putString("chosenGame", chosenGame).apply()
    }

    fun loadChosenGame(): String {
        return mySharedPref.getString("chosenGame", "") ?: ""
    }

    fun getHighScore(): Int {
        return mySharedPref.getInt("highScore", 0)
    }

    fun saveHighScore(newScore: Int) {
        val currentHighScore = getHighScore()
        if (newScore > currentHighScore) {
            mySharedPref.edit().putInt("highScore", newScore).apply()
        }
    }

    fun isFirstStart(): Boolean {
        return mySharedPref.getBoolean("firstStart", true)
    }

    fun setFirstStartShown() {
        mySharedPref.edit().putBoolean("firstStart", false).apply()
    }

    private val lastThrownDiceValueKey = "last_thrown_dice_value"
    private val diceRollTimestampKey = "dice_roll_timestamp"

    fun saveLastThrownDiceValue(value: Int) {
        mySharedPref.edit().putInt(lastThrownDiceValueKey, value).apply()
    }

    fun loadLastThrownDiceValue(): Int {
        return mySharedPref.getInt(lastThrownDiceValueKey, 0)
    }

    fun saveDiceRollTimestamp(timestamp: Long) {
        mySharedPref.edit().putLong(diceRollTimestampKey, timestamp).apply()
    }

    fun loadDiceRollTimestamp(): Long {
        return mySharedPref.getLong(diceRollTimestampKey, 0)
    }

    fun clearDiceRollTimestamp() {
        mySharedPref.edit().remove(diceRollTimestampKey).apply()
    }

    // Username for leaderboard
    fun saveUsername(username: String) {
        mySharedPref.edit().putString("username", username).apply()
    }

    fun getUsername(): String {
        return mySharedPref.getString("username", "") ?: ""
    }

    fun hasUsername(): Boolean {
        return getUsername().isNotEmpty()
    }
    
    // ========== SIMPLE LEADERBOARD SYSTEM ==========
    // Beginner-friendly: Stores leaderboard as simple JSON string
    // No complex database needed for a simple game!
    
    private val leaderboardKey = "leaderboard_data"
    
    /**
     * Save a score to the leaderboard.
     * Simple and synchronous - perfect for beginners!
     */
    fun saveScore(username: String, score: Int, gameType: String, difficulty: String) {
        val entries = getLeaderboardEntries().toMutableList()
        
        // Add new entry
        entries.add(
            LeaderboardEntry(
                username = username,
                score = score,
                gameType = gameType.lowercase(),
                difficulty = difficulty.uppercase(),
                timestamp = System.currentTimeMillis()
            )
        )
        
        // Keep only top 100 entries to avoid unbounded growth
        val sortedEntries = entries.sortedByDescending { it.score }.take(100)
        
        // Save as simple JSON
        val json = entriesToJson(sortedEntries)
        mySharedPref.edit().putString(leaderboardKey, json).apply()
    }
    
    /**
     * Get all leaderboard entries
     */
    fun getLeaderboardEntries(): List<LeaderboardEntry> {
        val json = mySharedPref.getString(leaderboardKey, "[]") ?: "[]"
        return jsonToEntries(json)
    }
    
    /**
     * Get top scores for a specific game and difficulty
     */
    fun getTopScores(gameType: String, difficulty: String, limit: Int = 10): List<LeaderboardEntry> {
        return getLeaderboardEntries()
            .filter { it.gameType.equals(gameType, ignoreCase = true) && 
                     it.difficulty.equals(difficulty, ignoreCase = true) }
            .sortedByDescending { it.score }
            .take(limit)
    }
    
    /**
     * Get personal best for a specific game and difficulty
     */
    fun getPersonalBest(username: String, gameType: String, difficulty: String): Int {
        return getLeaderboardEntries()
            .filter { it.username == username && 
                     it.gameType.equals(gameType, ignoreCase = true) && 
                     it.difficulty.equals(difficulty, ignoreCase = true) }
            .maxOfOrNull { it.score } ?: 0
    }
    
    /**
     * Get all scores for a specific game
     */
    fun getAllScoresForGame(gameType: String, limit: Int = 50): List<LeaderboardEntry> {
        return getLeaderboardEntries()
            .filter { it.gameType.equals(gameType, ignoreCase = true) }
            .sortedByDescending { it.score }
            .take(limit)
    }
    
    /**
     * Get overall top scores across all games
     */
    fun getOverallTopScores(limit: Int = 20): List<LeaderboardEntry> {
        return getLeaderboardEntries()
            .sortedByDescending { it.score }
            .take(limit)
    }
    
    /**
     * Clear all leaderboard data
     */
    fun clearLeaderboard() {
        mySharedPref.edit().remove(leaderboardKey).apply()
    }
    
    /**
     * Get total number of entries
     */
    fun getLeaderboardEntryCount(): Int {
        return getLeaderboardEntries().size
    }
    
    // Simple JSON serialization - beginner-friendly, no GSON needed!
    private fun entriesToJson(entries: List<LeaderboardEntry>): String {
        return entries.joinToString(separator = ",", prefix = "[", postfix = "]") { entry ->
            """{"username":"${entry.username}","score":${entry.score},"gameType":"${entry.gameType}","difficulty":"${entry.difficulty}","timestamp":${entry.timestamp}}"""
        }
    }
    
    private fun jsonToEntries(json: String): List<LeaderboardEntry> {
        if (json == "[]" || json.isBlank()) return emptyList()
        
        return try {
            // Simple manual JSON parsing - no library needed!
            val entries = mutableListOf<LeaderboardEntry>()
            val items = json.trim().removeSurrounding("[", "]").split("},")
            
            items.forEach { item ->
                val cleaned = item.trim().removeSuffix("}").removePrefix("{")
                val fields = cleaned.split(",").associate {
                    val (key, value) = it.split(":")
                    key.trim().removeSurrounding("\"") to value.trim().removeSurrounding("\"")
                }
                
                entries.add(
                    LeaderboardEntry(
                        username = fields["username"] ?: "",
                        score = fields["score"]?.toIntOrNull() ?: 0,
                        gameType = fields["gameType"] ?: "",
                        difficulty = fields["difficulty"] ?: "",
                        timestamp = fields["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
                    )
                )
            }
            entries
        } catch (e: Exception) {
            // If parsing fails, return empty list
            emptyList()
        }
    }
}


