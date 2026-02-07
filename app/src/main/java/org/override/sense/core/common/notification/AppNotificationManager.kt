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

interface AppNotificationManager {
    fun createNotificationChannels()
    fun getForegroundNotification(): Notification
    fun showEventNotification(event: SoundEvent)
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
                description = "Notifications for detected sounds"
                enableVibration(true)
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
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use default for now, ideally a specific one
            .setContentTitle("Override Sense Active")
            .setContentText("Listening for sounds...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun showEventNotification(event: SoundEvent) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = when (event.category) {
            SoundCategory.CRITICAL -> "CRITICAL ALERT"
            SoundCategory.WARNING -> "Warning"
            SoundCategory.INFO -> "Info"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$title: ${event.name}")
            .setContentText("Confidence: ${(event.confidence * 100).toInt()}%")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALERT_BASE + event.hashCode(), notification)
    }
}
