package org.override.sense.feature.settings.presentation

import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppTheme
import org.override.sense.feature.settings.domain.NotificationPriority
import org.override.sense.feature.settings.domain.SensitivityLevel
import org.override.sense.feature.settings.domain.VibrationPattern

sealed interface SettingsAction {
    // UI Settings
    data class UpdateTheme(val theme: AppTheme) : SettingsAction
    data class UpdateColor(val color: AppColor) : SettingsAction
    
    // Detection Settings
    data class UpdateSensitivity(val sensitivity: SensitivityLevel) : SettingsAction
    
    // Notification Settings
    data class UpdateNotificationPriority(val priority: NotificationPriority) : SettingsAction
    data class UpdateShowConfidence(val show: Boolean) : SettingsAction
    data class UpdateBypassDoNotDisturb(val bypass: Boolean) : SettingsAction
    data class UpdateShowOnLockScreen(val show: Boolean) : SettingsAction
    data class UpdateEnableNotificationLights(val enable: Boolean) : SettingsAction
    
    // Vibration Settings
    data class UpdateVibrationPattern(val pattern: VibrationPattern) : SettingsAction
    data class UpdateVibrationEnabled(val enabled: Boolean) : SettingsAction
    data class UpdateVibrationIntensity(val intensity: Float) : SettingsAction
    
    // Audio Settings
    data class UpdateMicrophoneSensitivity(val sensitivity: Float) : SettingsAction
    data class UpdateNoiseReduction(val enable: Boolean) : SettingsAction
    data class UpdateMinAmplitudeThreshold(val threshold: Int) : SettingsAction
    
    // Alert Settings
    data class UpdatePlaySound(val play: Boolean) : SettingsAction
    data class UpdateSoundVolume(val volume: Float) : SettingsAction
    data class UpdateOnlyAlertOnce(val once: Boolean) : SettingsAction
    
    // Battery Settings
    data class UpdateBatteryOptimization(val enable: Boolean) : SettingsAction
    data class UpdateWorkOnlyWhenCharging(val enable: Boolean) : SettingsAction
    
    data object ClearData : SettingsAction
}