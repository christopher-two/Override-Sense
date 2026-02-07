package org.override.sense.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.sense.feature.settings.domain.SettingsUseCases

class SettingsViewModel(
    private val useCases: SettingsUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart { loadSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState(isLoading = true)
        )

    private fun loadSettings() {
        viewModelScope.launch {
            useCases.getSettings().collect { settings ->
                _state.update {
                    it.copy(
                        theme = settings.theme,
                        language = settings.language,
                        color = settings.color,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            when (action) {
                is SettingsAction.UpdateTheme -> useCases.updateTheme(action.theme)
                is SettingsAction.UpdateLanguage -> useCases.updateLanguage(action.language)
                is SettingsAction.UpdateColor -> useCases.updateColor(action.color)
                SettingsAction.ClearData -> useCases.clearData()
            }
        }
    }
}