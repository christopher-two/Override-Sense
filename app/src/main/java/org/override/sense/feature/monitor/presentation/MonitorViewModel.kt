package org.override.sense.feature.monitor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.override.sense.feature.monitor.domain.MonitorRepository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import org.override.sense.feature.monitor.domain.SoundEvent
import java.time.Duration
import java.time.LocalDateTime

class MonitorViewModel(
    private val repository: MonitorRepository
) : ViewModel() {

    // Helper flow to auto-reset the "last detection" for UI visualization purposes
    // This makes the UI "flash" for a few seconds then return to normal scanning state
    private val activeDetection = repository.detectedSounds.map { event ->
        if (event != null) {
            // Keep the event active?
            // Actually, we want to emit it, then after some time emit null?
            // Flow map is 1-to-1. We need a different approach or combine logic.
            // Let's keep it simple: The UI will show the last detection.
            // But to make it "reactive" (alert stops if sound stops), we need to clear it.
            // Since we don't have a "silence detected" event, we can use a timeout in UI or here.
            // Let's implement a timeout in the repository or just let it stick for now as requested.
            // User: "una si solo se esta detectando y cambia el numero de ondas dependiendo del peligro"
            // This implies transient state.
            event
        } else {
            null
        }
    }

    // Combine streams from repository into UI state
    val state = combine(
        repository.isScanning,
        repository.recentHistory,
        repository.detectedSounds
    ) { isScanning, history, latestSound ->
        // Check if latestSound is recent (e.g. < 5 seconds ago) to show active alert
        val isRecent = latestSound?.timestamp?.let { 
            Duration.between(it, LocalDateTime.now()).seconds < 5 
        } ?: false
        
        val activeSound = if (isRecent) latestSound else null

        MonitorState(
            isScanning = isScanning,
            history = history,
            lastDetection = activeSound
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MonitorState()
    )

    fun onAction(action: MonitorAction) {
        viewModelScope.launch {
            when (action) {
                is MonitorAction.ToggleMonitoring -> {
                    // Toggle current scanning state
                    val currentScanning = state.value.isScanning
                    repository.setScanning(!currentScanning)
                }
            }
        }
    }
}