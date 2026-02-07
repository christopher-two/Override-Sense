package org.override.sense.feature.monitor.presentation

import org.override.sense.feature.monitor.domain.SoundEvent

data class MonitorState(
    val isScanning: Boolean = true,
    val lastDetection: SoundEvent? = null,
    val history: List<SoundEvent> = emptyList()
)