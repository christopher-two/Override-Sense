package org.override.sense.feature.settings.presentation

import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppLanguage
import org.override.sense.feature.settings.domain.AppTheme

sealed interface SettingsAction {
    data class UpdateTheme(val theme: AppTheme) : SettingsAction
    data class UpdateLanguage(val language: AppLanguage) : SettingsAction
    data class UpdateColor(val color: AppColor) : SettingsAction
    data object ClearData : SettingsAction
}