package org.override.sense.feature.monitor.work

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.override.sense.core.common.feedback.VibrationManager
import org.override.sense.core.common.logging.Logger
import org.override.sense.core.common.notification.AppNotificationManager
import org.override.sense.core.common.notification.SystemAppNotificationManager
import org.override.sense.feature.monitor.data.AudioRecorder
import org.override.sense.feature.monitor.data.RealMonitorRepository
import org.override.sense.feature.monitor.data.SoundClassifier
import org.override.sense.feature.monitor.domain.MonitorRepository
import org.override.sense.feature.monitor.domain.SoundEvent
import java.time.LocalDateTime
import java.util.UUID

class MonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    // Create dedicated instances for the worker to avoid lifecycle conflicts
    private val logger: Logger by inject()
    private val audioRecorder: AudioRecorder = AudioRecorder(logger) // Dedicated instance
    private val soundClassifier: SoundClassifier by inject()
    private val vibrationManager: VibrationManager by inject()
    private val notificationManager: AppNotificationManager by inject()
    
    // We need to push events back to the repository so the UI can see them.
    // Ideally, the repository is the source of truth, and this worker is just the engine.
    // Casting to implementation to access the emit method or exposure
    private val repository: MonitorRepository by inject()

    override suspend fun doWork(): Result {
        logger.d("MonitorWorker", "Starting background monitoring work")
        
        notificationManager.createNotificationChannels()
        val foregroundInfo = createForegroundInfo()
        setForeground(foregroundInfo)

        try {
            audioRecorder.startRecording().collect { audioData ->
                val results = soundClassifier.classify(audioData)
                
                if (results.isNotEmpty()) {
                    val bestMatch = results.first()
                    
                    if (bestMatch.score > 0.3f) {
                        val event = SoundEvent(
                            id = UUID.randomUUID().toString(),
                            name = bestMatch.label,
                            category = bestMatch.category,
                            confidence = bestMatch.score,
                            timestamp = LocalDateTime.now()
                        )
                        
                        logger.i("MonitorWorker", "Sound detected: ${event.name}")
                        
                        // 1. Vibrate
                        vibrationManager.vibrate(event.category)
                        
                        // 2. Notify
                        notificationManager.showEventNotification(event)
                        
                        // 3. Update Repository (so UI sees it)
                        // This is a bit of a hack if repository isn't designed for this external input,
                        // but since RealMonitorRepository is a Singleton in Koin, we can cast it safely usually
                        // or better, add a method to the interface `emitDetectedSound`.
                        (repository as? RealMonitorRepository)?.emitEventFromWorker(event)
                    }
                }
            }
        } catch (e: Exception) {
            logger.e("MonitorWorker", "Error in monitor worker", e)
            return Result.failure()
        } finally {
            // Ensure audio recording is stopped when worker finishes or is cancelled
            audioRecorder.stopRecording()
            logger.d("MonitorWorker", "Background monitoring work finished, audio recording stopped")
        }

        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = notificationManager.getForegroundNotification()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                SystemAppNotificationManager.NOTIFICATION_ID_FOREGROUND,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            ForegroundInfo(
                SystemAppNotificationManager.NOTIFICATION_ID_FOREGROUND,
                notification
            )
        }
    }
}
