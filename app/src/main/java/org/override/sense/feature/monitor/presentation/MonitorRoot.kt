package org.override.sense.feature.monitor.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import org.override.sense.core.ui.SenseTheme

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
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("Monitor Screen Placeholder")
    }
}

@Preview
@Composable
private fun Preview() {
    SenseTheme {
        MonitorScreen(
            state = MonitorState(),
            onAction = {}
        )
    }
}