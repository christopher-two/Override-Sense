package org.override.sense.feature.monitor.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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