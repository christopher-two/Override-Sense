package org.override.sense.feature.monitor.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.override.sense.feature.monitor.domain.MonitorRepository
import org.override.sense.feature.monitor.domain.SoundCategory
import org.override.sense.feature.monitor.domain.SoundEvent
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

class FakeMonitorRepository : MonitorRepository {

    private val _isScanning = MutableStateFlow(true)
    override val isScanning: Flow<Boolean> = _isScanning

    private val _history = MutableStateFlow<List<SoundEvent>>(emptyList())
    override val recentHistory: Flow<List<SoundEvent>> = _history

    // Simulate detection stream
    override val detectedSounds: Flow<SoundEvent> = flow {
        while (true) {
            if (_isScanning.value) {
                val delayTime = Random.nextLong(5000, 15000)
                delay(delayTime)
                
                val event = generateRandomEvent()
                
                // Add to history
                val currentHistory = _history.value.toMutableList()
                currentHistory.add(0, event)
                if (currentHistory.size > 20) {
                    currentHistory.removeAt(currentHistory.lastIndex)
                }
                _history.value = currentHistory
                
                emit(event)
            } else {
                delay(1000)
            }
        }
    }

    override suspend fun setScanning(isScanning: Boolean) {
        _isScanning.value = isScanning
    }

    override fun emitEvent(event: SoundEvent) {
        TODO("Not yet implemented")
    }

    private fun generateRandomEvent(): SoundEvent {
        val type = Random.nextInt(0, 4)
        return when (type) {
            0 -> SoundEvent(UUID.randomUUID().toString(), "Alarma de Incendio", SoundCategory.CRITICAL, 0.95f)
            1 -> SoundEvent(UUID.randomUUID().toString(), "Sirena de Policía", SoundCategory.CRITICAL, 0.88f)
            2 -> SoundEvent(UUID.randomUUID().toString(), "Timbre", SoundCategory.WARNING, 0.75f)
            else -> SoundEvent(UUID.randomUUID().toString(), "Golpes en Puerta", SoundCategory.WARNING, 0.65f)
        }
    }
}
