package org.override.sense.feature.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppTheme
import org.override.sense.feature.settings.domain.MonitorSettings
import org.override.sense.feature.settings.domain.NotificationPriority
import org.override.sense.feature.settings.domain.SensitivityLevel
import org.override.sense.feature.settings.domain.SettingsRepository
import org.override.sense.feature.settings.domain.UserSettings
import org.override.sense.feature.settings.domain.VibrationPattern

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        // UI Settings
        val THEME = stringPreferencesKey("theme")
        val COLOR = stringPreferencesKey("color")
        
        // Detection Settings
        val SENSITIVITY = stringPreferencesKey("sensitivity")
        
        // Notification Settings
        val NOTIFICATION_PRIORITY = stringPreferencesKey("notification_priority")
        val SHOW_CONFIDENCE = booleanPreferencesKey("show_confidence")
        val BYPASS_DND = booleanPreferencesKey("bypass_dnd")
        val SHOW_ON_LOCKSCREEN = booleanPreferencesKey("show_on_lockscreen")
        val ENABLE_NOTIFICATION_LIGHTS = booleanPreferencesKey("enable_notification_lights")
        
        // Vibration Settings
        val VIBRATION_PATTERN = stringPreferencesKey("vibration_pattern")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val VIBRATION_INTENSITY = floatPreferencesKey("vibration_intensity")
        
        // Audio Settings
        val MICROPHONE_SENSITIVITY = floatPreferencesKey("microphone_sensitivity")
        val NOISE_REDUCTION = booleanPreferencesKey("noise_reduction")
        val MIN_AMPLITUDE_THRESHOLD = intPreferencesKey("min_amplitude_threshold")
        
        // Alert Settings
        val PLAY_SOUND = booleanPreferencesKey("play_sound")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val ONLY_ALERT_ONCE = booleanPreferencesKey("only_alert_once")
        
        // Battery Settings
        val BATTERY_OPTIMIZATION = booleanPreferencesKey("battery_optimization")
        val WORK_ONLY_WHEN_CHARGING = booleanPreferencesKey("work_only_when_charging")
    }

    override fun getSettings(): Flow<UserSettings> {
        return context.settingsDataStore.data.map { preferences ->
            val themeName = preferences[Keys.THEME] ?: AppTheme.SYSTEM.name
            val colorName = preferences[Keys.COLOR] ?: AppColor.BLUE.name
            
            val monitorSettings = MonitorSettings(
                sensitivity = SensitivityLevel.valueOf(
                    preferences[Keys.SENSITIVITY] ?: SensitivityLevel.MEDIUM.name
                ),
                detectionThreshold = getSensitivityThreshold(
                    SensitivityLevel.valueOf(
                        preferences[Keys.SENSITIVITY] ?: SensitivityLevel.MEDIUM.name
                    )
                ),
                notificationPriority = NotificationPriority.valueOf(
                    preferences[Keys.NOTIFICATION_PRIORITY] ?: NotificationPriority.MAX.name
                ),
                showConfidence = preferences[Keys.SHOW_CONFIDENCE] ?: false,
                bypassDoNotDisturb = preferences[Keys.BYPASS_DND] ?: true,
                showOnLockScreen = preferences[Keys.SHOW_ON_LOCKSCREEN] ?: true,
                enableNotificationLights = preferences[Keys.ENABLE_NOTIFICATION_LIGHTS] ?: true,
                vibrationPattern = VibrationPattern.valueOf(
                    preferences[Keys.VIBRATION_PATTERN] ?: VibrationPattern.DOUBLE.name
                ),
                vibrationEnabled = preferences[Keys.VIBRATION_ENABLED] ?: true,
                vibrationIntensity = preferences[Keys.VIBRATION_INTENSITY] ?: 1.0f,
                microphoneSensitivity = preferences[Keys.MICROPHONE_SENSITIVITY] ?: 1.0f,
                noiseReduction = preferences[Keys.NOISE_REDUCTION] ?: false,
                minAmplitudeThreshold = preferences[Keys.MIN_AMPLITUDE_THRESHOLD] ?: 100,
                playSound = preferences[Keys.PLAY_SOUND] ?: true,
                soundVolume = preferences[Keys.SOUND_VOLUME] ?: 1.0f,
                onlyAlertOnce = preferences[Keys.ONLY_ALERT_ONCE] ?: false,
                batteryOptimization = preferences[Keys.BATTERY_OPTIMIZATION] ?: false,
                workOnlyWhenCharging = preferences[Keys.WORK_ONLY_WHEN_CHARGING] ?: false
            )

            UserSettings(
                theme = AppTheme.valueOf(themeName),
                color = AppColor.valueOf(colorName),
                monitorSettings = monitorSettings
            )
        }
    }

    private fun getSensitivityThreshold(level: SensitivityLevel): Float {
        return when (level) {
            SensitivityLevel.VERY_LOW -> 0.6f
            SensitivityLevel.LOW -> 0.5f
            SensitivityLevel.MEDIUM -> 0.4f
            SensitivityLevel.HIGH -> 0.3f
            SensitivityLevel.VERY_HIGH -> 0.2f
        }
    }

    override suspend fun updateTheme(theme: AppTheme) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    override suspend fun updateColor(color: AppColor) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.COLOR] = color.name
        }
    }

    override suspend fun updateSensitivity(sensitivity: SensitivityLevel) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.SENSITIVITY] = sensitivity.name
        }
    }

    override suspend fun updateNotificationPriority(priority: NotificationPriority) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.NOTIFICATION_PRIORITY] = priority.name
        }
    }

    override suspend fun updateVibrationPattern(pattern: VibrationPattern) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.VIBRATION_PATTERN] = pattern.name
        }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.VIBRATION_ENABLED] = enabled
        }
    }

    override suspend fun updateVibrationIntensity(intensity: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.VIBRATION_INTENSITY] = intensity
        }
    }

    override suspend fun updateMicrophoneSensitivity(sensitivity: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.MICROPHONE_SENSITIVITY] = sensitivity
        }
    }

    override suspend fun updateShowConfidence(show: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.SHOW_CONFIDENCE] = show
        }
    }

    override suspend fun updateBypassDoNotDisturb(bypass: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.BYPASS_DND] = bypass
        }
    }

    override suspend fun updateShowOnLockScreen(show: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.SHOW_ON_LOCKSCREEN] = show
        }
    }

    override suspend fun updateEnableNotificationLights(enable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.ENABLE_NOTIFICATION_LIGHTS] = enable
        }
    }

    override suspend fun updateNoiseReduction(enable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.NOISE_REDUCTION] = enable
        }
    }

    override suspend fun updateMinAmplitudeThreshold(threshold: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.MIN_AMPLITUDE_THRESHOLD] = threshold
        }
    }

    override suspend fun updatePlaySound(play: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.PLAY_SOUND] = play
        }
    }

    override suspend fun updateSoundVolume(volume: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.SOUND_VOLUME] = volume
        }
    }

    override suspend fun updateOnlyAlertOnce(once: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.ONLY_ALERT_ONCE] = once
        }
    }

    override suspend fun updateBatteryOptimization(enable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.BATTERY_OPTIMIZATION] = enable
        }
    }

    override suspend fun updateWorkOnlyWhenCharging(enable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.WORK_ONLY_WHEN_CHARGING] = enable
        }
    }

    override suspend fun clearAllData() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
