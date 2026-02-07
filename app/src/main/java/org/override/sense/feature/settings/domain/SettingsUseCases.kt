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

class ClearDataUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.clearAllData()
}

data class SettingsUseCases(
    val getSettings: GetSettingsUseCase,
    val updateTheme: UpdateThemeUseCase,
    val updateColor: UpdateColorUseCase,
    val clearData: ClearDataUseCase
)
