package org.override.sense.feature.monitor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.override.sense.feature.monitor.domain.MonitorRepository

class MonitorViewModel(
    private val repository: MonitorRepository
) : ViewModel() {

    // Combine streams from repository into UI state
    val state = combine(
        repository.isScanning,
        repository.recentHistory,
        repository.detectedSounds
    ) { isScanning, history, latestSound ->
        MonitorState(
            isScanning = isScanning,
            history = history,
            lastDetection = latestSound // This might need smarter handling if detectedSounds emits continuously
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MonitorState()
    )

    fun onAction(action: MonitorAction) {
        // Actions if any (e.g. clear history locally, etc.)
        viewModelScope.launch {
            // Handle actions like toggling scan if we had a button for it in the UI (currently in Home AppBar)
            // But if HomeViewModel handles the toggle, it needs to call repository.setScanning
            // Wait, HomeViewModel handles the global "ToggleMonitoring" action.
            // We need to ensure that action propagates to the repository.
        }
    }
}