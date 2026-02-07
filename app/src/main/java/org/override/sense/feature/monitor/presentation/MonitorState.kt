package org.override.sense.feature.monitor.presentation

data class MonitorState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)