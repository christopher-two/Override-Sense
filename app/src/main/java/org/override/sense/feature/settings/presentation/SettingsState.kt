package org.override.sense.feature.settings.presentation

data class SettingsState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)