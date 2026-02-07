package org.override.sense.feature.monitor.domain

import kotlinx.coroutines.flow.Flow

interface MonitorRepository {
    val isScanning: Flow<Boolean>
    val detectedSounds: Flow<SoundEvent>
    val recentHistory: Flow<List<SoundEvent>>
    
    suspend fun setScanning(isScanning: Boolean)
}
