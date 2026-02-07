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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.override.sense.feature.monitor.domain.SoundEvent
import java.time.Duration
import java.time.LocalDateTime

class MonitorViewModel(
    private val repository: MonitorRepository
) : ViewModel() {

    // Ticker flow that emits every second to refresh "recent" status
    private val tickerFlow = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }

    // Combine streams from repository AND ticker into UI state
    val state = combine(
        repository.isScanning,
        repository.recentHistory,
        repository.detectedSounds,
        tickerFlow
    ) { isScanning, history, latestSound, _ ->
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