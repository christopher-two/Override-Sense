package org.override.sense.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    
    // Configuración de UI
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateColor(color: AppColor)
    
    // Configuración de monitoreo
    suspend fun updateSensitivity(sensitivity: SensitivityLevel)
    suspend fun updateNotificationPriority(priority: NotificationPriority)
    suspend fun updateVibrationPattern(pattern: VibrationPattern)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateVibrationIntensity(intensity: Float)
    suspend fun updateMicrophoneSensitivity(sensitivity: Float)
    suspend fun updateShowConfidence(show: Boolean)
    suspend fun updateBypassDoNotDisturb(bypass: Boolean)
    suspend fun updateShowOnLockScreen(show: Boolean)
    suspend fun updateEnableNotificationLights(enable: Boolean)
    suspend fun updateNoiseReduction(enable: Boolean)
    suspend fun updateMinAmplitudeThreshold(threshold: Int)
    suspend fun updatePlaySound(play: Boolean)
    suspend fun updateSoundVolume(volume: Float)
    suspend fun updateOnlyAlertOnce(once: Boolean)
    suspend fun updateBatteryOptimization(enable: Boolean)
    suspend fun updateWorkOnlyWhenCharging(enable: Boolean)
    
    suspend fun clearAllData()
}
