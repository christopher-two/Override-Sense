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
                        color = settings.color,
                        monitorSettings = settings.monitorSettings,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            when (action) {
                // UI Settings
                is SettingsAction.UpdateTheme -> useCases.updateTheme(action.theme)
                is SettingsAction.UpdateColor -> useCases.updateColor(action.color)
                
                // Detection Settings
                is SettingsAction.UpdateSensitivity -> useCases.updateSensitivity(action.sensitivity)
                
                // Notification Settings
                is SettingsAction.UpdateNotificationPriority -> useCases.updateNotificationPriority(action.priority)
                is SettingsAction.UpdateShowConfidence -> useCases.updateShowConfidence(action.show)
                is SettingsAction.UpdateBypassDoNotDisturb -> useCases.updateBypassDoNotDisturb(action.bypass)
                is SettingsAction.UpdateShowOnLockScreen -> useCases.updateShowOnLockScreen(action.show)
                is SettingsAction.UpdateEnableNotificationLights -> useCases.updateEnableNotificationLights(action.enable)
                
                // Vibration Settings
                is SettingsAction.UpdateVibrationPattern -> useCases.updateVibrationPattern(action.pattern)
                is SettingsAction.UpdateVibrationEnabled -> useCases.updateVibrationEnabled(action.enabled)
                is SettingsAction.UpdateVibrationIntensity -> useCases.updateVibrationIntensity(action.intensity)
                
                // Audio Settings
                is SettingsAction.UpdateMicrophoneSensitivity -> useCases.updateMicrophoneSensitivity(action.sensitivity)
                is SettingsAction.UpdateNoiseReduction -> useCases.updateNoiseReduction(action.enable)
                is SettingsAction.UpdateMinAmplitudeThreshold -> useCases.updateMinAmplitudeThreshold(action.threshold)
                
                // Alert Settings
                is SettingsAction.UpdatePlaySound -> useCases.updatePlaySound(action.play)
                is SettingsAction.UpdateSoundVolume -> useCases.updateSoundVolume(action.volume)
                is SettingsAction.UpdateOnlyAlertOnce -> useCases.updateOnlyAlertOnce(action.once)
                
                // Battery Settings
                is SettingsAction.UpdateBatteryOptimization -> useCases.updateBatteryOptimization(action.enable)
                is SettingsAction.UpdateWorkOnlyWhenCharging -> useCases.updateWorkOnlyWhenCharging(action.enable)
                
                SettingsAction.ClearData -> useCases.clearData()
            }
        }
    }
}