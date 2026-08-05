package com.example.ui.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HapticEffects {

    /**
     * Triggered when completing a level set or major milestone.
     * Rhythmic multi-stage burst pattern:
     * Stage 1: Impact tick (80ms)
     * Stage 2: Secondary surge (120ms)
     * Stage 3: Grand finale celebration rumble (250ms)
     */
    fun triggerMilestoneBurst(context: Context, hapticFeedback: HapticFeedback?, isVibrationEnabled: Boolean = true) {
        if (!isVibrationEnabled) return
        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 80, 60, 120, 80, 250)
                    val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 80, 60, 120, 80, 250), -1)
                }
            } else {
                hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        } catch (e: Exception) {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    /**
     * Triggered when a star pops or reward counter steps up.
     */
    fun triggerStarDropPop(context: Context, hapticFeedback: HapticFeedback?, isVibrationEnabled: Boolean = true) {
        if (!isVibrationEnabled) return
        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(45)
                }
            } else {
                hapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        } catch (e: Exception) {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    /**
     * Triggered on claiming milestone reward or final button press.
     */
    fun triggerGrandFinaleRumble(context: Context, hapticFeedback: HapticFeedback?, isVibrationEnabled: Boolean = true) {
        if (!isVibrationEnabled) return
        try {
            val vibrator = getVibrator(context)
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 40, 30, 60, 30, 90, 40, 180)
                    val amplitudes = intArrayOf(0, 120, 0, 160, 0, 200, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 40, 30, 60, 30, 90, 40, 180), -1)
                }
            } else {
                hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        } catch (e: Exception) {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
