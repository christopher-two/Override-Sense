package org.override.sense.feature.monitor.domain

import kotlinx.coroutines.flow.Flow

interface MonitorRepository {
    val isScanning: Flow<Boolean>
    val detectedSounds: Flow<SoundEvent?>
    val recentHistory: Flow<List<SoundEvent>>
    
    suspend fun setScanning(isScanning: Boolean)
    
    /**
     * Reports a detected sound event to the app's state.
     * This allows background workers to update the UI via repository.
     */
    fun emitEvent(event: SoundEvent)
}
