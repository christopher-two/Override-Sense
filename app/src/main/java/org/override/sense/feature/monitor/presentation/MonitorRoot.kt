package org.override.sense.feature.monitor.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.sense.feature.monitor.presentation.components.EventItem
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Section: Status and Pulse
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            PulseAnimation(
                isScanning = state.isScanning,
                modifier = Modifier.size(300.dp)
            )
            
            MonitorStatus(isScanning = state.isScanning)
        }

        // Bottom Section: History
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Actividad Reciente",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (state.history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se han detectado sonidos recientemente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.history, key = { it.id }) { event ->
                        EventItem(event = event)
                    }
                }
            }
        }
    }
}
