package org.override.sense.feature.monitor.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.sense.feature.monitor.presentation.components.MonitorStatus
import org.override.sense.feature.monitor.presentation.components.PulseAnimation

@Composable
fun MonitorRoot(
    viewModel: MonitorViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MonitorScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun MonitorScreen(
    state: MonitorState,
    onAction: (MonitorAction) -> Unit,
) {
    // Determine active category based on lastDetection
    // Ideally we would want to clear this after some time, but for now we rely on the state
    // Let's assume if lastDetection is very old it shouldn't be shown, but for simplicity
    // we just use it if isScanning is true. 
    // To make it truly reactive to *current* sound, the ViewModel should handle "active alert".
    // For this UI implementation:
    val activeCategory = if (state.isScanning) state.lastDetection?.category else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onAction(MonitorAction.ToggleMonitoring) },
        contentAlignment = Alignment.Center
    ) {
        PulseAnimation(
            isScanning = state.isScanning,
            currentCategory = activeCategory,
            modifier = Modifier.size(400.dp) // Larger pulse area
        )
        
        MonitorStatus(
            isScanning = state.isScanning,
            lastDetection = if (state.isScanning) state.lastDetection else null
        )
    }
}
