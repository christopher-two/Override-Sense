package org.override.sense.core.common.feedback

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import org.override.sense.feature.monitor.domain.SoundCategory

interface VibrationManager {
    fun vibrate(category: SoundCategory)
    fun cancel()
}

class SystemVibrationManager(
    private val context: Context
) : VibrationManager {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun vibrate(category: SoundCategory) {
        // Only vibrate if the device has a vibrator
        if (!vibrator.hasVibrator()) return

        // Cancel any ongoing vibration first
        vibrator.cancel()

        val effect = when (category) {
            SoundCategory.CRITICAL -> createCriticalVibration()
            SoundCategory.WARNING -> createWarningVibration()
            SoundCategory.INFO -> createInfoVibration()
        }

        // Use AudioAttributes to ensure vibration is treated as an alarm or notification
        // This is important for bypassing some DND settings or ensuring visibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(
                    when (category) {
                        SoundCategory.CRITICAL -> AudioAttributes.USAGE_ALARM
                        SoundCategory.WARNING -> AudioAttributes.USAGE_NOTIFICATION_EVENT
                        SoundCategory.INFO -> AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                    }
                )
                .build()
            vibrator.vibrate(effect, attributes)
        } else {
            vibrator.vibrate(effect)
        }
    }

    override fun cancel() {
        vibrator.cancel()
    }

    private fun createCriticalVibration(): VibrationEffect {
        // Urgent: Long, intense, repetitive
        // Pattern: 0ms delay, 1000ms vibration, 200ms pause, repeat.
        // Amplitudes: High
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 500, 100, 500, 100, 1000)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
            VibrationEffect.createWaveform(
                timings,
                amplitudes,
                -1
            ) // -1 for no repeat (just one sequence) or 0 to repeat
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
        }
    }

    private fun createWarningVibration(): VibrationEffect {
        // Warning: Short, intermittent (double pulse)
        // Pattern: 0ms delay, 200ms vib, 100ms pause, 200ms vib
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 200, 100, 200)
            val amplitudes = intArrayOf(0, 200, 0, 200)
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
        }
    }

    private fun createInfoVibration(): VibrationEffect {
        // Info: Subtle, short
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createOneShot(50, 50)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        }
    }
}
