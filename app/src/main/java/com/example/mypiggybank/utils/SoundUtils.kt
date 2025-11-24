package com.example.mypiggybank.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Vibrator
import android.os.VibrationEffect
import android.os.Build

object SoundUtils {
    fun playNotificationSound(context: Context) {
        try {
            // Get default notification sound
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mediaPlayer = MediaPlayer.create(context, notification)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()

            // Add a small vibration
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 