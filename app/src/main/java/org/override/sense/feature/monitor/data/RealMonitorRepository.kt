package org.override.sense.feature.monitor.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.override.sense.core.common.feedback.VibrationManager
import org.override.sense.core.common.logging.Logger
import org.override.sense.feature.monitor.domain.MonitorRepository
import org.override.sense.feature.monitor.domain.SoundEvent
import org.override.sense.feature.settings.domain.SettingsRepository
import java.time.LocalDateTime
import java.util.UUID

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class RealMonitorRepository(
    private val context: Context,
    private val logger: Logger,
    private val settingsRepository: SettingsRepository
) : MonitorRepository {

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: Flow<Boolean> = _isScanning

    private val _detectedSounds = MutableStateFlow<SoundEvent?>(null)
    override val detectedSounds: Flow<SoundEvent?> = _detectedSounds

    private val _history = MutableStateFlow<List<SoundEvent>>(emptyList())
    override val recentHistory: Flow<List<SoundEvent>> = _history

    // We'll use WorkManager instead of direct coroutines
    private val workManager = androidx.work.WorkManager.getInstance(context)

    // DataStore for persistence
    private val Context.dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> by androidx.datastore.preferences.preferencesDataStore(name = "monitor_prefs")
    private val IS_SCANNING_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("is_scanning")

    init {
        // Restore state on init
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            context.dataStore.data.map { it[IS_SCANNING_KEY] ?: false }
                .collect { savedIsScanning ->
                    _isScanning.value = savedIsScanning
                    if (savedIsScanning) {
                        startWork()
                    } else {
                        stopWork()
                    }
                }
        }
        
        // Observe settings changes and restart worker if needed
        scope.launch {
            var previousSettings = settingsRepository.getSettings().first().monitorSettings
            settingsRepository.getSettings().collect { userSettings ->
                val currentSettings = userSettings.monitorSettings
                
                // Check if we need to restart the worker due to significant settings changes
                val needsRestart = _isScanning.value && (
                    // Detection settings
                    previousSettings.sensitivity != currentSettings.sensitivity ||
                    previousSettings.microphoneSensitivity != currentSettings.microphoneSensitivity ||
                    previousSettings.minAmplitudeThreshold != currentSettings.minAmplitudeThreshold ||
                    previousSettings.noiseReduction != currentSettings.noiseReduction ||
                    // Battery settings (affects WorkManager constraints)
                    previousSettings.batteryOptimization != currentSettings.batteryOptimization ||
                    previousSettings.workOnlyWhenCharging != currentSettings.workOnlyWhenCharging
                )
                
                if (needsRestart) {
                    logger.d("RealMonitorRepository", "Settings changed, restarting worker")
                    stopWork()
                    // Small delay to ensure worker is stopped
                    kotlinx.coroutines.delay(500)
                    startWork()
                }
                
                previousSettings = currentSettings
            }
        }
    }

    override suspend fun setScanning(isScanning: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_SCANNING_KEY] = isScanning
        }
        // The collect block above will handle starting/stopping work
    }

    private fun startWork() {
        // Get current settings to apply battery constraints
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val settings = settingsRepository.getSettings().first().monitorSettings
            
            // Build constraints based on battery settings
            val constraintsBuilder = androidx.work.Constraints.Builder()
            
            if (settings.batteryOptimization) {
                // Only run when battery is not low
                constraintsBuilder.setRequiresBatteryNotLow(true)
            } else {
                constraintsBuilder.setRequiresBatteryNotLow(false)
            }
            
            if (settings.workOnlyWhenCharging) {
                // Only run when charging
                constraintsBuilder.setRequiresCharging(true)
            }
            
            val constraints = constraintsBuilder.build()
                
            val request = androidx.work.OneTimeWorkRequestBuilder<org.override.sense.feature.monitor.work.MonitorWorker>()
                .setConstraints(constraints)
                .addTag("monitor_worker")
                .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
                
            // REPLACE policy to apply new settings
            workManager.enqueueUniqueWork(
                "monitor_worker",
                androidx.work.ExistingWorkPolicy.REPLACE,
                request
            )
            
            logger.d("RealMonitorRepository", "Started monitoring with battery optimization: ${settings.batteryOptimization}, charging only: ${settings.workOnlyWhenCharging}")
        }
    }

    private fun stopWork() {
        workManager.cancelUniqueWork("monitor_worker")
    }

    // Called by Worker
    fun emitEventFromWorker(event: SoundEvent) {
        _detectedSounds.value = event
        updateHistory(event)
    }

    private fun updateHistory(event: SoundEvent) {
        val currentList = _history.value.toMutableList()
        currentList.add(0, event)
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }
        _history.value = currentList
    }
}
