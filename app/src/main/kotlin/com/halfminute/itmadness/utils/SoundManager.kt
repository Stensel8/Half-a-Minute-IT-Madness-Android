package com.halfminute.itmadness.utils

import android.content.Context
import android.media.MediaPlayer
import com.halfminute.itmadness.R

object SoundManager {
    private var correctSound: MediaPlayer? = null
    private var incorrectSound: MediaPlayer? = null
    private var applauseSound: MediaPlayer? = null
    private var countdownSound: MediaPlayer? = null
    private var isSoundEnabled = true
    private var isCountdownPlaying = false
    
    fun initialize(context: Context) {
        release()
        correctSound = MediaPlayer.create(context, R.raw.correct_sound)
        incorrectSound = MediaPlayer.create(context, R.raw.incorrect_sound)
        applauseSound = MediaPlayer.create(context, R.raw.applause)
        countdownSound = MediaPlayer.create(context, R.raw.five_sec_countdown)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }
    
    fun playCorrect() {
        if (isSoundEnabled) {
            correctSound?.let {
                if (it.isPlaying) it.seekTo(0)
                it.start()
            }
        }
    }
    
    fun playIncorrect() {
        if (isSoundEnabled) {
            incorrectSound?.let {
                if (it.isPlaying) it.seekTo(0)
                it.start()
            }
        }
    }
    
    fun playApplause() {
        if (isSoundEnabled) {
            applauseSound?.start()
        }
    }
    
    fun playCountdown() {
        if (isSoundEnabled && !isCountdownPlaying) {
            isCountdownPlaying = true
            countdownSound?.let {
                if (it.isPlaying) it.seekTo(0)
                it.setOnCompletionListener {
                    isCountdownPlaying = false
                }
                it.start()
            }
        }
    }
    
    fun stopCountdown() {
        countdownSound?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
        isCountdownPlaying = false
    }
    
    fun release() {
        correctSound?.release()
        incorrectSound?.release()
        applauseSound?.release()
        countdownSound?.release()
        correctSound = null
        incorrectSound = null
        applauseSound = null
        countdownSound = null
        isCountdownPlaying = false
    }
}
