package com.halfminute.itmadness.utils

enum class Difficulty {
    EASY, MEDIUM, HARD;
    
    companion object {
        fun fromString(value: String): Difficulty = try {
            valueOf(value.uppercase())
        } catch (e: Exception) {
            EASY
        }
    }
}
