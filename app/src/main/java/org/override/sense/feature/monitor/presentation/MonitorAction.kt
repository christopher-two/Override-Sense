package org.override.sense.feature.monitor.presentation

sealed interface MonitorAction {
    data object ToggleMonitoring : MonitorAction
}