package org.override.sense.core.common.feedback

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import org.override.sense.feature.monitor.domain.SoundCategory
import org.override.sense.feature.settings.domain.VibrationPattern

interface VibrationManager {
    fun vibrate(
        category: SoundCategory,
        pattern: VibrationPattern = VibrationPattern.DOUBLE,
        intensity: Float = 1.0f
    )
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

    override fun vibrate(
        category: SoundCategory,
        pattern: VibrationPattern,
        intensity: Float
    ) {
        // Don't vibrate if pattern is NONE
        if (pattern == VibrationPattern.NONE) return
        
        // Only vibrate if the device has a vibrator
        if (!vibrator.hasVibrator()) return

        // Cancel any ongoing vibration first
        vibrator.cancel()

        val effect = createVibrationEffect(pattern, intensity)

        // Use AudioAttributes to ensure vibration is treated as an alarm or notification
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

    private fun createVibrationEffect(pattern: VibrationPattern, intensity: Float): VibrationEffect {
        // Clamp intensity between 0.0 and 1.0
        val clampedIntensity = intensity.coerceIn(0.0f, 1.0f)
        
        // Convert to amplitude (1-255)
        val amplitude = (clampedIntensity * 255).toInt().coerceIn(1, 255)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (pattern) {
                VibrationPattern.NONE -> {
                    VibrationEffect.createOneShot(0, 0)
                }
                VibrationPattern.SHORT -> {
                    VibrationEffect.createOneShot(200, amplitude)
                }
                VibrationPattern.MEDIUM -> {
                    VibrationEffect.createOneShot(500, amplitude)
                }
                VibrationPattern.LONG -> {
                    VibrationEffect.createOneShot(1000, amplitude)
                }
                VibrationPattern.DOUBLE -> {
                    val timings = longArrayOf(0, 500, 200, 500)
                    val amplitudes = intArrayOf(0, amplitude, 0, amplitude)
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                }
                VibrationPattern.TRIPLE -> {
                    val timings = longArrayOf(0, 200, 150, 200, 150, 200)
                    val amplitudes = intArrayOf(0, amplitude, 0, amplitude, 0, amplitude)
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                }
                VibrationPattern.PULSING -> {
                    val timings = longArrayOf(0, 200, 100, 300, 100, 200, 100, 300)
                    val amplitudes = intArrayOf(0, amplitude, 0, amplitude, 0, amplitude, 0, amplitude)
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                }
            }
        } else {
            // Fallback for older devices
            @Suppress("DEPRECATION")
            when (pattern) {
                VibrationPattern.NONE -> VibrationEffect.createOneShot(0, 0)
                VibrationPattern.SHORT -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationPattern.MEDIUM -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationPattern.LONG -> VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationPattern.DOUBLE -> VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1)
                VibrationPattern.TRIPLE -> VibrationEffect.createWaveform(longArrayOf(0, 200, 150, 200, 150, 200), -1)
                VibrationPattern.PULSING -> VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300, 100, 200, 100, 300), -1)
            }
        }
    }
}
