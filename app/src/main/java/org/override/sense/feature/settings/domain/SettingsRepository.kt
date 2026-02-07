package org.override.sense.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateColor(color: AppColor)
    suspend fun clearAllData()
}
