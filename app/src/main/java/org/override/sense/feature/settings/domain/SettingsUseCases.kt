package org.override.sense.feature.settings.domain

import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repository.getSettings()
}

class UpdateThemeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(theme: AppTheme) = repository.updateTheme(theme)
}

class UpdateColorUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(color: AppColor) = repository.updateColor(color)
}

class UpdateSensitivityUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(sensitivity: SensitivityLevel) = repository.updateSensitivity(sensitivity)
}

class UpdateNotificationPriorityUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(priority: NotificationPriority) = repository.updateNotificationPriority(priority)
}

class UpdateVibrationPatternUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(pattern: VibrationPattern) = repository.updateVibrationPattern(pattern)
}

class UpdateVibrationEnabledUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.updateVibrationEnabled(enabled)
}

class UpdateVibrationIntensityUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(intensity: Float) = repository.updateVibrationIntensity(intensity)
}

class UpdateMicrophoneSensitivityUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(sensitivity: Float) = repository.updateMicrophoneSensitivity(sensitivity)
}

class UpdateShowConfidenceUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(show: Boolean) = repository.updateShowConfidence(show)
}

class UpdateBypassDoNotDisturbUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(bypass: Boolean) = repository.updateBypassDoNotDisturb(bypass)
}

class UpdateShowOnLockScreenUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(show: Boolean) = repository.updateShowOnLockScreen(show)
}

class UpdateEnableNotificationLightsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enable: Boolean) = repository.updateEnableNotificationLights(enable)
}

class UpdateNoiseReductionUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enable: Boolean) = repository.updateNoiseReduction(enable)
}

class UpdateMinAmplitudeThresholdUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(threshold: Int) = repository.updateMinAmplitudeThreshold(threshold)
}

class UpdatePlaySoundUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(play: Boolean) = repository.updatePlaySound(play)
}

class UpdateSoundVolumeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(volume: Float) = repository.updateSoundVolume(volume)
}

class UpdateOnlyAlertOnceUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(once: Boolean) = repository.updateOnlyAlertOnce(once)
}

class UpdateBatteryOptimizationUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enable: Boolean) = repository.updateBatteryOptimization(enable)
}

class UpdateWorkOnlyWhenChargingUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enable: Boolean) = repository.updateWorkOnlyWhenCharging(enable)
}

class ClearDataUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.clearAllData()
}

data class SettingsUseCases(
    val getSettings: GetSettingsUseCase,
    val updateTheme: UpdateThemeUseCase,
    val updateColor: UpdateColorUseCase,
    val updateSensitivity: UpdateSensitivityUseCase,
    val updateNotificationPriority: UpdateNotificationPriorityUseCase,
    val updateVibrationPattern: UpdateVibrationPatternUseCase,
    val updateVibrationEnabled: UpdateVibrationEnabledUseCase,
    val updateVibrationIntensity: UpdateVibrationIntensityUseCase,
    val updateMicrophoneSensitivity: UpdateMicrophoneSensitivityUseCase,
    val updateShowConfidence: UpdateShowConfidenceUseCase,
    val updateBypassDoNotDisturb: UpdateBypassDoNotDisturbUseCase,
    val updateShowOnLockScreen: UpdateShowOnLockScreenUseCase,
    val updateEnableNotificationLights: UpdateEnableNotificationLightsUseCase,
    val updateNoiseReduction: UpdateNoiseReductionUseCase,
    val updateMinAmplitudeThreshold: UpdateMinAmplitudeThresholdUseCase,
    val updatePlaySound: UpdatePlaySoundUseCase,
    val updateSoundVolume: UpdateSoundVolumeUseCase,
    val updateOnlyAlertOnce: UpdateOnlyAlertOnceUseCase,
    val updateBatteryOptimization: UpdateBatteryOptimizationUseCase,
    val updateWorkOnlyWhenCharging: UpdateWorkOnlyWhenChargingUseCase,
    val clearData: ClearDataUseCase
)
