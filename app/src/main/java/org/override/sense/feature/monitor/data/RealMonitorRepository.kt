package org.override.sense.feature.monitor.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.override.sense.core.common.feedback.VibrationManager
import org.override.sense.core.common.logging.Logger
import org.override.sense.feature.monitor.domain.MonitorRepository
import org.override.sense.feature.monitor.domain.SoundEvent
import java.time.LocalDateTime
import java.util.UUID

class RealMonitorRepository(
    private val audioRecorder: AudioRecorder,
    private val soundClassifier: SoundClassifier,
    private val vibrationManager: VibrationManager,
    private val logger: Logger
) : MonitorRepository {

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: Flow<Boolean> = _isScanning

    private val _detectedSounds = MutableStateFlow<SoundEvent?>(null)
    override val detectedSounds: Flow<SoundEvent?> = _detectedSounds

    private val _history = MutableStateFlow<List<SoundEvent>>(emptyList())
    override val recentHistory: Flow<List<SoundEvent>> = _history

    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun setScanning(isScanning: Boolean) {
        _isScanning.value = isScanning
        if (isScanning) {
            startMonitoring()
        } else {
            stopMonitoring()
        }
    }

    private fun startMonitoring() {
        if (recordingJob?.isActive == true) return

        recordingJob = scope.launch {
            logger.d("MonitorRepository", "Starting audio recording...")
            audioRecorder.startRecording().collect { audioData ->
                val results = soundClassifier.classify(audioData)
                
                    if (results.isNotEmpty()) {
                        val bestMatch = results.first()
                        
                        if (bestMatch.score > 0.3f) { // Lowered to match classifier
                            val event = SoundEvent(
                                id = UUID.randomUUID().toString(),
                                name = bestMatch.label,
                                category = bestMatch.category,
                                confidence = bestMatch.score,
                                timestamp = LocalDateTime.now()
                            )
                            
                            logger.i("MonitorRepository", "Sound detected: ${event.name} (${event.confidence})")
                            
                            // Trigger Haptic Feedback
                            vibrationManager.vibrate(event.category)
                            
                            _detectedSounds.value = event
                            updateHistory(event)
                        }
                    }
            }
        }
    }

    private fun stopMonitoring() {
        logger.d("MonitorRepository", "Stopping monitoring...")
        audioRecorder.stopRecording()
        recordingJob?.cancel()
        recordingJob = null
    }

    private fun updateHistory(event: SoundEvent) {
        val currentList = _history.value.toMutableList()
        currentList.add(0, event)
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }
        _history.value = currentList
    }
}
