package org.override.sense.core.common.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.override.sense.MainActivity
import org.override.sense.R
import org.override.sense.feature.monitor.domain.SoundCategory
import org.override.sense.feature.monitor.domain.SoundEvent
import org.override.sense.feature.settings.domain.MonitorSettings

interface AppNotificationManager {
    fun createNotificationChannels()
    fun getForegroundNotification(): Notification
    fun showEventNotification(event: SoundEvent, settings: MonitorSettings)
}

class SystemAppNotificationManager(
    private val context: Context
) : AppNotificationManager {

    companion object {
        const val CHANNEL_ID_FOREGROUND = "monitor_foreground_channel"
        const val CHANNEL_ID_ALERTS = "monitor_alerts_channel"
        const val NOTIFICATION_ID_FOREGROUND = 1001
        const val NOTIFICATION_ID_ALERT_BASE = 2000
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val foregroundChannel = NotificationChannel(
                CHANNEL_ID_FOREGROUND,
                "Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Running background audio monitoring"
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Sound Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical sound detection alerts"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true) // Bypass Do Not Disturb for critical alerts
            }

            notificationManager.createNotificationChannel(foregroundChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }

    override fun getForegroundNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Override Sense Active")
            .setContentText("Listening for sounds...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun showEventNotification(event: SoundEvent, settings: MonitorSettings) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build title with optional confidence
        val baseTitle = when (event.category) {
            SoundCategory.CRITICAL -> "🚨 ${event.name}"
            SoundCategory.WARNING -> "⚠️ ${event.name}"
            SoundCategory.INFO -> "ℹ️ ${event.name}"
        }
        
        val title = if (settings.showConfidence) {
            "$baseTitle (${(event.confidence * 100).toInt()}%)"
        } else {
            baseTitle
        }

        // Map settings priority to notification priority
        val priority = when (settings.notificationPriority) {
            org.override.sense.feature.settings.domain.NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            org.override.sense.feature.settings.domain.NotificationPriority.DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            org.override.sense.feature.settings.domain.NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            org.override.sense.feature.settings.domain.NotificationPriority.MAX -> NotificationCompat.PRIORITY_MAX
        }

        // Map settings visibility
        val visibility = if (settings.showOnLockScreen) {
            NotificationCompat.VISIBILITY_PUBLIC
        } else {
            NotificationCompat.VISIBILITY_PRIVATE
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setPriority(priority)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(visibility)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(settings.onlyAlertOnce)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(title)
                .setBigContentTitle(title))

        // Apply vibration pattern if enabled
        if (settings.vibrationEnabled) {
            val pattern = getVibrationPattern(settings.vibrationPattern, settings.vibrationIntensity)
            builder.setVibrate(pattern)
        }

        // Apply LED lights if enabled
        if (settings.enableNotificationLights) {
            builder.setLights(0xFFFF0000.toInt(), 1000, 1000) // Red light blinking
        }

        // Apply sound settings
        if (!settings.playSound) {
            builder.setSilent(true)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID_ALERT_BASE + event.hashCode(), notification)
    }

    private fun getVibrationPattern(
        pattern: org.override.sense.feature.settings.domain.VibrationPattern,
        intensity: Float
    ): LongArray {
        val shortDuration = (200 * intensity).toLong()
        val mediumDuration = (500 * intensity).toLong()
        val longDuration = (1000 * intensity).toLong()
        val pause = 200L

        return when (pattern) {
            org.override.sense.feature.settings.domain.VibrationPattern.NONE -> longArrayOf(0)
            org.override.sense.feature.settings.domain.VibrationPattern.SHORT -> longArrayOf(0, shortDuration)
            org.override.sense.feature.settings.domain.VibrationPattern.MEDIUM -> longArrayOf(0, mediumDuration)
            org.override.sense.feature.settings.domain.VibrationPattern.LONG -> longArrayOf(0, longDuration)
            org.override.sense.feature.settings.domain.VibrationPattern.DOUBLE -> 
                longArrayOf(0, mediumDuration, pause, mediumDuration)
            org.override.sense.feature.settings.domain.VibrationPattern.TRIPLE -> 
                longArrayOf(0, shortDuration, pause, shortDuration, pause, shortDuration)
            org.override.sense.feature.settings.domain.VibrationPattern.PULSING -> 
                longArrayOf(0, shortDuration, pause, mediumDuration, pause, shortDuration, pause, mediumDuration)
        }
    }
}
