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
import java.time.LocalDateTime
import java.util.UUID

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class RealMonitorRepository(
    private val context: Context,
    private val logger: Logger
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
    }

    override suspend fun setScanning(isScanning: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_SCANNING_KEY] = isScanning
        }
        // The collect block above will handle starting/stopping work
    }

    private fun startWork() {
        // Enqueue as a long-running foreground service worker
        val request = androidx.work.OneTimeWorkRequestBuilder<org.override.sense.feature.monitor.work.MonitorWorker>()
            .addTag("monitor_worker")
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
            
        // Use keep to avoid restarting if already running, BUT since we want a long running foreground service,
        // we might want to ensure it's alive.
        // Actually, for continuous monitoring, we should enqueue.
        workManager.enqueueUniqueWork(
            "monitor_worker",
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
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
